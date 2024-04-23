package com.levi9.internship.social.network.dao;

public class QueryConstants
{
	public static final String GET_EVENT_CREATOR = "SELECT e.createdBy FROM Event e WHERE e.id = :eventId";
	public static final String DELETE_EXPIRED_EVENT = "DELETE FROM Event e WHERE e.eventTime < :eventTime";
	public static final String GET_ACCEPTED_OR_PENDING_FRIEND_REQUEST_FOR_USERS = """
		SELECT fr FROM FriendRequest fr \
		WHERE ((fr.sender.id = :user1 AND fr.receiver.id = :user2) OR (fr.sender.id = :user2 AND fr.receiver.id = :user1)) \
		AND (fr.status = 'ACCEPTED' OR fr.status = 'PENDING')\
		""";
	public static final String GET_ALL_PENDING_REQUESTS =
		"SELECT fr FROM FriendRequest fr WHERE fr.status = 'PENDING' AND fr.receiver.id = :userId";
	public static final String DELETE_FRIEND_REQUEST_BY_USER =
		"""
		DELETE FROM FriendRequest fr WHERE ((fr.sender.id = :senderId AND fr.receiver.id = :receiverId) \
		OR (fr.sender.id = :receiverId AND fr.receiver.id = :senderId))\
		""";
	public static final String GET_ALL_FRIENDS =
		"""
		SELECT u FROM User u WHERE u.id IN (SELECT f1.user1.id FROM Friendship f1 WHERE f1.user2.id = :userId) \
		OR u.id IN (SELECT f2.user2.id FROM Friendship f2 WHERE f2.user1.id = :userId)\
		""";
	public static final String GET_FRIENDSHIP_STATUS =
		"""
		SELECT f FROM Friendship f WHERE (f.user1.id = :senderId AND f.user2.id = :receiverId) OR (f.user1.id = :receiverId AND f\
		.user2.id = :senderId)\
		""";
	public static final String FIND_FRIENDS = """
		SELECT u FROM User u WHERE u.id IN \
		(SELECT f1.user1.id FROM Friendship f1 WHERE f1.user2.id = :userId AND (LOWER(f1.user1.username) LIKE \
		%:friendUsername% \
		OR LOWER(f1.user1.username ) LIKE ''))\
		OR u.id IN (SELECT f2.user2.id FROM Friendship f2 WHERE f2.user1.id = :userId AND (LOWER(f2.user2.username) LIKE \
		%:friendUsername% OR LOWER(f2.user2.username ) LIKE ''))\
		""";
	public static final String GET_GROUP_BY_NAME =
		"SELECT g FROM Group g WHERE LOWER(g.name) LIKE %:groupName% or LOWER(g.name) LIKE ''";
	public static final String GET_GROUP_MEMBERS = "SELECT gm.user FROM GroupMembership gm WHERE gm.group.id = :groupId";
	public static final String CHECK_IF_HIDDEN_FROM_USER =
		"SELECT hf FROM HiddenFrom hf WHERE hf.user.id = :userId AND hf.post.id = :postId";
	public static final String GET_JOIN_REQUEST_BY_USER_AND_GROUP =
		"SELECT r FROM JoinGroupRequest r WHERE r.group.id = :groupId AND r.user.id = :userId";
	public static final String GET_UNHANDLED_INVITATIONS =
		"""
		SELECT r FROM JoinGroupRequest r WHERE (r.respondType = 'PENDING_INVITATION_FRIEND' OR r.respondType = \
		'PENDING_INVITATION_ADMIN' OR r.respondType = 'WAITING_FOR_ADMIN_APPROVAL') \
		AND r.user.id = :userId AND r.group.id = :groupId\
		""";
	public static final String DELETE_BY_USER_AND_GROUP_ID =
		"delete from join_group_request where group_id = :groupId and user_id = :userId";
	public static final String DELETE_EXPIRED_POST =
		"DELETE FROM post p WHERE date_add(p.created_at, interval 24 hour) < :currentDate";
}
