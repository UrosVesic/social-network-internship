package com.levi9.internship.social.network.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse
{

	private Long id;
	private String name;
	private String admin;
	private boolean nonPublic;
}
