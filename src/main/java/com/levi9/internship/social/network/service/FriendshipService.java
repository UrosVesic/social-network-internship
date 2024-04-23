package com.levi9.internship.social.network.service;

import java.util.List;
import com.levi9.internship.social.network.dto.FriendResponse;

public interface FriendshipService
{
	FriendResponse getFriendById(String user1, String user2);

	void deleteFriend(String user1, String user2);

	List<FriendResponse> findAllFriends(String userId);

	List<FriendResponse> searchFriends(String userId, String friendUsername);

	void saveFriendship(String receiverId, String senderId);
}
