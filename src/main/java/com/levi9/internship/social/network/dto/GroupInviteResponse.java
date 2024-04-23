package com.levi9.internship.social.network.dto;

import com.levi9.internship.social.network.model.Group;
import com.levi9.internship.social.network.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupInviteResponse
{
	private Long inviteId;
	private User sender;
	private User receiver;
	private Group group;
}
