package com.levi9.internship.social.network.dao;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.levi9.internship.social.network.model.Event;
import com.levi9.internship.social.network.model.User;

public interface EventDao extends JpaRepository<Event, Long>
{

	Optional<Event> findByIdAndGroupId(Long eventId, Long groupId);

	@Query(value = QueryConstants.GET_EVENT_CREATOR)
	User findCreatorOfEvent(@Param("eventId") Long eventId);

	@Modifying
	@Query(value = QueryConstants.DELETE_EXPIRED_EVENT)
	void deleteExpiredEvent(@Param("eventTime") LocalDateTime eventTime);
}
