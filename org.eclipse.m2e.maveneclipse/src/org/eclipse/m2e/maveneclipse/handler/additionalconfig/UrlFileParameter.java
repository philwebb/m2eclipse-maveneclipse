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

import java.io.InputStream;
import java.net.URL;

import org.eclipse.m2e.maveneclipse.MavenEclipseContext;
import org.eclipse.m2e.maveneclipse.configuration.ConfigurationParameter;

/**
 * {@link FileParameter} that loads content from a URL.
 */
class UrlFileParameter extends FileParameter {

	public static final String CHILD_NAME = "url";

	public UrlFileParameter(MavenEclipseContext context,
			ConfigurationParameter fileParameter) {
		super(context, fileParameter);
	}

	@Override
	protected String getChildName() {
		return CHILD_NAME;
	}

	@Override
	protected InputStream getContent() throws Exception {
		String url = getChildValue();
		return new URL(url).openStream();
	}

}