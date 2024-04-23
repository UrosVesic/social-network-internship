package com.levi9.internship.social.network.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupRequest
{

	@NotEmpty(message = "Group name should not be empty")
	@Size(min = 2, message = "Group name should be at least 2 characters long")
	private String name;

	@NotNull(message = "Group visibility should not be empty")
	private boolean nonPublic;
}
