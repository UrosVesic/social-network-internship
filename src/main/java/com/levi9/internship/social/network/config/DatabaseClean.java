package com.levi9.internship.social.network.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("clean")
@Configuration
public class DatabaseClean
{

	@Bean
	public FlywayMigrationStrategy clean()
	{
		return flyway -> {
			flyway.clean();
			flyway.migrate();
		};
	}
}
