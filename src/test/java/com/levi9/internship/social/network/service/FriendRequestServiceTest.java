package com.levi9.internship.social.network.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import com.levi9.internship.social.network.TestConstants;
import com.levi9.internship.social.network.dao.FriendRequestDao;
import com.levi9.internship.social.network.dao.UserDao;
import com.levi9.internship.social.network.exceptions.BusinessException;
import com.levi9.internship.social.network.exceptions.ErrorCode;
import com.levi9.internship.social.network.model.FriendRequest;
import com.levi9.internship.social.network.model.User;
import com.levi9.internship.social.network.model.enums.FriendRequestStatus;
import com.levi9.internship.social.network.service.defaultservice.DefaultFriendRequestService;

public class FriendRequestServiceTest
{
	private final FriendRequestDao friendRequestDao;
	private final UserDao userDao;
	private final FriendshipService friendshipService;
	private final AuthService authService;
	private final FriendRequestService friendRequestService;
	private final ModelMapper modelMapper;

	public FriendRequestServiceTest()
	{
		userDao = mock(UserDao.class);
		friendRequestDao = mock(FriendRequestDao.class);
		friendshipService = mock(FriendshipService.class);
		authService = mock(AuthService.class);
		modelMapper = mock(ModelMapper.class);
		friendRequestService =
			new DefaultFriendRequestService(friendRequestDao, userDao, modelMapper, friendshipService, authService);
	}


	@Test
	public void createFriendRequestAlreadyExists()
	{
		//given
		final User receiver = getMockUser(TestConstants.USER_ID_1, TestConstants.USERNAME_1, TestConstants.USER_EMAIL_1, false);
		final User sender = getMockUser(TestConstants.USER_ID_2, TestConstants.USERNAME_2, TestConstants.USER_EMAIL_2, false);

		final FriendRequest friendRequest = getMockFriendRequest(TestConstants.FRIEND_REQUEST_ID, sender, receiver, FriendRequestStatus.PENDING);
		when(friendRequestDao.findAcceptedOrPendingFriendRequest(anyString(), anyString())).thenReturn(Optional.ofNullable(friendRequest));
		when(authService.getCurrentUser()).thenReturn(sender);
		when(userDao.findById(anyString())).thenReturn(Optional.ofNullable(receiver));

		//when
		final BusinessException exception = assertThrows(BusinessException.class, () -> friendRequestService.createRequestToAddFriend(receiver.getId()));

		//then
		assertEquals(ErrorCode.ERROR_CREATING_FRIEND_REQUEST, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("Friend request has already been created"));

	}
	@Test
	public void testAcceptFriendRequest()
	{
		//given
		final User receiver = getMockUser(TestConstants.USER_ID_1, TestConstants.USERNAME_1, TestConstants.USER_EMAIL_1, false);
		final User sender = getMockUser(TestConstants.USER_ID_2, TestConstants.USERNAME_2, TestConstants.USER_EMAIL_2, false);

		final FriendRequest currentRequestStatus = getMockFriendRequest(TestConstants.FRIEND_REQUEST_ID,sender, receiver, FriendRequestStatus.PENDING);
		final FriendRequest expectedRequestStatus = getMockFriendRequest(TestConstants.FRIEND_REQUEST_ID,sender, receiver, FriendRequestStatus.ACCEPTED);

		when(authService.getCurrentUser()).thenReturn(receiver);
		when(friendRequestDao.findByIdAndReceiver_Id(anyLong(), anyString())).thenReturn(Optional.ofNullable(currentRequestStatus));
		when(friendRequestDao.save(any(FriendRequest.class))).thenReturn(expectedRequestStatus);
		doNothing().when(friendshipService).saveFriendship(anyString(), anyString());

		//when
		friendRequestService.acceptFriendRequest((long)TestConstants.FRIEND_REQUEST_ID);

		//then
		verify(friendRequestDao,times(1)).findByIdAndReceiver_Id((long)TestConstants.FRIEND_REQUEST_ID, receiver.getId());
		verify(friendRequestDao,times(1)).save(any(FriendRequest.class));
		verify(friendshipService, times(1)).saveFriendship(receiver.getId(), sender.getId());

		assertEquals(FriendRequestStatus.ACCEPTED, friendRequestDao.findByIdAndReceiver_Id((long)TestConstants.FRIEND_REQUEST_ID, receiver.getId()).get().getStatus());
	}

