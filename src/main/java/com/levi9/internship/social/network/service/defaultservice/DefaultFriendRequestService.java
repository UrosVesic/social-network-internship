package com.levi9.internship.social.network.service.defaultservice;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.levi9.internship.social.network.dao.FriendRequestDao;
import com.levi9.internship.social.network.dao.UserDao;
import com.levi9.internship.social.network.dto.FriendRequestResponse;
import com.levi9.internship.social.network.exceptions.BusinessException;
import com.levi9.internship.social.network.exceptions.ErrorCode;
import com.levi9.internship.social.network.model.FriendRequest;
import com.levi9.internship.social.network.model.User;
import com.levi9.internship.social.network.model.enums.FriendRequestStatus;
import com.levi9.internship.social.network.service.AuthService;
import com.levi9.internship.social.network.service.FriendRequestService;
import com.levi9.internship.social.network.service.FriendshipService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DefaultFriendRequestService implements FriendRequestService
{
	private final FriendRequestDao friendRequestDao;
	private final UserDao userDao;
	private final FriendshipService friendshipService;
	private final AuthService authService;

	private final ModelMapper modelMapper;

	public DefaultFriendRequestService(
		final FriendRequestDao friendRequestDao,
		final UserDao userDao,
		final ModelMapper modelMapper,
		final FriendshipService friendshipService,
		final AuthService authService)
	{
		this.friendRequestDao = friendRequestDao;
		this.userDao = userDao;
		this.modelMapper = modelMapper;
		this.friendshipService = friendshipService;
		this.authService = authService;
	}

	@Override
	public List<FriendRequestResponse> getPendingFriendRequests()
	{
		final User currentUser = authService.getCurrentUser();

		log.info("Getting pending friend requests for user {}", currentUser.getId());

		return friendRequestDao.getAllPendingRequests(currentUser.getId())
			.stream()
			.map(friendRequest -> modelMapper.map(friendRequest, FriendRequestResponse.class))
			.toList();
	}

	@Transactional
	@Override
	public FriendRequestResponse createRequestToAddFriend(final String userId)
	{
		log.info("Creating friend request to add user {}", userId);
		final User userToBeAddedAsFriend = userDao.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_CREATING_FRIEND_REQUEST, "User does not exist"));

		final User currentUser = authService.getCurrentUser();
		if (currentUser.getId().equals(userToBeAddedAsFriend.getId()))
		{
			throw new BusinessException(
				ErrorCode.ERROR_CREATING_FRIEND_REQUEST, "Sender and receiver of friend request cannot be the same user");
		}
		if (friendRequestDao.findAcceptedOrPendingFriendRequest(currentUser.getId(), userToBeAddedAsFriend.getId()).isPresent())
		{
			throw new BusinessException(ErrorCode.ERROR_CREATING_FRIEND_REQUEST, "Friend request has already been created");
		}
		final FriendRequest friendRequest = new FriendRequest();
		friendRequest.setReceiver(userToBeAddedAsFriend);
		friendRequest.setSender(currentUser);
		friendRequest.setStatus(FriendRequestStatus.PENDING);
		friendRequestDao.save(friendRequest);
		log.info("User {} created friend request to add user {}", currentUser.getId(), userToBeAddedAsFriend.getId());

		return modelMapper.map(friendRequest, FriendRequestResponse.class);
	}

	@Override
	public void acceptFriendRequest(final Long requestId)
	{
		log.info("Accepting friend request {}", requestId);

		final User currentUser = authService.getCurrentUser();
		final FriendRequest friendRequest = friendRequestDao.findByIdAndReceiver_Id(requestId, currentUser.getId())
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_ACCEPTING_FRIEND_REQUEST, "Friend request does not exist"));

		if (friendRequest.getStatus().equals(FriendRequestStatus.ACCEPTED)
			|| friendRequest.getStatus().equals(FriendRequestStatus.REJECTED))
		{
			throw new BusinessException(
				ErrorCode.ERROR_ACCEPTING_FRIEND_REQUEST, "Friend request is already processed");
		}
		friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
		friendRequestDao.save(friendRequest);
		friendshipService.saveFriendship(friendRequest.getReceiver().getId(), friendRequest.getSender().getId());

		log.info("Friend request {} is accepted by user {}", requestId, currentUser.getId());
	}

	@Override
	public void rejectFriendRequest(final Long requestId)
	{
		log.info("Rejecting friend request");

		final User currentUser = authService.getCurrentUser();
		final FriendRequest friendRequest = friendRequestDao.findByIdAndReceiver_Id(requestId, currentUser.getId())
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_REJECTING_FRIEND_REQUEST, "Friend request does not exist"));

		if (friendRequest.getStatus().equals(FriendRequestStatus.ACCEPTED)
			|| friendRequest.getStatus().equals(FriendRequestStatus.REJECTED))
		{
			throw new BusinessException(
				ErrorCode.ERROR_REJECTING_FRIEND_REQUEST, "Friend request is already processed");
		}
		friendRequest.setStatus(FriendRequestStatus.REJECTED);
		friendRequestDao.save(friendRequest);
	}
}
