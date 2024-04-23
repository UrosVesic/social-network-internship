package com.levi9.internship.social.network.dao;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.levi9.internship.social.network.model.Post;


public interface PostDao extends JpaRepository<Post, Long> {

    @Modifying
    @Query(nativeQuery = true, value = QueryConstants.DELETE_EXPIRED_POST)
    void deleteExpiredPost(@Param("currentDate") LocalDateTime currentDate);
}
