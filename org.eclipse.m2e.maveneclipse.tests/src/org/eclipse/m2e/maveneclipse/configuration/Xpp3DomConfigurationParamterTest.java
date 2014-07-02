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

import java.util.List;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link Xpp3DomConfigurationParameter}.
 *
 * @author Alex Clarke
 * @author Phillip Webb
 */
public class Xpp3DomConfigurationParamterTest {

	@Mock
	private Xpp3Dom dom;

	private Xpp3DomConfigurationParameter parameter;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.parameter = new Xpp3DomConfigurationParameter(this.dom);
	}

	@Test
	public void shouldGetName() throws Exception {
		String expected = "name";
		given(this.dom.getName()).willReturn(expected);
		String actual = this.parameter.getName();
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void shouldGetChildren() throws Exception {
		Xpp3Dom c1 = mockDom("c1");
		Xpp3Dom c2 = mockDom("c2");
		given(this.dom.getChildren()).willReturn(new Xpp3Dom[] { c1, c2 });
		List<ConfigurationParameter> children = this.parameter.getChildren();
		assertThat(children.size(), equalTo(2));
		assertThat(children.get(0).getName(), equalTo("c1"));
		assertThat(children.get(1).getName(), equalTo("c2"));
	}

	@Test
	public void shouldHaveChildByName() throws Exception {
		Xpp3Dom child = mockDom("found");
		given(this.dom.getChild("found")).willReturn(child);
		assertThat(this.parameter.hasChild("missing"), equalTo(false));
		assertThat(this.parameter.hasChild("found"), equalTo(true));
	}

	@Test
	public void shouldGetChild() throws Exception {
		Xpp3Dom child = mockDom("child");
		given(this.dom.getChild("child")).willReturn(child);
		ConfigurationParameter actual = this.parameter.getChild("child");
		assertThat(actual.getName(), equalTo("child"));
	}

	@Test
	public void shouldGetNullChildWhenMissing() throws Exception {
		given(this.dom.getChild("child")).willReturn(null);
		ConfigurationParameter actual = this.parameter.getChild("child");
		assertThat(actual, nullValue());
	}

	@Test
	public void shouldGetValue() throws Exception {
		given(this.dom.getValue()).willReturn("value");
		assertThat(this.parameter.getValue(), equalTo("value"));
	}

	private Xpp3Dom mockDom(String name) {
		Xpp3Dom dom = mock(Xpp3Dom.class);
		given(dom.getName()).willReturn(name);
		return dom;
	}

}
