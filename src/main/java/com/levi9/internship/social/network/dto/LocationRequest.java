package com.levi9.internship.social.network.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationRequest
{

	@NotEmpty(message = "Address field should not be empty.")
	@Size(min = 2, message = "Address field should be at least 2 characters long")
	private String address;

	@NotEmpty(message = "City field should not be empty.")
	@Size(min = 2, message = "City field should be at least 2 characters long")
	private String city;

	@NotEmpty(message = "Country field should not be empty.")
	@Size(min = 2, message = "Country field should be at least 2 characters long")
	private String country;

	@NotNull(message = "Postcode field should not be empty.")
	private Integer postcode;
}
