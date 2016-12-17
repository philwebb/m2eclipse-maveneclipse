package org.eclipse.m2e.maveneclipse;

import static org.junit.Assert.*;

import org.eclipse.osgi.util.NLS;
import org.junit.Test;

/**
 * Tests for {@link Messages}.
 *
 * @author Raphael von der Grün
 */
public class MessagesTest {

	private static final String TEST_FILE = "test.file";
	
	private static final String EXPECTED_MESSAGE = "Error importing maven-eclipse-plugin settings; " + TEST_FILE;

	@Test
	public void shouldHaveConfiguratorErrorMessage() {
		String msg = NLS.bind(Messages.mavenEclipseProjectConfiguratorError, TEST_FILE);
		assertEquals(EXPECTED_MESSAGE, msg);
	}

}
