package com.levi9.internship.social.network.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinGroupResponse
{
	private final Result result;
	private final Long joinGroupRequestId;

	public enum Result
	{
		CREATED_REQUEST_TO_JOIN_GROUP,
		USER_ADDED_TO_GROUP
	}
}
