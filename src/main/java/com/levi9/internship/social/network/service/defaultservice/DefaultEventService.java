package com.levi9.internship.social.network.service.defaultservice;

import java.time.LocalDateTime;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.levi9.internship.social.network.dao.EventDao;
import com.levi9.internship.social.network.dao.EventRespondDao;
import com.levi9.internship.social.network.dao.GroupDao;
import com.levi9.internship.social.network.dao.GroupMembershipDao;
import com.levi9.internship.social.network.dao.LocationDao;
import com.levi9.internship.social.network.dao.UserDao;
import com.levi9.internship.social.network.dto.EventRequest;
import com.levi9.internship.social.network.dto.EventRespondResponse;
import com.levi9.internship.social.network.dto.EventResponse;
import com.levi9.internship.social.network.dto.MemberResponse;
import com.levi9.internship.social.network.exceptions.BusinessException;
import com.levi9.internship.social.network.exceptions.ErrorCode;
import com.levi9.internship.social.network.model.Event;
import com.levi9.internship.social.network.model.EventRespond;
import com.levi9.internship.social.network.model.Group;
import com.levi9.internship.social.network.model.Location;
import com.levi9.internship.social.network.model.enums.EventRespondType;
import com.levi9.internship.social.network.service.AuthService;
import com.levi9.internship.social.network.service.EmailService;
import com.levi9.internship.social.network.service.EventService;
import com.levi9.internship.social.network.service.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@Service
public class DefaultEventService implements EventService
{

	private final EventDao eventDao;
	private final GroupDao groupDao;
	private final GroupMembershipDao groupMembershipDao;
	private final LocationDao locationDao;
	private final UserDao userDao;
	private final EventRespondDao eventRespondDao;
	private final ModelMapper modelMapper;
	private final EmailService emailService;
	private final GroupService groupService;
	private final AuthService authService;

	@Override
	public EventRespondResponse attendEvent(final String userId, final Long groupId, final Long eventId)
	{
		groupDao.findById(groupId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_ATTENDING_EVENT, "Group does not exist."));
		groupMembershipDao.findByUserIdAndGroupId(userId, groupId)
			.orElseThrow(() -> new BusinessException(
				ErrorCode.ERROR_ATTENDING_EVENT,
				"Current user does not belong to provided group."));

		if (eventDao.findByIdAndGroupId(eventId, groupId).isEmpty())
		{
			throw new BusinessException(ErrorCode.ERROR_ATTENDING_EVENT, "Provided event not found in provided group.");
		}

		if (eventDao.findCreatorOfEvent(eventId).getId().equals(userId))
		{
			throw new BusinessException(ErrorCode.ERROR_ATTENDING_EVENT, "Creator of event with provided id already attends it.");
		}

		final EventRespond eventRespond = new EventRespond();
		eventRespond.setEvent(eventDao.findById(eventId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_ATTENDING_EVENT, "Event does not exist.")));
		eventRespond.setUser(userDao.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_ATTENDING_EVENT, "User does not exist.")));
		eventRespond.setRespondType(EventRespondType.ACCEPTED);

		if (eventRespondDao.findEventRespondByUserIdAndEventId(userId, eventId).isPresent())
		{
			throw new BusinessException(ErrorCode.ERROR_ATTENDING_EVENT, "User with provided id already attends given event");
		}

		eventRespondDao.saveAndFlush(eventRespond);

		return modelMapper.map(eventRespond, EventRespondResponse.class);
	}

	@Transactional
	@Override
	public EventResponse create(final String userId, final Long groupId, final EventRequest request)
	{
		try
		{
			log.info("Creating event for user {} in group {}: {}", userId, groupId, request.toString());
			final Group group = groupDao.findById(groupId)
				.orElseThrow(() -> new BusinessException("Group does not exist."));
			groupMembershipDao.findByUserIdAndGroupId(userId, groupId)
				.orElseThrow(() -> new BusinessException("Current user does not belong to provided group."));

			//saving location
			final Location location = new Location();
			modelMapper.map(request.getLocation(), location);
			locationDao.saveAndFlush(location);
			//saving event
			final Event event = new Event();
			modelMapper.map(request, event);
			event.setGroup(group);
			event.setLocation(location);
			event.setCreatedBy(userDao.findById(userId).get());
			eventDao.saveAndFlush(event);

			log.info("Notifying other group members about new event via email");
			final List<MemberResponse> groupMembers = groupService.findMembersOfGroup(userId, group.getId());
			groupMembers.removeIf(member -> member.getUsername().equals(authService.getCurrentUser().getUsername()));
			emailService.notifyGroupMembersAboutNewEvent(event, group, groupMembers);

			return modelMapper.map(event, EventResponse.class);
		} catch (final Exception e)
		{
			throw new BusinessException(ErrorCode.ERROR_CREATING_EVENT, e.getMessage());
		}
	}

	@Transactional
	@Override
	public void delete(final String userId, final Long groupId, final Long eventId)
	{
		log.info("DefaultEventService: Deleting event: {%d} from group: {%d}".formatted(eventId, groupId));
		final Group group = groupDao.findById(groupId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_EVENT, "Group does not exist."));
		final Event event = eventDao.findById(eventId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_EVENT, "Event does not exist"));
		eventDao.findByIdAndGroupId(eventId, groupId)
			.orElseThrow(() -> new BusinessException(
				ErrorCode.ERROR_DELETING_EVENT,
				"Event with provided id does not belong to provided group."));
		if (group.getAdmin().getId().equals(userId) || event.getCreatedBy().getId().equals(userId))
		{
			eventDao.deleteById(eventId);
		}
		else
		{
			throw new BusinessException(
				ErrorCode.ERROR_DELETING_EVENT,
				"Current user is not creator of event, or admin of the group.");
		}
	}

	@Transactional
	@Scheduled(fixedRateString = "${FIXED_RATE_SCHEDULER:86400000}")
	public void deleteExpiredEvent()
	{
		eventDao.deleteExpiredEvent(LocalDateTime.now());
	}
}
