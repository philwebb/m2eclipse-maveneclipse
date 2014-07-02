/*
 * Copyright 2000-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.m2e.maveneclipse.handler.additionalbuildcommands;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.maveneclipse.MavenEclipseContext;
import org.eclipse.m2e.maveneclipse.configuration.ConfigurationParameter;
import org.eclipse.m2e.maveneclipse.configuration.MavenEclipseConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link AdditionalBuildCommandsConfigurationHandler}.
 *
 * @author Alex Clarke
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class AdditionalBuildCommandsConfigurationHandlerTest {

	@InjectMocks
	private final AdditionalBuildCommandsConfigurationHandler additionalBuildCommandsConfigurationHandler = new AdditionalBuildCommandsConfigurationHandler();

	@Mock
	private BuildCommandFactory commandFactory;

	@Mock
	private final MavenEclipseContext context = mock(MavenEclipseContext.class);

	@Mock
	private final IProjectDescription projectDescription = mock(IProjectDescription.class);

	@Mock
	private final IProject project = mock(IProject.class);

	@Mock
	private final ICommand initialBuildCommand = mock(ICommand.class);

	@Mock
	private final IProgressMonitor monitor = mock(IProgressMonitor.class);

	@Captor
	ArgumentCaptor<ICommand[]> argument;

	@Before
	public void setupBasicContextWithInitialBuildCommand() throws Exception {
		given(this.context.getProject()).willReturn(this.project);
		given(this.project.getDescription()).willReturn(this.projectDescription);

		given(this.initialBuildCommand.getBuilderName()).willReturn("initial");
		ICommand[] initialBuildSpecs = { this.initialBuildCommand };
		given(this.projectDescription.getBuildSpec()).willReturn(initialBuildSpecs);
		given(this.context.getMonitor()).willReturn(this.monitor);
	}

	@Test
	public void shouldAddAdditionalBuildCommands() throws Exception {
		// Given
		ConfigurationParameter additionalBuildCommandParameter = mock(ConfigurationParameter.class);
		List<ConfigurationParameter> buildCommandParameters = new ArrayList<ConfigurationParameter>();
		given(additionalBuildCommandParameter.getChildren()).willReturn(
				buildCommandParameters);

		ConfigurationParameter firstBuildCommandParameter = mock(ConfigurationParameter.class);
		buildCommandParameters.add(firstBuildCommandParameter);
		ICommand iCommandForFirst = mock(ICommand.class);
		given(iCommandForFirst.getBuilderName()).willReturn("first");
		given(
				this.commandFactory.createCommand(this.projectDescription,
						firstBuildCommandParameter)).willReturn(iCommandForFirst);

		ConfigurationParameter secondBuildCommandParameter = mock(ConfigurationParameter.class);
		buildCommandParameters.add(secondBuildCommandParameter);
		ICommand iCommandForSecond = mock(ICommand.class);
		given(iCommandForSecond.getBuilderName()).willReturn("second");
		given(
				this.commandFactory.createCommand(this.projectDescription,
						secondBuildCommandParameter)).willReturn(iCommandForSecond);

		// When
		this.additionalBuildCommandsConfigurationHandler.handle(this.context,
				additionalBuildCommandParameter);

		// Then
		verify(this.projectDescription).setBuildSpec(this.argument.capture());

		List<ICommand> actualBuildSpec = Arrays.asList(this.argument.getValue());
		assertThat(actualBuildSpec.size(), equalTo(3));
		assertTrue(actualBuildSpec.contains(this.initialBuildCommand));
		assertTrue(actualBuildSpec.contains(iCommandForFirst));
		assertTrue(actualBuildSpec.contains(iCommandForSecond));

		verify(this.project).setDescription(this.projectDescription, this.monitor);
	}

	@Test
	public void shouldClaimToHandleContextsWithAdditionalProjectNaturesParamater() {
		// Given
		MavenEclipseContext context = mock(MavenEclipseContext.class);
		MavenEclipseConfiguration configuration = mock(MavenEclipseConfiguration.class);
		given(context.getPluginConfiguration()).willReturn(configuration);
		given(
				configuration
						.containsParamter(this.additionalBuildCommandsConfigurationHandler
								.getParamterName())).willReturn(true);

		// When
		boolean canHandle = this.additionalBuildCommandsConfigurationHandler
				.canHandle(context);

		assertThat(canHandle, equalTo(true));
	}

	@Test
	public void shouldNotClaimToHandleContextsWithoutAdditionalProjectNaturesParamater() {
		// Given
		MavenEclipseContext context = mock(MavenEclipseContext.class);
		MavenEclipseConfiguration configuration = mock(MavenEclipseConfiguration.class);
		given(context.getPluginConfiguration()).willReturn(configuration);
		given(
				configuration
						.getParamter(this.additionalBuildCommandsConfigurationHandler
								.getParamterName())).willReturn(null);

		// When
		boolean canHandle = this.additionalBuildCommandsConfigurationHandler
				.canHandle(context);

		assertThat(canHandle, equalTo(false));
	}

}
