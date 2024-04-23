package com.levi9.internship.social.network.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EventResponse
{

	private Long id;
	private String name;
	private String description;
	private LocationResponse location;
	private LocalDateTime eventTime;
}
