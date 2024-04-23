package com.levi9.internship.social.network.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.levi9.internship.social.network.model.HiddenFrom;

public interface HiddenFromDao extends JpaRepository<HiddenFrom, Long>
{

	@Query(value = QueryConstants.CHECK_IF_HIDDEN_FROM_USER)
	Optional<HiddenFrom> findByUserAndPostId(@Param("userId") String userId, @Param("postId") Long postId);
}
