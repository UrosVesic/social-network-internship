package com.levi9.internship.social.network;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages = { "com.levi9.internship.social.network" })
public class SocialNetworkApplication
{

	public static void main(final String[] args)
	{
		SpringApplication.run(SocialNetworkApplication.class, args);
	}
}
