package com.levi9.internship.social.network.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.security.Principal;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.levi9.internship.social.network.dto.GroupInviteResponse;
import com.levi9.internship.social.network.dto.GroupRequest;
import com.levi9.internship.social.network.dto.GroupResponse;
import com.levi9.internship.social.network.dto.JoinGroupResponse;
import com.levi9.internship.social.network.dto.MemberResponse;
import com.levi9.internship.social.network.service.GroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "/api/groups")
public class GroupController
{
	private final GroupService groupService;

	public GroupController(final GroupService groupService)
	{
		this.groupService = groupService;
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@DeleteMapping(value = "/{groupId}")
	public ResponseEntity<Void> deleteById(@PathVariable final Long groupId, final Principal principal)
	{
		log.info("GroupController: Deleting group by id: {%d}".formatted(groupId));
		groupService.deleteById(groupId, principal.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@DeleteMapping(value = "/{groupId}/members/{memberId}")
	public ResponseEntity<Void> deleteGroupMember(
		@PathVariable final Long groupId,
		@PathVariable final String memberId, final Principal principal)
	{
		log.info("GroupController: Deleting group member by id: {%s}, from group: {%d}".formatted(memberId, groupId));
		groupService.deleteGroupMember(groupId, memberId, principal.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<GroupResponse> createGroup(@RequestBody @Valid final GroupRequest request, final Principal principal)
	{
		return new ResponseEntity<>(groupService.createGroup(request, principal), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping(value = "/request/{requestId}/approve")
	public ResponseEntity<Void> approveGroupMembership(@PathVariable final Long requestId, final Principal principal)
	{
		groupService.approveGroupMembership(requestId, principal.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping(value = "/request/{requestId}/reject")
	public ResponseEntity<Void> rejectGroupMembership(@PathVariable final Long requestId, final Principal principal)
	{
		groupService.rejectGroupMembership(requestId, principal.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/search/{name}")
	public ResponseEntity<List<GroupResponse>> searchForGroup(@PathVariable final String name)
	{
		log.info("GroupController: SearchForGroup with request parameter: %s".formatted(name));
		return new ResponseEntity<>(groupService.searchForGroup(name), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping("/{id}")
	public ResponseEntity<GroupResponse> findGroupById(@PathVariable final Long id)
	{
		return new ResponseEntity<>(groupService.findGroupById(id), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping()
	public ResponseEntity<List<GroupResponse>> findAllGroups()
	{
		return new ResponseEntity<>(groupService.findAllGroups(), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PutMapping(value = "/{groupId}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<GroupResponse> updateGroup(
		final Principal principal,
		@PathVariable final Long groupId,
		@RequestBody @Valid final GroupRequest groupRequest)
	{
		return new ResponseEntity<>(groupService.updateGroup(principal.getName(), groupId, groupRequest), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping(value = "/{groupId}/members")
	public ResponseEntity<List<MemberResponse>> findGroupMembers(
		@PathVariable final Long groupId,
		final Principal principal)
	{
		return new ResponseEntity<>(groupService.findMembersOfGroup(principal.getName(), groupId), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping(value = "/{groupId}/join")
	public ResponseEntity<JoinGroupResponse> joinGroup(@PathVariable final Long groupId, final Principal principal)
	{
		log.info("GroupController: Joining group with id: {%d}, with user: {%s}".formatted(groupId, principal.getName()));
		final JoinGroupResponse response = groupService.joinGroup(groupId, principal.getName());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping(value = "/{groupId}/invite/{userId}")
	public ResponseEntity<GroupInviteResponse> inviteUserToJoin(
		@PathVariable final Long groupId,
		@PathVariable final String userId,
		final Principal principal)
	{
		log.info("GroupController: Invite user with id: {%s}, to join a group with id: {%d}".formatted(userId, groupId));
		return new ResponseEntity<>(groupService.inviteUserToJoin(groupId, userId, principal.getName()), HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PatchMapping(value = "/invitations/{invitationId}/confirm")
	public ResponseEntity<Void> confirmInvitation(@PathVariable final Long invitationId, final Principal principal)
	{
		log.info("GroupController: Confirm invitation with id: {%d}".formatted(invitationId));
		groupService.confirmInvitation(invitationId, principal.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PatchMapping(value = "/invitations/{invitationId}/approve")
	public ResponseEntity<Void> approveInvitation(@PathVariable final Long invitationId, final Principal principal)
	{
		log.info("GroupController: Approve invitation with id: {%d}".formatted(invitationId));
		groupService.approveInvitation(invitationId, principal.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PatchMapping(value = "/invitations/{invitationId}/reject")
	public ResponseEntity<Void> rejectInvitation(@PathVariable final Long invitationId, final Principal principal)
	{
		log.info("GroupController: Reject invitation with id: {%d}".formatted(invitationId));
		groupService.rejectInvitation(invitationId, principal.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
