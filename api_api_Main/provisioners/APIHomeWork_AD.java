package provisioners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import services.GroupMembersService;
import services.GroupsService;
import services.UsersService;
import util.APIContext;
import util.ConfigurationHelper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import entities.Group;
import entities.User;
import entities.UserAccountType;



public class APIHomeWork_AD {
		//Array to track all users as they are created
	private static User[] createdUsers;
	
		//List to track all users as they are added to their respective groups
	private static List<User> groupMemberUsers = new ArrayList<User>();
	
		//ArrayList to track all groups as they are created
	//private static Group[] createdGroups;
	private static ArrayList<Group> createdGroups = new  ArrayList<Group>();
	
		//Array to store retrieved groups so as to get the Syncplicity group IDs
	private static Group[] all_groups;
	
		//Multikey hashmap to hold user/group member combinations where user belongs to more than one group
	private static Multimap<String,String> multiEmail_GName = ArrayListMultimap.create();
	
		//Hashmap to track combination of group name and group id to use for cross-reference look up of group id in array email_gname
	private static HashMap<String,String> gid_gname = new HashMap<String,String>();
	
		//Hashmap to track combination of group name and userEmail id during user creation from csv to use for cross-reference look up of group id in array gid_gname
	private static HashMap<String,String> email_gname = new HashMap<String,String>();
	
	
	//Multimap to track combination of group name and userEmail id during user creation from csv to use for cross-reference look up of group id in array gid_gname
	private static Multimap<String,String> gid_gname_to_grp = ArrayListMultimap.create();
	
	private static Group[] cloudGroups;
	
	//private static String url = ConfigurationHelper.getLdapUrl();
	private static String url = "LDAP://win2k8-R2.johnk.local:389";
	//private static String usr = ConfigurationHelper.getAdminCn();
	private static String usr = "CN=Administrator,CN=Users,DC=johnk,DC=local";
	//private static String pas = ConfigurationHelper.getAdminPass();
	private static String pas = "Francisco!2";
	//private static String search_fltr = ConfigurationHelper.all_search_filters();
	//private static String search_fltr = "(&(objectClass=user)(memberOf=CN=apidev,CN=Users,DC=johnk,DC=local)(mail=*))";
	//private static String search_fltr = "(objectClass=group)";

	//private static String search_base = ConfigurationHelper.all_search_bases();
	//private static String search_base = "CN=Support,OU=SyncplicityUsers,DC=support12r2,DC=com";
	private static String search_base = "OU=Izogeek,DC=johnk,DC=local";
	//private static String search_base = "CN=Users,DC=johnk,DC=local";
 
	public static void execute() {

		createUsers();
		createGroups();
		addUsersToGroups();
		removeUsersFromGroup();
		deleteGroups();
		deleteUsers();
	}

	
	private static void deleteGroups() {
		System.out.println("");
		
		if (createdGroups == null || createdGroups.size()==0) {
			
			return;
		}

		System.out.println("");
		System.out.println("Press enter before Group Deletion...");

		System.out.println("");

		
		for (Group grpp: createdGroups)
		{
			System.out.println(String.format("Removing Group  [%s].", grpp.Id)); 
			GroupsService.deleteGroup(grpp.Id);
		}

	}

