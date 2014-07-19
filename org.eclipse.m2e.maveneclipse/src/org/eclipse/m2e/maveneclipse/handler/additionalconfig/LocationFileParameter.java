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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.m2e.maveneclipse.MavenEclipseContext;
import org.eclipse.m2e.maveneclipse.configuration.ConfigurationParameter;

/**
 * {@link FileParameter} that loads content from a project location.
 */
class LocationFileParameter extends FileParameter {

	public static final String CHILD_NAME = "location";

	private final File locationRoot;

	public LocationFileParameter(MavenEclipseContext context,
			ConfigurationParameter fileParameter, File locationRoot) {
		super(context, fileParameter);
		this.locationRoot = locationRoot;
	}

	@Override
	protected String getChildName() {
		return CHILD_NAME;
	}

	@Override
	protected InputStream getContent() throws Exception {
		String location = getChildValue();
		File locationFile = new File(location);
		if (!locationFile.isAbsolute()) {
			locationFile = new File(this.locationRoot, location);
		}
		if (!locationFile.exists()) {
			throw new IllegalStateException("Location file "
					+ locationFile.getAbsolutePath() + " does not exist");
		}
		return new FileInputStream(locationFile);
	}

}