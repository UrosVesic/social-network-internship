package com.levi9.internship.social.network.service;

import com.levi9.internship.social.network.dto.EventRequest;
import com.levi9.internship.social.network.dto.EventRespondResponse;
import com.levi9.internship.social.network.dto.EventResponse;

public interface EventService
{
	EventRespondResponse attendEvent(String name, Long groupId, Long eventId);

	EventResponse create(String userId, Long groupId, EventRequest request);

	void delete(String userId, Long groupId, Long eventId);
}
