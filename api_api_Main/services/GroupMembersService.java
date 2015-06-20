package services;

import util.APIGateway;
import entities.Group;
import entities.User;

/**
 * Class for requests to group_members.svc and group_member.svc
 *
 */
public class GroupMembersService 
	extends APIGateway {

	/**
	 * Gets or sets url to GroupMembers service.
	 */
	protected static String groupMembersUrl;

	/**
	 * Gets or sets url to GroupMember service.
	 */
	protected static String groupMemberUrl;

	static  {
		groupMembersUrl = provisioningAPIUrlPrefix + "group_members.svc/%s";
		groupMemberUrl = provisioningAPIUrlPrefix + "group_member.svc/%s/member/%s";
	}

	/**
	 * Adds users to group.
	 * 
	 * @param groupGuid Group Guid.
	 * @param users Array of users.
	 * 
	 * @return Array of added users.
	 */
	public static User[] addGroupMembers(String groupGuid, User[] users) {
		return httpPost(String.format(groupMembersUrl, groupGuid), "application/json", users );
	}
	
	/*public static User addGroupMember(String groupGuid, User user) {
		return (User)httpPost(String.format(groupMemberUrl, groupGuid), "application/json", user );
		
	}*/
	
	public static User addGroupMember(String groupGuid, User user) {
		//return (User)httpPost(String.format(groupMemberUrl, groupGuid), "application/json", user );
		return httpPost(String.format(groupMemberUrl, new Object[] { groupGuid }), "application/json", user);
		
	}

	public static void addMemberToGroup(String groupGuid, String userEmail) {
			
			httpPost(String.format(groupMemberUrl, groupGuid, userEmail),"application/json",Group.class );
	}

	/**
	 * Deletes user from group.
	 * 
	 * @param groupGuid Group Guid.
	 * @param userEmail Email of deleted group member.
	 */
	public static void deleteGroupMember(String groupGuid, String userEmail) {
		
		httpDelete(String.format(groupMemberUrl, groupGuid, userEmail), Group.class);
	}
}
