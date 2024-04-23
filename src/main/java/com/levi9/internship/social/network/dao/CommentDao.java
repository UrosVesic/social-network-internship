package com.levi9.internship.social.network.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.levi9.internship.social.network.model.Comment;

public interface CommentDao extends JpaRepository<Comment, Long>
{
	Optional<Comment> findByPost_IdAndId(final Long postId, final Long commentId);
}
