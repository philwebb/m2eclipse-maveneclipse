package org.eclipse.m2e.maveneclipse.handler.additionalconfig;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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
	public void shouldHandleAbsouluteWindowsPaths() throws Exception {
		// This test only makes sense when run under windows
		ConfigurationParameter fileParam = mock(ConfigurationParameter.class);
		MavenEclipseContext context = mock(MavenEclipseContext.class);
		File locationRoot = new File(THIS_DIRECTORY).getAbsoluteFile();
		given(fileParam.getChild(LocationFileParameter.CHILD_NAME)).willReturn(fileParam);
		given(fileParam.getValue()).willReturn("content.txt");
		LocationFileParameter param = new LocationFileParameter(context, fileParam, locationRoot);
		assertNotNull(param.getContent());
	}

}
