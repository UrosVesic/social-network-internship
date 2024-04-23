package com.levi9.internship.social.network.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.security.Principal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.levi9.internship.social.network.dto.EventRequest;
import com.levi9.internship.social.network.dto.EventRespondResponse;
import com.levi9.internship.social.network.dto.EventResponse;
import com.levi9.internship.social.network.service.EventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "/api/groups/{groupId}/events")
public class EventController
{

	private final EventService eventService;

	public EventController(final EventService eventService)
	{
		this.eventService = eventService;
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<EventResponse> createEvent(
		@PathVariable final Long groupId,
		@RequestBody @Valid final EventRequest request, final Principal principal)
	{
		log.info(
			"EventController: Creating event, groupId:{%d}, userId: {%s}, with body: {%s}".formatted(
			groupId,
			principal.getName(),
			request.toString()));
		return new ResponseEntity<>(eventService.create(principal.getName(), groupId, request), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@DeleteMapping(value = "/{eventId}")
	public ResponseEntity<Void> deleteEvent(
		@PathVariable final Long groupId,
		@PathVariable final Long eventId,
		final Principal principal)
	{
		log.info("DefaultEventService: Deleting event: {%d} from group: {%d}".formatted(eventId, groupId));
		eventService.delete(principal.getName(), groupId, eventId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping(value = "/{eventId}/attend", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<EventRespondResponse> attendEvent(
		@PathVariable final Long groupId,
		@PathVariable final Long eventId,
		final Principal principal)
	{
		return new ResponseEntity<>(eventService.attendEvent(principal.getName(), groupId, eventId), HttpStatus.OK);
	}
}
