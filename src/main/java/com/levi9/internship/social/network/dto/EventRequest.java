package com.levi9.internship.social.network.dto;

import java.time.LocalDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventRequest
{

	@NotEmpty(message = "Name field should not be empty.")
	@Size(min = 2, message = "Name field should be at least 2 characters long")
	private String name;

	public void setName(final String name)
	{
		this.name = name.trim();
	}

	@NotEmpty(message = "Description field should not be empty.")
	@Size(min = 2, message = "Description field should be at least 2 characters long")
	private String description;

	public void setDescription(final String description)
	{
		this.description = description.trim();
	}

	@Valid
	private LocationRequest location;

	@Future(message = "Date field should be in future.")
	@NotNull(message = "Date should not be empty.")
	private LocalDateTime eventTime;
}
