package com.levi9.internship.social.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.levi9.internship.social.network"})
public class SocialNetworkApplication {

	@Value("${spring.cloud.aws.credentials.profile.name}")
	private String profile;

	private static final Logger log = LoggerFactory.getLogger(SocialNetworkApplication.class);

    public static void main(final String[] args) {
        SpringApplication.run(SocialNetworkApplication.class, args);
    }

	@Bean
	CommandLineRunner commandLineRunner(){
		return args-> log.info("Profile: {}", profile);
	}
}
