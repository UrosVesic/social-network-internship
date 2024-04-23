package com.levi9.internship.social.network.dto;

import lombok.Data;

@Data
public class KafkaMessage
{

	private PostResponse post;
	private String token;
}
