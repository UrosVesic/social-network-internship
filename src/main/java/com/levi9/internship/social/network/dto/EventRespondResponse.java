package com.levi9.internship.social.network.dto;

import com.levi9.internship.social.network.model.enums.EventRespondType;
import lombok.Data;

@Data
public class EventRespondResponse
{
	private Long id;
	private EventResponse eventResponse;
	private EventRespondType respondType;
}
