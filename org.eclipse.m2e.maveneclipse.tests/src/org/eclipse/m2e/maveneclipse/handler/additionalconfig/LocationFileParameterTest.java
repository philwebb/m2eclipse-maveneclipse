package org.eclipse.m2e.maveneclipse.handler.additionalconfig;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.eclipse.m2e.maveneclipse.MavenEclipseContext;
import org.eclipse.m2e.maveneclipse.configuration.ConfigurationParameter;
import org.junit.Test;

/**
 * Tests for {@link LocationFileParameter}.
 *
 * @author Raphael von der Grün
 */
public class LocationFileParameterTest {
	
	private static final String THIS_DIRECTORY = "./src/org/eclipse/m2e/maveneclipse/handler/additionalconfig";

	@Test
	// This test only makes sense when run under windows
	public void shouldHandleAbsouluteWindowsPaths() throws Exception {
		ConfigurationParameter fileParam = mock(ConfigurationParameter.class);
		MavenEclipseContext context = mock(MavenEclipseContext.class);
		File locationRoot = new File(THIS_DIRECTORY).getAbsoluteFile();
		
		when(fileParam.getChild(LocationFileParameter.CHILD_NAME)).thenReturn(fileParam);
		when(fileParam.getValue()).thenReturn("content.txt");
		
		LocationFileParameter param = new LocationFileParameter(context, fileParam, locationRoot);
		assertNotNull(param.getContent());
	}

}
