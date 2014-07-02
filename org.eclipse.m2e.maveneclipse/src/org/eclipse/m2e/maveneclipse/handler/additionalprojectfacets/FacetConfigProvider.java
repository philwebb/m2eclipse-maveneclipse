/*
 * Copyright 2000-2014 the original author or authors.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.m2e.maveneclipse.handler.additionalprojectfacets;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.maveneclipse.MavenEclipseContext;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Strategy interface used to provide additional config for project facets.
 *
 * @author Phillip Webb
 */
public interface FacetConfigProvider {

	/**
	 * Prepare the project before any configuration occurs.
	 * @param context the context
	 * @throws CoreException
	 */
	void prepare(MavenEclipseContext context) throws CoreException;

	/**
	 * Returns the config for the project facet version.
	 * @param context the context
	 * @param projectFacetVersion the project facet version
	 * @return the config
	 * @throws CoreException
	 */
	Object getFacetConfig(MavenEclipseContext context,
			IProjectFacetVersion projectFacetVersion) throws CoreException;

}
