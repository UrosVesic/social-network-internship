package com.levi9.internship.social.network.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.levi9.internship.social.network.model.GroupMembership;
import com.levi9.internship.social.network.model.User;

public interface GroupMembershipDao extends JpaRepository<GroupMembership, Long>
{

	Optional<GroupMembership> findByUserId(String userId);

	@Query(value = QueryConstants.GET_GROUP_MEMBERS)
	List<User> getMembersOfGroup(@Param("groupId") Long groupId);

	Optional<GroupMembership> findByUserIdAndGroupId(String userId, Long groupId);
}
