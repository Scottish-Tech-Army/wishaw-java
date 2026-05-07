package org.scottishtecharmy.wishaw_java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Lightweight sanity test — does NOT start the Spring context (no DB needed).
 * Add @SpringBootTest + Testcontainers once an integration test profile exists.
 */
class WishawJavaApplicationTests {

	@Test
	void applicationClassExists() {
		assertNotNull(WishawJavaApplication.class);
	}

}
