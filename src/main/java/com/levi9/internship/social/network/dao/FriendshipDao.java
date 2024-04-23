package com.levi9.internship.social.network.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.levi9.internship.social.network.model.Friendship;
import com.levi9.internship.social.network.model.User;

public interface FriendshipDao extends JpaRepository<Friendship, Long>
{

	@Query(value = QueryConstants.GET_ALL_FRIENDS)
	List<User> findAllFriends(@Param("userId") String userId);

	@Query(value = QueryConstants.FIND_FRIENDS)
	List<User> searchFriends(@Param("userId") String userId, @Param("friendUsername") String friendUsername);

	@Query(value = QueryConstants.GET_FRIENDSHIP_STATUS)
	Optional<Friendship> findFriend(@Param("senderId") String senderId, @Param("receiverId") String receiverId);
}
