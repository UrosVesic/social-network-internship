package com.levi9.internship.social.network.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.levi9.internship.social.network.model.enums.JoinGroupRequestStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class JoinGroupRequest
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@Column(columnDefinition = "enum('ACCEPTED','REJECTED','PENDING')")
	@Enumerated(EnumType.STRING)
	private JoinGroupRequestStatus respondType;
}