	@Test
	public void testAcceptAlreadyAcceptedFriendRequest()
	{
		//given
		final User receiver = getMockUser(TestConstants.USER_ID_1, TestConstants.USERNAME_1, TestConstants.USER_EMAIL_1, false);
		final User sender = getMockUser(TestConstants.USER_ID_2, TestConstants.USERNAME_2, TestConstants.USER_EMAIL_2, false);

		final FriendRequest friendRequest = getMockFriendRequest(TestConstants.FRIEND_REQUEST_ID,sender, receiver, FriendRequestStatus.ACCEPTED);

		when(authService.getCurrentUser()).thenReturn(receiver);
		when(friendRequestDao.findByIdAndReceiver_Id(anyLong(), anyString())).thenReturn(Optional.ofNullable(friendRequest));

		//when
		final BusinessException exception = assertThrows(BusinessException.class, () -> friendRequestService.acceptFriendRequest((long)TestConstants.FRIEND_REQUEST_ID));

		//then
		verify(friendRequestDao,times(1)).findByIdAndReceiver_Id((long)TestConstants.FRIEND_REQUEST_ID, receiver.getId());
		verify(friendRequestDao,never()).save(any(FriendRequest.class));
		verify(friendshipService, never()).saveFriendship(receiver.getId(), sender.getId());

		assertEquals(ErrorCode.ERROR_ACCEPTING_FRIEND_REQUEST, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("Friend request is already processed"));

	}

	@Test
	public void testRejectFriendRequest()
	{
		//given
		final User receiver = getMockUser(TestConstants.USER_ID_1, TestConstants.USERNAME_1, TestConstants.USER_EMAIL_1, false);
		final User sender = getMockUser(TestConstants.USER_ID_2, TestConstants.USERNAME_2, TestConstants.USER_EMAIL_2, false);

		final FriendRequest currentRequestStatus = getMockFriendRequest(TestConstants.FRIEND_REQUEST_ID,sender, receiver, FriendRequestStatus.PENDING);
		final FriendRequest expectedRequestStatus = getMockFriendRequest(TestConstants.FRIEND_REQUEST_ID,sender, receiver, FriendRequestStatus.REJECTED);

		when(authService.getCurrentUser()).thenReturn(receiver);
		when(friendRequestDao.findByIdAndReceiver_Id(anyLong(), anyString())).thenReturn(Optional.ofNullable(currentRequestStatus));
		when(friendRequestDao.save(any(FriendRequest.class))).thenReturn(expectedRequestStatus);

		//when
		friendRequestService.rejectFriendRequest((long)TestConstants.FRIEND_REQUEST_ID);

		//then
		verify(friendRequestDao,times(1)).findByIdAndReceiver_Id((long)TestConstants.FRIEND_REQUEST_ID, receiver.getId());
		verify(friendRequestDao,times(1)).save(any(FriendRequest.class));
		verify(friendshipService, never()).saveFriendship(receiver.getId(), sender.getId());

		assertEquals(FriendRequestStatus.REJECTED, friendRequestDao.findByIdAndReceiver_Id((long)TestConstants.FRIEND_REQUEST_ID, receiver.getId()).get().getStatus());
	}

	protected User getMockUser(final String userId, final String username, final String email, final boolean isAdmin)
	{
		final User user = new User();
		user.setAdmin(isAdmin);
		user.setEmail(email);
		user.setUsername(username);
		user.setId(userId);
		return user;
	}

	protected FriendRequest getMockFriendRequest(final long id, final User sender, final User receiver, final FriendRequestStatus status)
	{
		final FriendRequest friendRequest = new FriendRequest();
		friendRequest.setId(id);
		friendRequest.setSender(sender);
		friendRequest.setReceiver(receiver);
		friendRequest.setStatus(status);
		return friendRequest;
	}
}
