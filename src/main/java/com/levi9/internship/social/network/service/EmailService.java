package com.levi9.internship.social.network.service;

import java.util.List;
import com.levi9.internship.social.network.dto.FriendResponse;
import com.levi9.internship.social.network.dto.MemberResponse;
import com.levi9.internship.social.network.model.Event;
import com.levi9.internship.social.network.model.Group;
import com.levi9.internship.social.network.model.Post;

public interface EmailService
{
	void notifyFriendsAboutNewPost(final Post post, final List<FriendResponse> friendsToBeNotified);

	void notifyGroupMembersAboutNewPost(final Post post, final Group group, final List<MemberResponse> usersToBeNotified);

	void notifyGroupMembersAboutNewEvent(final Event event, final Group group, final List<MemberResponse> usersToBeNotified);
}
