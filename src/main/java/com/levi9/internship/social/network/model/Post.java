package com.levi9.internship.social.network.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Post
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "Text")
	private String content;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User createdBy;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;

	@OneToMany(mappedBy = "post")
	private List<Comment> comments;

	@Column(name = "s3_file_key")
	private String s3FileKey;

	@CreationTimestamp
	private LocalDateTime createdAt;

	private LocalDateTime deletedAt;

	private boolean isPrivate;
}
