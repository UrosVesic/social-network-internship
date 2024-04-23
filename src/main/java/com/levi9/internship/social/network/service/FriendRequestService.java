package com.levi9.internship.social.network.service;

import java.util.List;
import com.levi9.internship.social.network.dto.FriendRequestResponse;

public interface FriendRequestService
{
	FriendRequestResponse createRequestToAddFriend(final String userId);

	List<FriendRequestResponse> getPendingFriendRequests();

	void acceptFriendRequest(final Long requestId);

	void rejectFriendRequest(final Long requestId);
}
