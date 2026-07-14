package com.openscheduler.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires a running PostgreSQL (docker compose up -d). "
        + "Re-enable once Testcontainers or a CI database is set up.")
class SchedulerApplicationTests {

	@Test
	void contextLoads() {
	}

}