	private static void removeUsersFromGroup() {
		System.out.println("");
		
			//Check if list of members added to respective groups during this run is populated. Return if empty 
		if ((groupMemberUsers == null) || (groupMemberUsers.size() == 0)) {
			
			return;
		}

		System.out.println("");
		System.out.println("Press enter before Group Members Deletion...");

		System.out.println("");
		System.out.println("Start Group Member Deletion...");
			
				//Loop through added to group user list to remove email ids from respective groups
		
		for (Map.Entry<String, String> ttt: gid_gname_to_grp.entries())
		{
			System.out.println(String.format("Removing User ==>%s<== from Group ==>%s<==", ttt.getValue(), ttt.getKey()));
		}

			for (Map.Entry<String,String> entry: gid_gname_to_grp.entries())
			{
			
				System.out.println("");
					
				System.out.println(String.format("Removing user[%s] from group [%s].", new Object[] {
						entry.getKey(), entry.getValue()
					}));
				
				GroupMembersService.deleteGroupMember(entry.getKey().toString().trim(),entry.getValue().toString().trim());
							
			}

		System.out.println("");
		System.out.println("Finish Group Member Deletion.");
	}

private static void addUsersToGroups() {		
		System.out.println("");
		
		//Check if list of Members Created During this Run is Populated. Return if Empty 
		if ((createdUsers == null) || (createdUsers.length == 0)) 
		{
		
			return;
		}
		
		System.out.println("Start Adding Group Members...");
		
			//Initialize Array to Hold User to be Added
		User[] thisCreatedUser = new User[1];
		
		//Otherwise, loop through all members added to their respective groups during this run
		for (User usr: createdUsers)
		{
			System.out.println(String.format(""));
				//And for each User, loop through the emailID/groupName hashmap and get groupName for email ID
			for (Map.Entry<String,String> entry1: multiEmail_GName.entries())
			{
				
				if (entry1.getKey().toString().trim().equals(usr.EmailAddress.toString().trim()))

				{
		
					//gid_gname==(group.Name, group.Id)	multiEmail_GName==(EmailAddress,group_name)
					
						//Loop Through gid_gname Hashmap
					for(Map.Entry<String,String> entry2: gid_gname.entrySet())
					{
								//To Get corresponding Syncplicity group ID that matches group name
							if (entry2.getKey().toString().trim().equals(entry1.getValue().toString().trim()))
							{
									//Copy this User to Array that is a parameter to the API call When Adding User to Group
								thisCreatedUser[0] = usr;
								
								System.out.println(String.format("Adding User : %s to Group %s", usr.EmailAddress,entry2.getValue() ));
								
								GroupMembersService.addGroupMembers(entry2.getValue(), thisCreatedUser);
								
								
								groupMemberUsers.add(usr);
								gid_gname_to_grp.put(entry2.getValue().toString().trim(), usr.EmailAddress.toString().trim());
								System.out.println(String.format("A total of %d has been Added to Group(s)", groupMemberUsers.size() ));
								
							}
					}
				}
			}
		}
						
}		

	private static void createGroups() {
		
		//List to 
		ArrayList<Group> grpsToAdd = new ArrayList<Group>();
		System.out.println("");

		String companyGuid = APIContext.getCompanyGuid();

		if ((companyGuid == null) || (companyGuid.isEmpty())) {
			System.err.println("Group Can't Be Created Because Company Guid Wasn't Received During Authorization.");
			return;
		}

		System.out.println("Starting Group Creation...");
		
		System.out.println("\tRetrieve Existing Groups in Cloud.");
			 
			cloudGroups = GroupsService.getGroups(companyGuid);
			
			System.out.println(String.format("\t[%s] of the Groups you Want to Create Already Exist", cloudGroups.length));

			System.out.println("");
		
		//Load Groups from Active Directory as specified in Config File
			
			try
			{
			
			String search_fltr = "(objectClass=group)";
			
			List<String> attr_frm_ad = new ArrayList<String>();			
			
			ADConnect adc = new ADConnect();
			
			attr_frm_ad = adc.getGroups(adc.createContext(url, usr, pas), search_base, search_fltr);	
				
				
			//Loop Through List of Groups Retrieved from AD to Create them in Syncplicity
				for(String ad_group: attr_frm_ad)
				{
					System.out.println(String.format(ad_group));
				
					Group group = new Group();
					group.Name = ad_group;
					group.Owner = new User();
	
					group.Owner.EmailAddress = ConfigurationHelper.getOwnerEmail();			
				
					
					for (Group grp: cloudGroups)
					{
						if (grp.Name.equals(group.Name)){
							System.out.println(String.format("\tGroup [%s] with ID %s Already Exists in Cloud", group.Name, grp.Id));

							gid_gname.put(group.Name, grp.Id);
						}
								//Update GroupName-GroupID Tracking Array

							gid_gname.put(group.Name, group.Id);

						}
										
					//Add Group in this Iteration to temp Tracking List
					
					grpsToAdd.add(group);	
			}
				
				} catch (NamingException nee) {
					nee.printStackTrace();
				}		
	
			//System.out.println("Creating Groups..........");
			
			//Convert Temp List to Array of Group and Call API to Create Groups
			
			GroupsService.createGroups(companyGuid, grpsToAdd.toArray(new Group[grpsToAdd.size()]));
			
				//Make Another API Call to retrieve Cloud Groups now updated
			all_groups = GroupsService.getGroups(companyGuid);
			
				//Rebuild Array to track all GroupName-GroupID combinations
			for (Group gp: all_groups){
			
				gid_gname.put(gp.Name, gp.Id);
				createdGroups.add(gp);
				
			}

	}

	
	
