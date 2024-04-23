package com.levi9.internship.social.network.dto;

import com.levi9.internship.social.network.model.User;
import com.levi9.internship.social.network.model.enums.FriendRequestStatus;
import lombok.Data;

@Data
public class FriendRequestResponse
{
	private Long id;
	private User sender;
	private User receiver;
	private FriendRequestStatus status;
}
