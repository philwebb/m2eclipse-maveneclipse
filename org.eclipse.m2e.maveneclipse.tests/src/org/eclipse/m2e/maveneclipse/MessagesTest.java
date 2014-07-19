package org.eclipse.m2e.maveneclipse;

import static org.junit.Assert.*;

import org.eclipse.osgi.util.NLS;
import org.junit.Test;

/**
 * Tests for {@link Messages}.
 * 
 * @author Raphael von der Grün
 *
 */
public class MessagesTest {

	@Test
	public void shouldHaveConfiguratorErrorMessage() {
		String fileName = "test.file";
		String msg = NLS.bind(Messages.mavenEclipseProjectConfiguratorError, "test.file");
		assertEquals("Error importing maven-eclipse-plugin settings; " + fileName, msg);
	}

}
