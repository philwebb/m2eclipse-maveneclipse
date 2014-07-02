/*
 * Copyright 2000-2014 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.m2e.maveneclipse.handler.additionalbuildcommands;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.m2e.maveneclipse.configuration.ConfigurationParameter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link BuildCommandFactoryImpl}.
 *
 * @author Alex Clarke
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class BuildCommandFactoryImplTest {

	private final BuildCommandFactoryImpl factory = new BuildCommandFactoryImpl();

	@Captor
	private ArgumentCaptor<Map<String, String>> argument;

	@Test
	public void shouldCreateICommandFromNamedConfigurationParameter() {
		// Given
		IProjectDescription projectDescription = mock(IProjectDescription.class);

		ConfigurationParameter buildCommandConfigParameter = mock(ConfigurationParameter.class);
		String buildCommandName = "my.build.command";
		given(buildCommandConfigParameter.getValue()).willReturn(buildCommandName);
		given(buildCommandConfigParameter.getName()).willReturn(
				BuildCommandFactoryImpl.LEGACY_BUILD_COMMAND_ELEMENT_NAME);

		ICommand newCommand = mock(ICommand.class);
		given(projectDescription.newCommand()).willReturn(newCommand);

		// When
		ICommand createdICommand = this.factory.createCommand(projectDescription,
				buildCommandConfigParameter);

		// Then
		assertSame(newCommand, createdICommand);
		verify(newCommand).setBuilderName(buildCommandName);
	}

	@Test
	public void shouldCreateICommandFromCompleteConfigurationParameter() {
		// Given
		IProjectDescription projectDescription = mock(IProjectDescription.class);

		ConfigurationParameter buildCommandConfigParameter = mock(ConfigurationParameter.class);
		given(buildCommandConfigParameter.getName()).willReturn(
				BuildCommandFactoryImpl.BUILD_COMMAND_ELEMENT_NAME);

		ConfigurationParameter nameConfigurationParameter = mock(ConfigurationParameter.class);
		given(
				buildCommandConfigParameter
						.getChild(BuildCommandFactoryImpl.NAME_ELEMENT_NAME)).willReturn(
				nameConfigurationParameter);
		String buildCommandName = "my.build.command";
		given(nameConfigurationParameter.getValue()).willReturn(buildCommandName);

		String argumentName = "test-arg";
		String argumentValue = "test-value";
		ConfigurationParameter argumentsParameter = createArgumentConfigurationParameter(
				argumentName, argumentValue);
		given(
				buildCommandConfigParameter
						.getChild(BuildCommandFactoryImpl.ARGUMENTS_ELEMENT_NAME))
				.willReturn(argumentsParameter);

		ICommand newCommand = mock(ICommand.class);
		given(projectDescription.newCommand()).willReturn(newCommand);

		// When
		ICommand createdICommand = this.factory.createCommand(projectDescription,
				buildCommandConfigParameter);

		// Then
		assertSame(newCommand, createdICommand);
		verify(newCommand).setBuilderName(buildCommandName);

		verify(newCommand).setArguments(this.argument.capture());
		Map<String, String> actualArguments = this.argument.getValue();
		assertThat(actualArguments.keySet().size(), equalTo(1));
		String firstKey = actualArguments.keySet().iterator().next();
		assertThat(firstKey, equalTo(argumentName));
		assertThat(actualArguments.get(firstKey), equalTo(argumentValue));

	}

	@Test
	public void shouldIgnoreCreateICommandFromNonBuildCommandConfigurationParameter() {
		// Given
		IProjectDescription projectDescription = mock(IProjectDescription.class);
		ConfigurationParameter buildCommandConfigParameter = mock(ConfigurationParameter.class);

		// When
		ICommand actualICommand = this.factory.createCommand(projectDescription,
				buildCommandConfigParameter);

		// Then
		verifyZeroInteractions(projectDescription);
		assertNull(actualICommand);
	}

	private ConfigurationParameter createArgumentConfigurationParameter(
			String argumentName, String argumentValue) {
		ConfigurationParameter argumentsParameter = mock(ConfigurationParameter.class);
		List<ConfigurationParameter> arguments = new ArrayList<ConfigurationParameter>();
		ConfigurationParameter argumentParameter = mock(ConfigurationParameter.class);
		given(argumentParameter.getName()).willReturn(argumentName);
		given(argumentParameter.getValue()).willReturn(argumentValue);
		arguments.add(argumentParameter);
		given(argumentsParameter.getChildren()).willReturn(arguments);
		return argumentsParameter;
	}

}
