/*
 * Copyright 2000-2014 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.m2e.maveneclipse.configuration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link DefaultMavenEclipseConfiguration}.
 *
 * @author Alex Clarke
 * @author Phillip Webb
 */
public class DefaultMavenEclipseConfigurationTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private Xpp3Dom dom;

	@Mock
	private Plugin plugin;

	private DefaultMavenEclipseConfiguration configuration;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		given(this.plugin.getConfiguration()).willReturn(this.dom);
		this.configuration = new DefaultMavenEclipseConfiguration(this.plugin);
	}

	@Test
	public void shouldGetParamter() throws Exception {
		Xpp3Dom child = mock(Xpp3Dom.class);
		given(child.getName()).willReturn("name");
		given(this.dom.getChildren("p")).willReturn(new Xpp3Dom[] { child });
		ConfigurationParameter paramter = this.configuration.getParamter("p");
		assertThat(paramter.getName(), equalTo("name"));
	}

	@Test
	public void shouldReturnNullParamter() throws Exception {
		given(this.dom.getChildren("p")).willReturn(new Xpp3Dom[] {});
		ConfigurationParameter paramter = this.configuration.getParamter("p");
		assertThat(paramter, nullValue());
	}

	@Test
	public void shouldNotSupportMultipleParamtersOfSameName() throws Exception {
		Xpp3Dom c1 = mock(Xpp3Dom.class);
		Xpp3Dom c2 = mock(Xpp3Dom.class);
		given(this.dom.getChildren("p")).willReturn(new Xpp3Dom[] { c1, c2 });
		this.thrown.expect(IllegalStateException.class);
		this.thrown
				.expectMessage("Unexpected number of child parameters defined for 'p'");
		this.configuration.getParamter("p");
	}

}
