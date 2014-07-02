/*
 * Copyright 2000-2014 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.m2e.maveneclipse.handler.additionalprojectnatures;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.maveneclipse.MavenEclipseContext;
import org.eclipse.m2e.maveneclipse.configuration.ConfigurationParameter;
import org.eclipse.m2e.maveneclipse.configuration.MavenEclipseConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link AdditionalProjectNaturesConfigurationHandler}.
 *
 * @author Alex Clarke
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class AdditionalProjectNaturesConfigurationHandlerTest {

	private static final String FIRST_PROJECT_NATURE = "first";
	private static final String SECOND_PROJECT_NATURE = "second";
	private static final String MISSING_PROJECT_NATURE = "missing";

	private static final String INITIAL_NATURE = "initial";

	private final AdditionalProjectNaturesConfigurationHandler additionalProjectNaturesConfigurationHandler = new AdditionalProjectNaturesConfigurationHandler() {
		@Override
		protected org.eclipse.core.runtime.IExtensionRegistry getExtensionRegistry() {
			return AdditionalProjectNaturesConfigurationHandlerTest.this.extensionRegistry;
		};
	};

	@Mock
	private MavenEclipseContext context;

	@Mock
	private IProjectDescription projectDescription;

	@Mock
	private IProject project;

	@Mock
	private IProgressMonitor monitor;

	@Captor
	private ArgumentCaptor<String[]> argument;

	@Mock
	private IExtensionRegistry extensionRegistry;

	@Mock
	private IExtension extension;

	@Before
	public void setupBasicContextWithInitialNature() throws Exception {
		given(this.context.getProject()).willReturn(this.project);

		given(this.project.getDescription()).willReturn(this.projectDescription);
		String[] initialNatureIds = { INITIAL_NATURE };
		given(this.projectDescription.getNatureIds()).willReturn(initialNatureIds);

		given(this.context.getMonitor()).willReturn(this.monitor);
		given(
				this.extensionRegistry.getExtension(ResourcesPlugin.PI_RESOURCES,
						ResourcesPlugin.PT_NATURES, FIRST_PROJECT_NATURE)).willReturn(
				this.extension);
		given(
				this.extensionRegistry.getExtension(ResourcesPlugin.PI_RESOURCES,
						ResourcesPlugin.PT_NATURES, SECOND_PROJECT_NATURE)).willReturn(
				this.extension);
	}

	@Test
	public void shouldAddAdditionalProjectNaturesFilteringMissing() throws Exception {
		// Given
		MavenEclipseConfiguration mavenEclipseConfiguration = mock(MavenEclipseConfiguration.class);
		given(this.context.getPluginConfiguration())
				.willReturn(mavenEclipseConfiguration);
		ConfigurationParameter configurationParameter = mock(ConfigurationParameter.class);
		given(
				mavenEclipseConfiguration
						.getParamter(this.additionalProjectNaturesConfigurationHandler
								.getParamterName())).willReturn(configurationParameter);
		given(
				mavenEclipseConfiguration
						.containsParamter(this.additionalProjectNaturesConfigurationHandler
								.getParamterName())).willReturn(true);

		List<ConfigurationParameter> projectNatureConfigurationParameters = new ArrayList<ConfigurationParameter>();
		ConfigurationParameter firstProjectNature = createProjectNatureConfigParameter(FIRST_PROJECT_NATURE);
		ConfigurationParameter secondProjectNature = createProjectNatureConfigParameter(SECOND_PROJECT_NATURE);
		ConfigurationParameter missingProjectNature = createProjectNatureConfigParameter(MISSING_PROJECT_NATURE);
		projectNatureConfigurationParameters.add(firstProjectNature);
		projectNatureConfigurationParameters.add(secondProjectNature);
		projectNatureConfigurationParameters.add(missingProjectNature);
		given(configurationParameter.getChildren()).willReturn(
				projectNatureConfigurationParameters);

		// When
		this.additionalProjectNaturesConfigurationHandler.handle(this.context);

		// Then
		verify(this.projectDescription).setNatureIds(this.argument.capture());
		verify(this.project).setDescription(this.projectDescription, this.monitor);

		List<String> newNatureIds = Arrays.asList(this.argument.getValue());
		assertThat(newNatureIds.size(), equalTo(3));
		assertTrue(newNatureIds.contains(INITIAL_NATURE));
		assertTrue(newNatureIds.contains(FIRST_PROJECT_NATURE));
		assertTrue(newNatureIds.contains(SECOND_PROJECT_NATURE));
	}

	@Test
	public void shouldNotChangeProjectDescriptionWhenNoAdditionalProjectNaturesConfigured()
			throws Exception {
		// Given
		MavenEclipseConfiguration mavenEclipseConfiguration = mock(MavenEclipseConfiguration.class);
		given(this.context.getPluginConfiguration())
				.willReturn(mavenEclipseConfiguration);
		ConfigurationParameter configurationParameter = mock(ConfigurationParameter.class);
		given(
				mavenEclipseConfiguration
						.getParamter(this.additionalProjectNaturesConfigurationHandler
								.getParamterName())).willReturn(configurationParameter);
		given(
				mavenEclipseConfiguration
						.containsParamter(this.additionalProjectNaturesConfigurationHandler
								.getParamterName())).willReturn(true);

		given(configurationParameter.getChildren()).willReturn(
				new ArrayList<ConfigurationParameter>());

		// When
		this.additionalProjectNaturesConfigurationHandler.handle(this.context);

		// Then
		verify(this.project, never()).setDescription(any(IProjectDescription.class),
				eq(this.monitor));

	}

	@Test
	public void shouldClaimToHandleContextsWithAdditionalProjectNaturesParamater() {
		// Given
		MavenEclipseContext context = mock(MavenEclipseContext.class);
		MavenEclipseConfiguration configuration = mock(MavenEclipseConfiguration.class);
		given(context.getPluginConfiguration()).willReturn(configuration);
		given(
				configuration
						.containsParamter(this.additionalProjectNaturesConfigurationHandler
								.getParamterName())).willReturn(true);

		// When
		boolean canHandle = this.additionalProjectNaturesConfigurationHandler
				.canHandle(context);

		assertTrue(canHandle);
	}

	@Test
	public void shouldNotClaimToHandleContextsWithoutAdditionalProjectNaturesParamater() {
		// Given
		MavenEclipseContext context = mock(MavenEclipseContext.class);
		MavenEclipseConfiguration configuration = mock(MavenEclipseConfiguration.class);
		given(context.getPluginConfiguration()).willReturn(configuration);
		given(
				configuration
						.getParamter(this.additionalProjectNaturesConfigurationHandler
								.getParamterName())).willReturn(null);

		// When
		boolean canHandle = this.additionalProjectNaturesConfigurationHandler
				.canHandle(context);

		assertFalse(canHandle);
	}

	private ConfigurationParameter createProjectNatureConfigParameter(String natureId) {
		ConfigurationParameter firstProjectNature = mock(ConfigurationParameter.class);
		given(firstProjectNature.getName()).willReturn(
				AdditionalProjectNaturesConfigurationHandler.PROJECT_NATURE_NAME);
		given(firstProjectNature.getValue()).willReturn(natureId);
		return firstProjectNature;
	}
}
