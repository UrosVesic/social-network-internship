package com.levi9.internship.social.network.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.levi9.internship.social.network.model.FriendRequest;

public interface FriendRequestDao extends JpaRepository<FriendRequest, Long>
{
	Optional<FriendRequest> findByIdAndReceiver_Id(final Long requestId, final String receiverId);

	@Query(value = QueryConstants.GET_ACCEPTED_OR_PENDING_FRIEND_REQUEST_FOR_USERS)
	Optional<FriendRequest> findAcceptedOrPendingFriendRequest(
		@Param("user1") String user1,
		@Param("user2") String user2);

	@Query(value = QueryConstants.GET_ALL_PENDING_REQUESTS)
	List<FriendRequest> getAllPendingRequests(@Param("userId") String userId);

	@Modifying
	@Query(value = QueryConstants.DELETE_FRIEND_REQUEST_BY_USER)
	void deleteFriendRequestsByUsers(@Param("senderId") String senderId, @Param("receiverId") String receiverId);
}
