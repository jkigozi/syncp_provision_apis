package services;

import util.APIGateway;
import entities.Group;
import entities.User;


/**
 * Class for requests to group.svc and groups.svc
 *
 */
public class GroupsService 
	extends APIGateway {


	/**
	 * Gets or sets url to Groups service.
	 */
	protected static String groupsUrl;

	/**
	 * Gets or sets url to Group service.
	 */
	protected static String groupUrl;

	static {
		groupsUrl = provisioningAPIUrlPrefix + "groups.svc/%s/groups";
		groupUrl = provisioningAPIUrlPrefix + "group.svc/%s";
	}

	/**
	 * Creates new groups in company.
	 * 
	 * @param companyGuid Company Guid.
	 * @param groups Array of groups to be created.
	 * 
	 * @return Array of created groups.
	 */
	public static Group createGroup(String companyGuid, Group group) {
		return httpPost(String.format(groupsUrl, companyGuid), "application/json", group );
	}
	
	public static Group[] createGroups(String companyGuid, Group[] groups) {
		return httpPost(String.format(groupsUrl, companyGuid), "application/json", groups );
	}
	
	/**
	 * Checks if groups Already Exists in company.
	 * 
	 * @param ownerEmail Owner Email.
	 * @param grpID Group Id.
	 * 
	 * @return Boolean true/false for exists/does not exist.
	 */
	//public static Group getGroup(String grpGuid,boolean suppressErrors ) {
	public static Group[] getGroup(String grpGuid) {
		return httpGet(String.format(groupUrl, grpGuid), Group[].class );
	}

	public static Group[] getGroups(String companyId) {
		return httpGet(String.format(groupsUrl, companyId), Group[].class );
	}
	
	/**
	 * Deletes group by Guid.
	 * 
	 * @param groupGuid Group Guid.
	 */
	public static void deleteGroup(String groupGuid) {
		httpDelete(String.format(groupUrl, groupGuid), Group.class);
	}
}
