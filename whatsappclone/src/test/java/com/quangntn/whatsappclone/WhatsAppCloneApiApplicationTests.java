package com.quangntn.whatsappclone;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // This will ensure it uses application.yml from test resources
class WhatsAppCloneApiApplicationTests {

	@Test
	void contextLoads() {
		// This test verifies that the Spring context can load successfully
		// with our test configuration (H2 database)
	}

}
