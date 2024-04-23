package com.levi9.internship.social.network.service.defaultservice;

import java.util.List;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.levi9.internship.social.network.constants.ApplicationConstants;
import com.levi9.internship.social.network.dto.FriendResponse;
import com.levi9.internship.social.network.dto.MemberResponse;
import com.levi9.internship.social.network.model.Event;
import com.levi9.internship.social.network.model.Group;
import com.levi9.internship.social.network.model.Post;
import com.levi9.internship.social.network.service.EmailService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultEmailService implements EmailService
{
	private final Environment env;
	private final JavaMailSender mailSender;
	private final String emailFrom;
	private final String backendFqdn;
	private final String contextPath;

	public DefaultEmailService(final Environment env, final JavaMailSender mailSender)
	{
		this.env = env;
		this.mailSender = mailSender;
		this.emailFrom = env.getProperty("spring.mail.properties.from");
		this.backendFqdn = env.getProperty("backend.fqdn");
		this.contextPath = env.getProperty("server.servlet.context-path");
	}

	@Override
	@Async
	public void notifyFriendsAboutNewPost(final Post post, final List<FriendResponse> friendsToBeNotified)
	{
		final String emailBody = String.format("Your friend %s created new post, check it out: %s",
			post.getCreatedBy().getUsername(), getProfilePostUrl(post.getId()));
		friendsToBeNotified.forEach(friend -> sendEmail(ApplicationConstants.NEW_POST, emailBody, emailFrom, friend.getEmail()));
	}

	@Override
	@Async
	public void notifyGroupMembersAboutNewPost(final Post post, final Group group, final List<MemberResponse> usersToBeNotified)
	{
		final String emailBody = String.format("New post in group %s created by %s. Check it out: %s",
			group.getName(), post.getCreatedBy().getUsername(), getGroupPostUrl(group.getId(), post.getId()));
		usersToBeNotified
			.forEach(user -> sendEmail(ApplicationConstants.NEW_POST, emailBody, emailFrom, user.getEmail()));
	}

	@Override
	@Async
	public void notifyGroupMembersAboutNewEvent(final Event event, final Group group, final List<MemberResponse> usersToBeNotified)
	{
		final String emailBody = String.format("New event in group %s created by %s. Check it out: %s",
			group.getName(), event.getCreatedBy().getUsername(), getGroupEventUrl(group.getId(), event.getId()));
		usersToBeNotified
			.forEach(user -> sendEmail(ApplicationConstants.NEW_EVENT, emailBody, emailFrom, user.getEmail()));
	}

	private void sendEmail(final String subject, final String body, final String from, final String to)
	{
		try
		{
			log.info("Sending email with subject {} to {}", subject, to);
			final MimeMessage mimeMessage = mailSender.createMimeMessage();
			final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setSubject(subject);
			messageHelper.setTo(to);
			messageHelper.setFrom(from);
			messageHelper.setText(body);
			mailSender.send(mimeMessage);
		} catch (final MessagingException e)
		{
			log.error("Error sending email with subject {} to {}: {}", subject, to, e.getMessage());
		}
	}

	private String getProfilePostUrl(final Long postId)
	{
		final String profilePostUrl = backendFqdn + contextPath + "/api/posts/%s";
		return profilePostUrl.formatted(postId);
	}

	private String getGroupPostUrl(final Long groupId, final Long postId)
	{
		final String groupPostUrl = backendFqdn + contextPath + "/api/groups/%s/posts/%s";
		return groupPostUrl.formatted(groupId, postId);
	}

	private String getGroupEventUrl(final Long groupId, final Long eventId)
	{
		final String groupEventUrl = backendFqdn + contextPath + "/api/groups/%s/events/%s";
		return groupEventUrl.formatted(groupId, eventId);
	}
}
