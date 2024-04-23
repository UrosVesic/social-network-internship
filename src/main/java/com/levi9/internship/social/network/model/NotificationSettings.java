package com.levi9.internship.social.network.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import com.levi9.internship.social.network.model.enums.NotificationTurnoffType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class NotificationSettings
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(columnDefinition = "enum('TEMPORARILY','PERMANENTLY')")
	@Enumerated(EnumType.STRING)
	private NotificationTurnoffType type;

	private LocalDateTime turnedOffUntil;
}
