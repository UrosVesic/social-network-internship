package com.levi9.internship.social.network.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.levi9.internship.social.network.model.EventRespond;

public interface EventRespondDao extends JpaRepository<EventRespond, Long>
{

	Optional<EventRespond> findEventRespondByUserIdAndEventId(String userId, Long eventId);
}
