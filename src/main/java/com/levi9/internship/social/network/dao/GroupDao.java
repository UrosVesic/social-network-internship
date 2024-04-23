package com.levi9.internship.social.network.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.levi9.internship.social.network.model.Group;

public interface GroupDao extends JpaRepository<Group, Long>
{

	Optional<Group> findByIdAndAdminId(Long groupId, String adminId);

	@Query(value = QueryConstants.GET_GROUP_BY_NAME)
	List<Group> findGroupsByName(@Param("groupName") String name);

	Optional<Group> findGroupById(Long groupId);
}
