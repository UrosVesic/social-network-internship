package com.levi9.internship.social.network.dto;

import lombok.Data;

@Data
public class LocationResponse
{

	private Long id;
	private String address;
	private String city;
	private String country;
	private Integer postcode;
}
