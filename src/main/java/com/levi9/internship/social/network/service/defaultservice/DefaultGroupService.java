package com.levi9.internship.social.network.service.defaultservice;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.levi9.internship.social.network.dao.FriendshipDao;
import com.levi9.internship.social.network.dao.GroupDao;
import com.levi9.internship.social.network.dao.GroupMembershipDao;
import com.levi9.internship.social.network.dao.JoinGroupRequestDao;
import com.levi9.internship.social.network.dao.UserDao;
import com.levi9.internship.social.network.dto.GroupInviteResponse;
import com.levi9.internship.social.network.dto.GroupRequest;
import com.levi9.internship.social.network.dto.GroupResponse;
import com.levi9.internship.social.network.dto.JoinGroupResponse;
import com.levi9.internship.social.network.dto.MemberResponse;
import com.levi9.internship.social.network.exceptions.BusinessException;
import com.levi9.internship.social.network.exceptions.ErrorCode;
import com.levi9.internship.social.network.model.Group;
import com.levi9.internship.social.network.model.GroupMembership;
import com.levi9.internship.social.network.model.JoinGroupRequest;
import com.levi9.internship.social.network.model.User;
import com.levi9.internship.social.network.model.enums.JoinGroupRequestStatus;
import com.levi9.internship.social.network.service.GroupService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DefaultGroupService implements GroupService
{
	private final GroupDao groupDao;
	private final GroupMembershipDao groupMembershipDao;
	private final ModelMapper modelMapper;
	private final UserDao userDao;
	private final JoinGroupRequestDao joinGroupRequestDao;
	private final FriendshipDao friendshipDao;

	public DefaultGroupService(
		final GroupDao groupDao,
		final ModelMapper modelMapper,
		final UserDao userDao,
		final GroupMembershipDao groupMembershipDao,
		final JoinGroupRequestDao joinGroupRequestDao,
		final FriendshipDao friendshipDao)
	{
		this.groupDao = groupDao;
		this.groupMembershipDao = groupMembershipDao;
		this.modelMapper = modelMapper;
		this.userDao = userDao;
		this.joinGroupRequestDao = joinGroupRequestDao;
		this.friendshipDao = friendshipDao;
	}

	@Transactional
	@Override
	public GroupResponse createGroup(final GroupRequest request, final Principal principal)
	{
		final Group group = modelMapper.map(request, Group.class);
		group.setAdmin(userDao.findById(principal.getName())
			.orElseThrow(() -> new UsernameNotFoundException("User that tried creating group does not exist")));
		final GroupMembership groupMembership = new GroupMembership();
		group.setPrivate(request.isNonPublic());
		groupMembership.setGroup(group);
		groupMembership.setUser(group.getAdmin());
		groupDao.saveAndFlush(group);
		groupMembershipDao.save(groupMembership);
		return modelMapper.map(group, GroupResponse.class);
	}

	@Transactional
	@Override
	public void deleteById(final Long groupId, final String userId)
	{
		log.info("DefaultGroupService: Deleting group by id: {%d}".formatted(groupId));
		final Group group = groupDao.findById(groupId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_GROUP, "Group does not exist."));
		if (!group.getAdmin().getId().equals(userId))
		{
			throw new BusinessException(ErrorCode.ERROR_DELETING_GROUP, "Current user is not the admin of provided group.");
		}
		groupDao.deleteById(groupId);
	}

	@Transactional
	@Override
	public void deleteGroupMember(final Long groupId, final String memberId, final String userId)
	{
		log.info("DefaultGroupService: Deleting group member: {} from group: {}", memberId, groupId);
		final Group group = groupDao.findById(groupId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_GROUP_MEMBER, "Group does not exist."));
		//current user is admin
		if (group.getAdmin().getId().equals(userId))
		{
			//admin can't be deleted
			if (group.getAdmin().getId().equals(memberId))
			{
				throw new BusinessException(ErrorCode.ERROR_DELETING_GROUP_MEMBER, "Admin can't be deleted from the group.");
			}
			final GroupMembership groupMembership = groupMembershipDao.findByUserIdAndGroupId(memberId, groupId)
				.orElseThrow(() -> new BusinessException(
					ErrorCode.ERROR_DELETING_GROUP_MEMBER,
					"User does not belong to provided group."));
			groupMembershipDao.deleteById(groupMembership.getId());
			if (group.isPrivate())
			{
				joinGroupRequestDao.deleteByGroupIdAndUserId(groupId, memberId);
			}
			return;
		}
		//current user is not admin
		if (!group.getAdmin().getId().equals(userId) && !userId.equals(memberId))
		{
			throw new BusinessException(ErrorCode.ERROR_DELETING_GROUP_MEMBER, "Current user is not an admin of provided group.");
		}

		final GroupMembership groupMembership = groupMembershipDao.findByUserIdAndGroupId(memberId, groupId)
			.orElseThrow(() -> new BusinessException(
				ErrorCode.ERROR_DELETING_GROUP_MEMBER,
				"User does not belong to provided group."));
		groupMembershipDao.deleteById(groupMembership.getId());
		if (group.isPrivate())
		{
			joinGroupRequestDao.deleteByGroupIdAndUserId(groupId, memberId);
		}
	}

	@Override
	public List<GroupResponse> searchForGroup(final String username)
	{
		final List<Group> groups = groupDao.findGroupsByName(username);

		return groups.stream().map(group -> modelMapper.map(group, GroupResponse.class)).collect(Collectors.toList());
	}

	@Override
	public GroupResponse findGroupById(final Long id)
	{
		final Group group = groupDao.findGroupById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_GETTING_GROUP, "Group does not exist."));

		return modelMapper.map(group, GroupResponse.class);
	}

	@Override
	public List<GroupResponse> findAllGroups()
	{
		final List<Group> groups = groupDao.findAll();

		return groups.stream().map(group -> modelMapper.map(group, GroupResponse.class)).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public GroupResponse updateGroup(final String adminId, final Long groupId, final GroupRequest groupRequest)
	{
		final Group group = groupDao.findGroupById(groupId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_UPDATING_GROUP, "Group does not exist."));
		groupDao.findByIdAndAdminId(groupId, adminId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_UPDATING_GROUP, "Group with provided admin id not found."));

		group.setName(groupRequest.getName());
		group.setPrivate(groupRequest.isNonPublic());
		groupDao.saveAndFlush(group);

		return modelMapper.map(group, GroupResponse.class);
	}

	@Transactional
	@Override
	public List<MemberResponse> findMembersOfGroup(final String userId, final Long groupId)
	{
		final Group group = groupDao.findGroupById(groupId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_GETTING_GROUP_MEMBERS, "Group does not exist."));
		if (group.isPrivate())
		{
			groupMembershipDao.findByUserIdAndGroupId(userId, groupId).orElseThrow(() -> new BusinessException(
				ErrorCode.ERROR_GETTING_GROUP_MEMBERS,
				"User is not member of provided private group."));
		}

		final List<User> members = groupMembershipDao.getMembersOfGroup(groupId);

		return members.stream().map(member -> modelMapper.map(member, MemberResponse.class)).collect(Collectors.toList());
	}

	@Override
	public void approveGroupMembership(final Long requestId, final String admin)
	{
		final JoinGroupRequest validRequest = validateRequest(joinGroupRequestDao.findById(requestId), admin);
		final User user = validRequest.getUser();
		final Group group = validRequest.getGroup();

		if ((groupMembershipDao.findByUserIdAndGroupId(user.getId(), group.getId())).isPresent())
		{
			throw new BusinessException(ErrorCode.ERROR_APPROVING_GROUP_MEMBERSHIP, "User is already member of the group");
		}

		final GroupMembership membership = new GroupMembership();
		membership.setGroup(group);
		membership.setUser(user);
		log.info("Adding member to group");
		groupMembershipDao.save(membership);

		validRequest.setRespondType(JoinGroupRequestStatus.ACCEPTED);
		joinGroupRequestDao.save(validRequest);
	}

	@Override
	public void rejectGroupMembership(final Long requestId, final String admin)
	{
		final JoinGroupRequest validRequest = validateRequest(joinGroupRequestDao.findById(requestId), admin);
		log.info("Rejecting request");
		validRequest.setRespondType(JoinGroupRequestStatus.REJECTED);
		joinGroupRequestDao.save(validRequest);
	}

	protected JoinGroupRequest validateRequest(final Optional<JoinGroupRequest> request, final String admin)
	{
		if (request.isEmpty())
		{
			throw new BusinessException(ErrorCode.ERROR_VALIDATING_GROUP_MEMBERSHIP_REQUEST, "Request does not exist.");
		}

		if (!request.get().getRespondType().name().equals("PENDING"))
		{
			throw new BusinessException(ErrorCode.ERROR_VALIDATING_GROUP_MEMBERSHIP_REQUEST, "Invalid request status");
		}

		final Group group = request.get().getGroup();
		groupDao.findByIdAndAdminId(group.getId(), admin)
			.orElseThrow(() -> new BusinessException(
				ErrorCode.ERROR_VALIDATING_GROUP_MEMBERSHIP_REQUEST,
				"Current user is not an admin of the group."));
		return request.get();
	}

	@Transactional
	@Override
	public JoinGroupResponse joinGroup(final Long groupId, final String userId)
	{
		log.info("DefaultGroupService: User: {} joining group: {}", userId, groupId);

		final Group group = groupDao.findById(groupId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_JOINING_GROUP, "Group does not exist."));
		final User user = userDao.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_JOINING_GROUP, "User does not exist."));
		if (groupMembershipDao.findByUserIdAndGroupId(userId, groupId).isPresent())
		{
			throw new BusinessException(ErrorCode.ERROR_JOINING_GROUP, "User is already a group member.");
		}
		if (group.isPrivate())
		{
			final JoinGroupRequest request = createRequestToJoinGroup(user, group);
			log.debug("Created request with id: {} for user to join private group", request.getId());
			return new JoinGroupResponse(JoinGroupResponse.Result.CREATED_REQUEST_TO_JOIN_GROUP, request.getId());
		}
		addUserToGroup(user, group);
		log.debug("User {} successfully joined group {}", user.getId(), group.getId());
		return new JoinGroupResponse(JoinGroupResponse.Result.USER_ADDED_TO_GROUP, null);
	}

	private void addUserToGroup(final User user, final Group group)
	{
		final GroupMembership groupMembership = new GroupMembership();
		groupMembership.setGroup(group);
		groupMembership.setUser(user);
		groupMembership.setMemberSince(LocalDateTime.now());
		groupMembershipDao.save(groupMembership);
	}

	private JoinGroupRequest createRequestToJoinGroup(final User user, final Group group)
	{
		if (joinGroupRequestDao.findRequestByUserIdAndGroupId(group.getId(), user.getId()).isPresent())
		{
			throw new BusinessException(ErrorCode.ERROR_JOINING_GROUP, "Request from provided user already exists.");
		}
		final JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
		joinGroupRequest.setGroup(group);
		joinGroupRequest.setUser(user);
		joinGroupRequest.setRespondType(JoinGroupRequestStatus.PENDING);
		joinGroupRequestDao.save(joinGroupRequest);
		return joinGroupRequest;
	}

	@Override
	public GroupInviteResponse inviteUserToJoin(final Long groupId, final String receiverId, final String senderId)
	{
		final Group group = groupDao.findById(groupId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_INVITING_TO_GROUP, "Group does not exist."));
		final User receiver = userDao.findById(receiverId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_INVITING_TO_GROUP, "User does not exist."));
		final User sender = userDao.findById(senderId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_INVITING_TO_GROUP, "User does not exist."));
		if (groupMembershipDao.findByUserIdAndGroupId(senderId, groupId).isEmpty())
		{
			throw new BusinessException(
				ErrorCode.ERROR_INVITING_TO_GROUP,
				"User who wants to make an invitation is not a group member.");
		}
		if (groupMembershipDao.findByUserIdAndGroupId(receiverId, groupId).isPresent())
		{
			throw new BusinessException(ErrorCode.ERROR_INVITING_TO_GROUP, "User is already a group member.");
		}
		if (joinGroupRequestDao.findAnyUnhandledInvitations(groupId, receiverId).isPresent())
		{
			throw new BusinessException(ErrorCode.ERROR_INVITING_TO_GROUP, "Previous join request not handled");
		}

		final JoinGroupRequest joinGroupRequest = new JoinGroupRequest();
		joinGroupRequest.setGroup(group);
		joinGroupRequest.setUser(receiver);

		if (group.getAdmin().getId().equals(senderId))
		{
			joinGroupRequest.setRespondType(JoinGroupRequestStatus.PENDING_INVITATION_ADMIN);
			joinGroupRequestDao.save(joinGroupRequest);
		}
		else
		{
			if (friendshipDao.findFriend(receiverId, senderId).isEmpty())
			{
				throw new BusinessException(ErrorCode.ERROR_INVITING_TO_GROUP, "Users are not friends.");
			}
			joinGroupRequest.setRespondType(JoinGroupRequestStatus.PENDING_INVITATION_FRIEND);
			joinGroupRequestDao.save(joinGroupRequest);
		}

		return new GroupInviteResponse(joinGroupRequest.getId(), sender, receiver, group);
	}

	@Transactional
	@Override
	public void confirmInvitation(final Long invitationId, final String userId)
	{
		final JoinGroupRequest joinInvitation = joinGroupRequestDao.findById(invitationId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_CONFIRMING_INVITATION, "Invitation does not exist."));

		if (joinInvitation.getGroup().getAdmin().getId().equals(userId))
		{
			throw new BusinessException(ErrorCode.ERROR_CONFIRMING_INVITATION, "User is group admin.");
		}

		if (!joinInvitation.getUser().getId().equals(userId))
		{
			throw new BusinessException(
				ErrorCode.ERROR_CONFIRMING_INVITATION,
				"Invitation with provided id is not for current user.");
		}

		if (joinInvitation.getRespondType().equals(JoinGroupRequestStatus.PENDING_INVITATION_ADMIN))
		{
			setGroupMembership(joinInvitation);
		}
		else if (joinInvitation.getRespondType().equals(JoinGroupRequestStatus.PENDING_INVITATION_FRIEND))
		{
			if (joinInvitation.getGroup().isPrivate())
			{
				joinInvitation.setRespondType(JoinGroupRequestStatus.WAITING_FOR_ADMIN_APPROVAL);
				joinGroupRequestDao.save(joinInvitation);
			}
			else
			{
				setGroupMembership(joinInvitation);
			}
		}
		else
		{
			throw new BusinessException(ErrorCode.ERROR_CONFIRMING_INVITATION, "Invalid invitation status");
		}
	}

	@Transactional
	@Override
	public void approveInvitation(final Long invitationId, final String userId)
	{
		final JoinGroupRequest joinInvitation = joinGroupRequestDao.findById(invitationId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_APPROVING_INVITATION, "Invitation does not exist."));

		if (!joinInvitation.getGroup().getAdmin().getId().equals(userId))
		{
			throw new AccessDeniedException("User is not group admin.");
		}

		if (joinInvitation.getRespondType().equals(JoinGroupRequestStatus.PENDING_INVITATION_FRIEND))
		{
			throw new BusinessException(ErrorCode.ERROR_APPROVING_INVITATION, "User hasn't yet confirmed invite");
		}
		else if (joinInvitation.getRespondType().equals(JoinGroupRequestStatus.WAITING_FOR_ADMIN_APPROVAL))
		{
			setGroupMembership(joinInvitation);
		}
		else
		{
			throw new BusinessException(ErrorCode.ERROR_APPROVING_INVITATION, "Invitation with provided id is already handled.");
		}
	}

	private void setGroupMembership(final JoinGroupRequest joinInvitation)
	{
		joinInvitation.setRespondType(JoinGroupRequestStatus.ACCEPTED);
		joinGroupRequestDao.save(joinInvitation);
		final GroupMembership groupMembership = new GroupMembership();
		groupMembership.setGroup(joinInvitation.getGroup());
		groupMembership.setUser(joinInvitation.getUser());
		groupMembershipDao.save(groupMembership);
	}

	@Transactional
	@Override
	public void rejectInvitation(final Long invitationId, final String userId)
	{
		final JoinGroupRequest joinInvitation = joinGroupRequestDao.findById(invitationId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_REJECTING_INVITATION, "Invitation does not exist."));

		if (joinInvitation.getGroup().getAdmin().getId().equals(userId) && joinInvitation.getRespondType()
			.equals(JoinGroupRequestStatus.WAITING_FOR_ADMIN_APPROVAL))
		{
			joinInvitation.setRespondType(JoinGroupRequestStatus.REJECTED);
			joinGroupRequestDao.save(joinInvitation);
		}
		else if (joinInvitation.getUser().getId().equals(userId) && (joinInvitation.getRespondType()
			.equals(JoinGroupRequestStatus.PENDING_INVITATION_FRIEND) || joinInvitation.getRespondType()
			.equals(JoinGroupRequestStatus.PENDING_INVITATION_ADMIN)))
		{
			joinInvitation.setRespondType(JoinGroupRequestStatus.REJECTED);
			joinGroupRequestDao.save(joinInvitation);
		}
		else if (joinInvitation.getGroup().getAdmin().getId().equals(userId) && (joinInvitation.getRespondType()
			.equals(JoinGroupRequestStatus.ACCEPTED) || joinInvitation.getRespondType().equals(JoinGroupRequestStatus.REJECTED)))
		{
			throw new BusinessException(ErrorCode.ERROR_REJECTING_INVITATION, "Invitation with provided id is already handled.");
		}
		else
		{
			throw new AccessDeniedException("User is not group admin.");
		}
	}
}
