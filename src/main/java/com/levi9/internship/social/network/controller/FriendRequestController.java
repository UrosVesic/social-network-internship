package com.levi9.internship.social.network.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.levi9.internship.social.network.dto.FriendRequestResponse;
import com.levi9.internship.social.network.service.FriendRequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value = "/api/friend-requests")
public class FriendRequestController
{

	private final FriendRequestService friendRequestService;

	public FriendRequestController(final FriendRequestService friendRequestService)
	{
		this.friendRequestService = friendRequestService;
	}

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping()
    public ResponseEntity<List<FriendRequestResponse>> getPendingFriendRequests()
    {
        final List<FriendRequestResponse> response = friendRequestService.getPendingFriendRequests();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping()
    public ResponseEntity<FriendRequestResponse> createFriendRequest(@RequestBody final Map<String, String> body)
    {
        final FriendRequestResponse response = friendRequestService.createRequestToAddFriend(body.get("userId"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/{requestId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable final Long requestId)
    {
        friendRequestService.acceptFriendRequest(requestId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/{requestId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable final Long requestId)
    {
        friendRequestService.rejectFriendRequest(requestId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
