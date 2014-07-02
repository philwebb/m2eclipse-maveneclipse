/*
 * Copyright 2000-2014 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.m2e.maveneclipse;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.maveneclipse.handler.ConfigurationHandlers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link MavenEclipseProjectConfigurator}.
 *
 * @author Alex Clarke
 * @author Phillip Webb
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class MavenEclipseProjectConfiguratorTest {

	@InjectMocks
	private final MavenEclipseProjectConfigurator mavenEclipseProjectConfigurator = new MavenEclipseProjectConfigurator();

	@Mock
	private ConfigurationHandlers configurationHandlers;

	@Test
	public void shouldConfigureProjectWhenPluginAvailable() throws Exception {
		// Given
		ProjectConfigurationRequest request = mock(ProjectConfigurationRequest.class);
		IProgressMonitor monitor = mock(IProgressMonitor.class);
		MavenProject mavenProject = mock(MavenProject.class);
		given(request.getMavenProject()).willReturn(mavenProject);

		List<Plugin> plugins = new ArrayList<Plugin>();
		Plugin mavenEclipsePlugin = mock(Plugin.class);
		plugins.add(mavenEclipsePlugin);
		given(mavenProject.getBuildPlugins()).willReturn(plugins);

		given(mavenEclipsePlugin.getArtifactId()).willReturn(
				MavenEclipseProjectConfigurator.ARTIFACT_ID);
		given(mavenEclipsePlugin.getGroupId()).willReturn(
				MavenEclipseProjectConfigurator.GROUP_ID);

		// When
		this.mavenEclipseProjectConfigurator
				.configureRawClasspath(request, null, monitor);

		// Then
		verify(this.configurationHandlers).handle(any(MavenEclipseContext.class));
	}

	@Test
	public void shouldNotConfigureProjectWhenPluginUnavailable() throws Exception {
		// Given
		ProjectConfigurationRequest request = mock(ProjectConfigurationRequest.class);
		IProgressMonitor monitor = mock(IProgressMonitor.class);
		MavenProject mavenProject = mock(MavenProject.class);
		given(request.getMavenProject()).willReturn(mavenProject);

		List<Plugin> plugins = new ArrayList<Plugin>();
		given(mavenProject.getBuildPlugins()).willReturn(plugins);

		// When
		this.mavenEclipseProjectConfigurator
				.configureRawClasspath(request, null, monitor);

		// Then
		verifyZeroInteractions(this.configurationHandlers);
	}

}
