package provisioners;

import entities.Group;
import entities.User;
import entities.UserAccountType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import com.google.common.collect.*;

import services.GroupMembersService;
import services.GroupsService;
import services.UsersService;
import util.APIContext;
import util.ConfigurationHelper;



public class APIHomeWork_F {
		//Array to track all users as they are created
	private static User[] createdUsers;
	
		//List to track all users as they are added to their respective groups
	private static List<User> groupMemberUsers = new ArrayList<User>();
	
		//Array to track all groups as they are created
	private static Group[] createdGroups;
	
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
		
		if (createdGroups == null || createdGroups.length==0) {
			
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
		System.out.println("");

		String companyGuid = APIContext.getCompanyGuid();

		if ((companyGuid == null) || (companyGuid.isEmpty())) {
			System.err.println("Group Can't Be Created Because Company Guid Wasn't Received During Authorization.");
			return;
		}

		System.out.println("Starting Group Creation...");
		
		System.out.println("\tRetrieve Existing Groups in Cloud.");
			 
			cloudGroups = GroupsService.getGroups(companyGuid);
			//Group[] all_groups = GroupsService.getGroups(companyGuid);

		//Load Group Input File as specified in Config File
		File fil = new File(ConfigurationHelper.getGrp_infil());

		BufferedReader brg = null;
		
			//Initialize temp List Where Groups Will be Tracked as They are Created
		ArrayList<Group> grpsToAdd = new ArrayList<Group>();
		
		try {
			FileReader frg = new FileReader(fil);

			brg = new BufferedReader(frg);
			String line;
			
			while ((line = brg.readLine()) != null) {
				
				Group group = new Group();

				group.Name = line.split(":")[0].trim();
				group.Owner = new User();

				group.Owner.EmailAddress = line.split(":")[1].trim();			
			
				
					for (Group grp: cloudGroups)
					{
						if (grp.Name.equals(group.Name)){
							System.out.println(String.format("\tGroup [%s] with ID %s Already Exists in Cloud", group.Name, group.Id));
						}
								//Update GroupName-GroupID Tracking Array
							gid_gname.put(group.Name, group.Id);
							//return;
						}
					
					//Add Group in this Iteration to temp Tracking List
					
					grpsToAdd.add(group);	
				}

			System.out.println("Creating Groups..........");
			
			//Convert Temp List to Array of Group and Call API to Create Groups
			createdGroups = GroupsService.createGroups(companyGuid, grpsToAdd.toArray(new Group[grpsToAdd.size()]));
			
				//Make Another API Call to retrieve Cloud Groups now updated
			all_groups = GroupsService.getGroups(companyGuid);
			
				//Rebuild Array to track all GroupName-GroupID combinations
			for (Group gp: all_groups){
			
				gid_gname.put(gp.Name, gp.Id);
				
			}
			
			brg.close();			

		} catch (FileNotFoundException fnfe) {
			System.out.println("");
			fnfe.printStackTrace();
			
		} catch (IOException e) {
			System.out.println("");
			e.printStackTrace();
			
		} 
	}

	private static void createUsers() {
		System.out.println("");
		System.out.println("Start Users Creation...");

		File fil = new File(ConfigurationHelper.getUsr_infil());

		BufferedReader br = null;
		
		ArrayList<User> usersToAdd = new ArrayList<User>();
		try {
			FileReader fr = new FileReader(fil);

			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
			
				User user = new User();

				user.EmailAddress = line.split(":")[0].trim();

				user.FirstName = line.split(":")[1].trim();
				user.LastName = line.split(":")[2].trim();
				user.Password = line.split(":")[3].trim();

				user.AccountType = UserAccountType.valueOf(line.split(":")[4].trim());

				String grp = line.toString().split(":")[5].trim();
				email_gname.put(user.EmailAddress, grp);
				
					//create a list of groups from the |separated field list				
				String[] tug_list = grp.split("\\|");
				
					//Loop over list to create email-groupname combinations for each email added to group
				for (String group_name: tug_list)
				{				
					multiEmail_GName.put(user.EmailAddress,group_name);
				}
			

				usersToAdd.add(user);
				System.out.println("");

				System.out.println(String.format("\tChecking if user [%s] already exists.", new Object[] {
					user.EmailAddress
				}));

				User checkUser = UsersService.getUser(user.EmailAddress, true);

				System.out.println("");

				if (checkUser != null) {
					System.out.println(String.format("\tUser found. Cleanup of User  [%s].  Deleting user that may have been created from previous Sample App run.", new Object[] {
						user.EmailAddress
					}));
					try {
						UsersService.deleteUser(user.EmailAddress);
					} catch (Exception localException1) {}
				} else {
					System.out.println("\tNo user found in company, continuing successfully.");


					String companyGuid = APIContext.getCompanyGuid();

					if ((companyGuid == null) || (companyGuid.isEmpty())) {
						System.err.println("Group Can't Be Created Because Company Guid Wasn't Received During Authorization.");
						return;
					}

					System.out.println("Start Group Creation...");
													
				}
			}
			br.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} 

		System.out.println("Calling UsersService to add users");
 
			for (Map.Entry<String, String> mm: multiEmail_GName.entries())
			{
				System.out.println(String.format("\tGroup: %s Email: %s",mm.getKey(),mm.getValue()));
				
			}

		createdUsers = UsersService.createUsers((User[]) usersToAdd.toArray(new User[usersToAdd.size()]));


		
		System.err.println("");
		if ((createdUsers == null) || (createdUsers.length == 0)) {
			int addedCount = createdUsers == null ? 0 : createdUsers.length;

			System.err.println(String.format("\tError Occured During Creating Some Of Users. Number of Created Users:%d", new Object[] {
				Integer.valueOf(addedCount)
			}));
		} else {
			System.out.println("\tFinished Users Creation.");
		}
		System.out.println(String.format("Calling UsersService to add ==== %d ====users", new Object[] {
			Integer.valueOf(usersToAdd.size())
		}));

		System.out.println(String.format("CreatedUsers now has ****==== %d ====**** users", new Object[] {
			Integer.valueOf(createdUsers.length)
		}));
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