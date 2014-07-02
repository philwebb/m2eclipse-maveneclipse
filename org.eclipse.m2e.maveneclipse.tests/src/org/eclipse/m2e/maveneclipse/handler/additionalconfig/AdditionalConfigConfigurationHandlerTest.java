/*
 * Copyright 2000-2014 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.m2e.maveneclipse.handler.additionalconfig;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.maveneclipse.MavenEclipseContext;
import org.eclipse.m2e.maveneclipse.configuration.ConfigurationParameter;
import org.eclipse.m2e.maveneclipse.configuration.MavenEclipseConfiguration;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Tests for {@link AdditionalConfigConfigurationHandler}.
 *
 * @author Alex Clarke
 * @author Phillip Webb
 */
public class AdditionalConfigConfigurationHandlerTest {

	private final AdditionalConfigConfigurationHandler handler = new AdditionalConfigConfigurationHandler();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private MavenEclipseContext context;

	@Mock
	private ConfigurationParameter additionalConfigParameter;

	private final File location = new File(
			"./src/org/eclipse/m2e/maveneclipse/handler/additionalconfig");

	@Mock
	private IFile projectFile;

	@Mock
	private IProgressMonitor monitor;

	private String writtenContent;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		MavenProject mavenProject = mock(MavenProject.class);
		File file = mock(File.class);
		IProject project = mock(IProject.class);
		MavenEclipseConfiguration configuration = mock(MavenEclipseConfiguration.class);
		given(this.context.getMavenProject()).willReturn(mavenProject);
		given(mavenProject.getFile()).willReturn(file);
		given(file.getParentFile()).willReturn(this.location);
		given(this.context.getPluginConfiguration()).willReturn(configuration);
		given(configuration.containsParamter("additionalConfig")).willReturn(true);
		given(configuration.getParamter("additionalConfig")).willReturn(
				this.additionalConfigParameter);
		given(this.context.getProject()).willReturn(project);
		given(project.getFile(anyString())).willReturn(this.projectFile);
		given(this.context.getMonitor()).willReturn(this.monitor);
		willAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				InputStream inputStream = (InputStream) invocation.getArguments()[0];
				AdditionalConfigConfigurationHandlerTest.this.writtenContent = IOUtil
						.toString(inputStream);
				return null;
			}
		}).given(this.projectFile).create(isA(InputStream.class), eq(true),
				eq(this.monitor));
	}

	@Test
	public void shouldNeedContentLocationOrUrl() throws Exception {
		givenSingleChild("name", "unknown", "value");
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Malformed additionalConfig file paramter");
		this.handler.handle(this.context);
	}

	@Test
	public void shouldCopyContent() throws Exception {
		givenSingleChild("name", "content", "value");
		this.handler.handle(this.context);
		assertThatFileContent(equalTo("value"));
	}

	@Test
	public void shouldCopyLocation() throws Exception {
		givenSingleChild("name", "location", "content.txt");
		this.handler.handle(this.context);
		assertThatFileContent(equalTo("content"));
	}

	@Test
	public void shouldCopyURL() throws Exception {
		URL url = getClass().getResource("content.txt");
		givenSingleChild("name", "url", url.toString());
		this.handler.handle(this.context);
		assertThatFileContent(equalTo("content"));
	}

	private void givenSingleChild(String name, String type, String value) {
		List<ConfigurationParameter> children = Collections.singletonList(childParameter(
				name, type, value));
		given(this.additionalConfigParameter.getChildren()).willReturn(children);
	}

	private ConfigurationParameter childParameter(String name, String type, String value) {
		ConfigurationParameter childParameter = mock(ConfigurationParameter.class);
		ConfigurationParameter nameParameter = mock(ConfigurationParameter.class);
		ConfigurationParameter contentParameter = mock(ConfigurationParameter.class);
		given(childParameter.getName()).willReturn("file");
		given(childParameter.getChild("name")).willReturn(nameParameter);
		given(nameParameter.getValue()).willReturn(name);
		given(childParameter.hasChild(type)).willReturn(true);
		given(childParameter.getChild(type)).willReturn(contentParameter);
		given(contentParameter.getValue()).willReturn(value);
		return childParameter;
	}

	private void assertThatFileContent(Matcher<String> matcher) throws Exception {
		verify(this.projectFile).create(isA(InputStream.class), eq(true),
				eq(this.monitor));
		assertThat(this.writtenContent, matcher);
	}

}
