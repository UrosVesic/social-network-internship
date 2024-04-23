package com.levi9.internship.social.network.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Friendship
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user1_id", nullable = false)
	private User user1;

	@ManyToOne
	@JoinColumn(name = "user2_id", nullable = false)
	private User user2;

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime friendsSince;
}
