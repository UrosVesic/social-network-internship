package com.levi9.internship.social.network.service.defaultservice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import com.levi9.internship.social.network.dao.FriendRequestDao;
import com.levi9.internship.social.network.dao.FriendshipDao;
import com.levi9.internship.social.network.dao.UserDao;
import com.levi9.internship.social.network.dto.FriendResponse;
import com.levi9.internship.social.network.exceptions.BusinessException;
import com.levi9.internship.social.network.exceptions.ErrorCode;
import com.levi9.internship.social.network.model.Friendship;
import com.levi9.internship.social.network.model.User;
import com.levi9.internship.social.network.service.FriendshipService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DefaultFriendshipService implements FriendshipService
{
	private final FriendshipDao friendshipDao;

	private final FriendRequestDao friendRequestDao;
	private final UserDao userDao;
	private final ModelMapper modelMapper;

	public DefaultFriendshipService(
		final FriendshipDao friendshipDao,
		final FriendRequestDao friendRequestDao,
		final UserDao userDao,
		final ModelMapper modelMapper)
	{
		this.friendshipDao = friendshipDao;
		this.friendRequestDao = friendRequestDao;
		this.userDao = userDao;
		this.modelMapper = modelMapper;
	}

	@Override
	public List<FriendResponse> findAllFriends(final String userId)
	{
		log.info("Getting all friends for user {}", userId);
		userDao.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_GETTING_FRIENDS, "User with provided id does not exist."));

		final List<FriendResponse> friends = new ArrayList<>();
		for (final User friend : friendshipDao.findAllFriends(userId))
		{
			friends.add(modelMapper.map(friend, FriendResponse.class));
		}
		return friends;
	}

	@Override
	public FriendResponse getFriendById(final String senderId, final String receiverId)
	{
		log.info("Getting friend {} for user {}", receiverId, senderId);
		userDao.findById(senderId).orElseThrow(() -> new BusinessException(ErrorCode.ERROR_GETTING_FRIEND, """
			User does not exist\
			.\
			"""));
		final User user =
			userDao.findById(receiverId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_GETTING_FRIEND, "User does not exist."));
		friendshipDao.findFriend(senderId, receiverId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_GETTING_FRIEND, "Friendship does not exist."));
		return modelMapper.map(user, FriendResponse.class);
	}

	@Modifying
	@Transactional
	@Override
	public void deleteFriend(final String senderId, final String receiverId)
	{
		log.info("Deleting friend {} for user {}", receiverId, senderId);
		userDao.findById(senderId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_FRIEND, "User does not exist."));
		userDao.findById(receiverId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_FRIEND, "User does not exist."));
		friendshipDao.delete(friendshipDao.findFriend(senderId, receiverId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_FRIEND, "Friendship does not exist.")));
		friendRequestDao.deleteFriendRequestsByUsers(senderId, receiverId);
		log.info("Friend deleted");
	}

	@Override
	public List<FriendResponse> searchFriends(final String userId, final String friendUsername)
	{
		log.info("Searching friends for user {}", userId);
		final List<FriendResponse> friends = new ArrayList<>();
		for (final User friend : friendshipDao.searchFriends(userId, friendUsername))
		{
			friends.add(modelMapper.map(friend, FriendResponse.class));
		}
		return friends;
	}

	@Override
	public void saveFriendship(final String receiverId, final String senderId)
	{
		log.info("Saving friendship between users: {}, {}", senderId, receiverId);
		final User receiver = userDao.findById(receiverId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_FRIEND, "User does not exist."));
		final User sender = userDao.findById(senderId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_FRIEND, "User does not exist."));

		final LocalDateTime friendSince = LocalDateTime.now();
		final Friendship friendship = new Friendship();
		friendship.setFriendsSince(friendSince);
		friendship.setUser1(receiver);
		friendship.setUser2(sender);
		friendshipDao.save(friendship);
		log.info("Friendship saved");
	}
}