	 private static void createUsers()
	  {

		List<User> usersToAdd = new ArrayList<User>();
		String track_usr = "";
		try
		{
			String search_fltr = "(objectClass=user)";
			
					
			ADConnect adc = new ADConnect();

			List<ADConnect.AdUser> attr_frm_ad = new ArrayList<ADConnect.AdUser>();
			attr_frm_ad = adc.getUserData(adc.createContext(url, usr, pas), search_base, search_fltr);
			
			System.out.println("");

			for(ADConnect.AdUser usr_frm_ad: attr_frm_ad)
			{
				
				User user = new User();
	         
	          user.AccountType = UserAccountType.LimitedBusiness;	     
	          user.Password = ConfigurationHelper.getSimplePassword();

	          user.FirstName = usr_frm_ad.getFirst().toString().trim();
	          user.LastName = usr_frm_ad.getLast().toString().trim();
	          user.EmailAddress = usr_frm_ad.getMail().toString().trim();
	          track_usr = user.EmailAddress;
	          System.out.println(String.format(usr_frm_ad.getFirst()));
	          System.out.println(String.format("Details retrieved from active directory for user [%s]",user.EmailAddress));
	          System.out.println(String.format("\tUserEmail:  [%s]", user.EmailAddress)); 
	          System.out.println(String.format("\tFirstName:  [%s]", user.FirstName)); 
	          System.out.println(String.format("\tLastName:  [%s]", user.FirstName)); 
	          System.out.println(String.format("\tGroups user belongs to: [%s]", usr_frm_ad.getGroup().toString()));
	          System.out.println("\t\t======================");

	        
	        	//create email_groupname combinations for all groups user belongs to in ad using the |separated field group list
	        	  String[] grp_list_for_user = usr_frm_ad.getGroup().toString().split("\\|");
	        	  for (String group_name: grp_list_for_user)
	        	  {
	        		  if (!group_name.trim().equals(null) || !group_name.isEmpty())
	        		  //if (!group_name.isEmpty() || !group_name.)
	  				{
	  				
	  					//multiEmail_GName.put(user.EmailAddress,group_name);

	  					multiEmail_GName.put(user.EmailAddress,group_name);
	  				} 
	        	  
	        	  }

	          usersToAdd.add(user);
	        
				
				System.out.println("");

				System.out.println(String.format("\tChecking if user [%s] already exists.", new Object[] {
					user.EmailAddress
				}));

				User checkUser = UsersService.getUser(user.EmailAddress, true);

	        
				System.out.println("");
				
				if (checkUser != null) {
					System.out.println(String.format("\tUser found i.e already exists in company. Cleanup of User  [%s].  Deleting user that may have been created from previous Sample App run.", new Object[] {
					
						user.EmailAddress
					}));
					try {
						UsersService.deleteUser(user.EmailAddress);
					} catch (Exception localException1) {}
				} else {
					System.out.println(String.format("\tUser [%s] does not already exist in company, continuing successfully.",user.EmailAddress));
					
	      }
			}


	    } catch (NamingException e) {
		      e.printStackTrace();
		    } catch (Exception e) {
		      System.out.println("Errors Listing Attributes:" + e);
		    }
	          
	          System.out.println(String.format("Calling UsersService to add ***==== %d ====***users", new Object[] { Integer.valueOf(usersToAdd.size()) }));


	      System.out.println("");

	      System.out.println(String.format("Calling UsersService to add ==== %d ====users", new Object[] { Integer.valueOf(usersToAdd.size()) }));
	      
	      createdUsers = UsersService.createUsers((User[])usersToAdd.toArray(new User[usersToAdd.size()]));

	      System.err.println("");
	      if ((createdUsers == null) || (createdUsers.length == 0))
	      {
	        int addedCount = createdUsers == null ? 0 : createdUsers.length;

	        System.err.println(String.format("\tError Occured During Creating Some Of Users. Number of Created Users:%d", new Object[] { Integer.valueOf(addedCount) }));
	      }
	      else {
	        System.out.println(String.format("\tFinished creation of user [%s] .", track_usr ));

	      }
	    

	System.out.println(String.format("Calling UsersService to add ==== %d ====users", new Object[] {
		Integer.valueOf(usersToAdd.size())
	}));

	System.out.println(String.format("CreatedUsers now has ****==== %d ====**** users", new Object[] {Integer.valueOf(createdUsers.length)}));
	  }


	private static void deleteUsers() {
		if ((createdUsers == null) || (createdUsers.length == 0)) {
			return;
		}

		System.out.println("");
		System.out.println("Press enter before Users Deletion...");

		System.out.println("Start Users Deletion...");

		for (User user: createdUsers) {
			System.out.println("");
			System.out.println(String.format("\tDelete User [%s].", new Object[] {
				user.EmailAddress
			}));

			UsersService.deleteUser(user.EmailAddress);
		}

		System.out.println("");
		System.out.println("Finished Users Deletion.");
	}
}