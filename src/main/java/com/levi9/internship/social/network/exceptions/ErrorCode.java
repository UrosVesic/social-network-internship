package com.levi9.internship.social.network.exceptions;

public enum ErrorCode
{
	//iam provider

	ERROR_DURING_SIGN_UP,
	ERROR_DURING_SIGN_IN,
	ERROR_DURING_SIGN_OUT,
	ERROR_DURING_PASSWORD_RESET,
	ERROR_SENDING_CONFIRMATION_CODE,

	//events
	ERROR_CREATING_EVENT,
	ERROR_ATTENDING_EVENT,
	ERROR_DELETING_EVENT,

	//comments
	ERROR_CREATING_COMMENT,
	ERROR_REPLYING_ON_COMMENT,
	ERROR_DELETING_COMMENT,

	//groups
	ERROR_GETTING_GROUP_MEMBERS,
	ERROR_DELETING_GROUP_MEMBER,
	ERROR_GETTING_GROUP,
	ERROR_UPDATING_GROUP,
	ERROR_DELETING_GROUP,
	ERROR_JOINING_GROUP,
	ERROR_APPROVING_GROUP_MEMBERSHIP,
	ERROR_VALIDATING_GROUP_MEMBERSHIP_REQUEST,
	ERROR_INVITING_TO_GROUP,
	ERROR_CONFIRMING_INVITATION,
	ERROR_APPROVING_INVITATION,
	ERROR_REJECTING_INVITATION,

	//posts
	ERROR_GETTING_POST,
	ERROR_CREATING_POST,
	ERROR_HIDING_POST,
	ERROR_DELETING_POST,
	ERROR_DOWNLOADING_IMAGE,

	//friend requests
	ERROR_CREATING_FRIEND_REQUEST,
	ERROR_ACCEPTING_FRIEND_REQUEST,
	ERROR_REJECTING_FRIEND_REQUEST,

	//friendships
	ERROR_DELETING_FRIEND,
	ERROR_GETTING_FRIENDS, ERROR_GETTING_FRIEND,

	ERROR_ACCESS_DENIED,
	ERROR_INVALID_PARAMETERS,
	RUNTIME_EXCEPTION_OCCURED,
	UNHANDLED_EXCEPTION_OCCURED,

	//emails
	ERROR_SENDING_EMAIL;

}
