package com.levi9.internship.social.network.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.levi9.internship.social.network.model.JoinGroupRequest;

public interface JoinGroupRequestDao extends JpaRepository<JoinGroupRequest, Long>
{

	@Query(value = QueryConstants.GET_JOIN_REQUEST_BY_USER_AND_GROUP)
	Optional<JoinGroupRequest> findRequestByUserIdAndGroupId(@Param("groupId") Long groupId, @Param("userId") String userId);

	@Query(value = QueryConstants.GET_UNHANDLED_INVITATIONS)
	Optional<JoinGroupRequest> findAnyUnhandledInvitations(@Param("groupId") Long groupId, @Param("userId") String userId);

	@Modifying
	@Query(nativeQuery = true, value = QueryConstants.DELETE_BY_USER_AND_GROUP_ID)
	void deleteByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") String userId);
}
