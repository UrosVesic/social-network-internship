package com.levi9.internship.social.network.service;

import java.security.Principal;
import java.util.List;
import com.levi9.internship.social.network.dto.GroupInviteResponse;
import com.levi9.internship.social.network.dto.GroupRequest;
import com.levi9.internship.social.network.dto.GroupResponse;
import com.levi9.internship.social.network.dto.JoinGroupResponse;
import com.levi9.internship.social.network.dto.MemberResponse;

public interface GroupService
{
	void deleteById(Long groupId, String userId);

	GroupResponse createGroup(GroupRequest request, Principal principal);

	void deleteGroupMember(Long groupId, String memberId, String admin);

	List<GroupResponse> searchForGroup(String username);

	GroupResponse findGroupById(Long id);

	List<GroupResponse> findAllGroups();

	GroupResponse updateGroup(String adminId, Long groupId, GroupRequest groupRequest);

	List<MemberResponse> findMembersOfGroup(String userId, Long groupId);

	void approveGroupMembership(Long requestId, String admin);

	void rejectGroupMembership(Long requestId, String admin);

	JoinGroupResponse joinGroup(Long groupId, String userId);

	GroupInviteResponse inviteUserToJoin(Long groupId, String receiverId, String senderId);

	void confirmInvitation(Long invitationId, String userId);

	void approveInvitation(Long invitationId, String userId);

	void rejectInvitation(Long invitationId, String userId);
}
