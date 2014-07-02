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

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.maveneclipse.MavenEclipseContext;
import org.eclipse.m2e.maveneclipse.configuration.ConfigurationParameter;

/**
 * Abstract base class of an additional config file.
 * @see ContentFileParameter
 * @see LocationFileParameter
 * @see UrlFileParameter
 */
abstract class FileParameter {

	private final MavenEclipseContext context;

	private final ConfigurationParameter fileParameter;

	public FileParameter(MavenEclipseContext context, ConfigurationParameter fileParameter) {
		this.context = context;
		this.fileParameter = fileParameter;
	}

	/**
	 * @return the name of the child element that this paramter supports.
	 */
	protected abstract String getChildName();

	/**
	 * @return the child value
	 */
	protected final String getChildValue() {
		return this.fileParameter.getChild(getChildName()).getValue();
	}

	/**
	 * Copy this files to the project.
	 * @throws Exception
	 */
	public void copyToProject() throws Exception {
		String name = this.fileParameter.getChild("name").getValue();
		IFile projectFile = this.context.getProject().getFile(name);
		if (projectFile.exists()) {
			try {
				projectFile.delete(true, this.context.getMonitor());
			}
			catch (CoreException ex) {
			}
		}
		InputStream content = getContent();
		try {
			projectFile.create(new BufferedInputStream(content), true,
					this.context.getMonitor());
		}
		finally {
			content.close();
		}
	}

	/**
	 * Returns an input stream containing file content.
	 * @return the files content
	 * @throws Exception
	 */
	protected abstract InputStream getContent() throws Exception;

}