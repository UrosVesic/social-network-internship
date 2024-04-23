package com.levi9.internship.social.network.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.levi9.internship.social.network.dto.FriendResponse;
import com.levi9.internship.social.network.service.FriendshipService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "/api/friends")
public class FriendshipController
{
	private final FriendshipService friendshipService;

	public FriendshipController(final FriendshipService friendshipService)
	{
		this.friendshipService = friendshipService;
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping("/{id}")
	public ResponseEntity<FriendResponse> getFriendById(final Principal principal, @PathVariable final String id)
	{
		return new ResponseEntity<>(friendshipService.getFriendById(principal.getName(), id), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteFriend(final Principal principal, @PathVariable final String id)
	{
		friendshipService.deleteFriend(principal.getName(), id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping()
	public ResponseEntity<List<FriendResponse>> findAllFriends(final Principal principal)
	{
		log.info("FriendshipController: FindAllFriends for user with id: %s".formatted(principal.getName()));

		return new ResponseEntity<>(friendshipService.findAllFriends(principal.getName()), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping("/search/{friendUsername}")
	public ResponseEntity<List<FriendResponse>> searchFriends(final Principal principal, @PathVariable final String friendUsername)
	{
		log.info(
			"FriendshipController: SearchFriends with request parameter: %s, for user with id: %s".formatted(
			friendUsername,
			principal.getName()));

		return new ResponseEntity<>(friendshipService.searchFriends(principal.getName(), friendUsername), HttpStatus.OK);
	}
}
