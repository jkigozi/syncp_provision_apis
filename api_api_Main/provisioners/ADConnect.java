package provisioners;

//import java.util.HashMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.CompoundName;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.NameParser;
import javax.naming.Name;

import util.ConfigurationHelper;



public class ADConnect {

	public static void main(String[] args) {
		
		try
		{
		String url = ConfigurationHelper.getLdapUrl();

		String usr = ConfigurationHelper.getAdminCn();

		String pas = ConfigurationHelper.getAdminPass();

		String search_fltr = "(objectClass=user)";
		
		//String search_base = ConfigurationHelper.all_search_bases();
		String search_base = "OU=SyncplicityUsers,DC=support12r2,DC=com";
		
		List<AdUser> attr_frm_ad = new ArrayList<AdUser>();		
		
		ADConnect adc = new ADConnect();

		attr_frm_ad = adc.getUserData(adc.createContext(url, usr, pas), search_base, search_fltr);

		
		System.out.println("");

		for(AdUser ad_group: attr_frm_ad)
		{
			
			
			System.out.println(String.format(ad_group.getFirst()));
			System.out.println(String.format(ad_group.getLast()));
			System.out.println(String.format(ad_group.getMail()));
			System.out.println(String.format(ad_group.getGroup()));
		}
		System.out.println("");
		
		} catch (NamingException nee) {
			nee.printStackTrace();
		}

	}
	
	

	public LdapContext createContext(String server_url, String user, String passwd)
	{
		Hashtable<String,String> env = getProperties(server_url,user,passwd);
		LdapContext ctx;
		
		try
		{
			ctx = new InitialLdapContext(env,null);
			
			System.out.println("Successfully Connected to active directory @" + server_url);
			
		} 
		catch (NamingException ne)
		{
			System.err.println("Problems encountered when connecting to directory @" + server_url);
			//ne.printStackTrace();
			
			throw new RuntimeException(ne);
		}
		
		return ctx;
	}
	
	private Hashtable<String,String> getProperties(String serverurl,String user,String passwd)
	{
		//create initial ldap context
		Hashtable<String,String> env = new Hashtable<String,String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.REFERRAL, "ignore");
		env.put("com.sun.jndi.ldap.connect.pool", "true");
		
		//specify how long to wait for pooled connection
		//if omitted, application will wait indefinitely
		env.put("com.sun.jndi.ldap.connect.timeout", "300000");		
		env.put(Context.PROVIDER_URL, serverurl);
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, passwd);
		//env.put("java.naming.ldap.attributes.binary", "tokenGroups objectSid objectGUID");
		//specify any attributes to be returned in binary format
		//env.put("java.naming.ldap.attributes.binary","tokenGroups");
		
		 // the following is helpful in debugging errors
        //env.put("com.sun.jndi.ldap.trace.ber", System.err);
		
		return env;
	}
	
	public List<String> getGroups(LdapContext ctx, String sBase, String sFltr) throws NamingException
	{
		LdapContext ctx_sub = ctx;

		List<String> sb = new ArrayList<String>();		
		
	try
	{
			//Create the search controls
			SearchControls searchCtrls = new SearchControls();
			
			 //Specify the search scope
			//searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			//specify the LDAP search filter to find 
			String searchFltr = sFltr;
			
			//specify the LDAP Base for the search 
			String searchBase = sBase;
			
			//placeholder for an LDAP filter that will store groups the user belongs to
			StringBuffer groupsSearchFilter = new StringBuffer();
			groupsSearchFilter.append("(|");
			

			String[] returnedAtts={"cn","mail","memberOf","sAMAccountName"};
			
			searchCtrls.setReturningAttributes(returnedAtts);
			
			//Search for objects using the filter
			NamingEnumeration<SearchResult> answer = ctx_sub.search(searchBase, searchFltr, searchCtrls);
			
			String str_grp = "";
			String str_mail = "";

			//Loop through the search results
			while (answer.hasMoreElements())
			{
				SearchResult s_rslts = (SearchResult)answer.next();
				Attributes attrs = s_rslts.getAttributes();
				
	
				
	
					if (attrs != null)
					{
						str_grp=attrs.get("sAMAccountName").get().toString();
					sb.add(str_grp);
			
					}
			
			}	

			ctx_sub.close();
		} catch(NamingException ne) {
			ne.printStackTrace();
		}
		return sb;	
		
	}
	
	
	public List<AdUser> getUserData(LdapContext ctx, String sBase, String sFltr) throws NamingException
	{
		
		
		List<AdUser> sb = new ArrayList<AdUser>();
		
				
	try
	{
			LdapContext ctx_usr = ctx;
			
			//Create the search controls
			SearchControls searchCtrls = new SearchControls();
			
			 //Specify the search scope
			//searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			//specify the LDAP search filter to find 
			String searchFltr = sFltr;
			
			//specify the LDAP Base for the search 
			String searchBase = sBase;
			
			//placeholder for an LDAP filter that will store groups the user belongs to
			StringBuffer groupsSearchFilter = new StringBuffer();
			groupsSearchFilter.append("(|");
			
			String returnedAtts[]={"sn", "mail", "givenName", "memberOf" };
			searchCtrls.setReturningAttributes(returnedAtts);
			
			//Search for objects using the filter
			NamingEnumeration<SearchResult> answer = ctx_usr.search(searchBase, searchFltr, searchCtrls);
				
			int results_tot = 0;

			//Loop through the search results
			while (answer.hasMoreElements())
			{
				SearchResult s_rslts = (SearchResult)answer.next();
				Attributes attrs = s_rslts.getAttributes();
				
				//System.out.println(results_tot+1 + " ->>>" + s_rslts.getName());
						
					if (attrs != null)
					{
						AdUser adusr = new AdUser();
						
						adusr.first = attrs.get("givenName").toString().split(":")[1];
						adusr.last = attrs.get("sn").toString().split(":")[1];
						adusr.email = attrs.get("mail").toString().split(":")[1];
						adusr.groups = ExtractGrp(attrs.get("memberOf").toString().split(":")[1]);
									
								
					sb.add(adusr);
			}
			}
			results_tot+=1;
							
		
					
			ctx_usr.close();
		} catch(NamingException ne) {
			ne.printStackTrace();
		}
		//System.out.println(sb);
		return sb;	
		
	}

	public List<AdUser> getUserData_1(LdapContext ctx, String sBase, String sFltr) throws NamingException
	{
		
		
		List<AdUser> sb = new ArrayList<AdUser>();
		NameParser np = ctx.getNameParser("");
				
	try
	{
			LdapContext ctx_usr = ctx;
			
			//Create the search controls
			SearchControls searchCtrls = new SearchControls();
			
			 //Specify the search scope
			//searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			//specify the LDAP search filter to find 
			String searchFltr = sFltr;
			
			//specify the LDAP Base for the search 
			String searchBase = sBase;
			
			//placeholder for an LDAP filter that will store groups the user belongs to
			StringBuffer groupsSearchFilter = new StringBuffer();
			groupsSearchFilter.append("(|");
			
			String returnedAtts[]={"sn", "mail", "givenName", "memberOf" };
			searchCtrls.setReturningAttributes(returnedAtts);
			
			//Search for objects using the filter
			NamingEnumeration<SearchResult> answer = ctx_usr.search(searchBase, searchFltr, searchCtrls);
				
			int results_tot = 0;

			//Loop through the search results
			while (answer.hasMoreElements())
			{
				SearchResult s_rslts = (SearchResult)answer.next();
				Attributes attrs = s_rslts.getAttributes();
				
				//System.out.println(results_tot+1 + " ->>>" + s_rslts.getName());
						
					if (attrs != null)
					{
						AdUser adusr = new AdUser();
						
						adusr.first = attrs.get("givenName").toString().split(":")[1];
						adusr.last = attrs.get("sn").toString().split(":")[1];
						adusr.email = attrs.get("mail").toString().split(":")[1];
						adusr.groups = attrs.get("memberOf").toString();
						Name dn_to_parse = np.parse(adusr.groups);	
						
						String xx = (String)dn_to_parse.remove(1);
						System.out.println(String.format("Extracted is ", xx));
					sb.add(adusr);
			}
			}
			results_tot+=1;
							
		
					
			ctx_usr.close();
		} catch(NamingException ne) {
			ne.printStackTrace();
		}
		//System.out.println(sb);
		return sb;	
		
	}
	
	public String ExtractGrp (String dn_string)
	//public String[] ExtractGrp (String dn_string)
	{
		//All groups user belongs to will be delimited by "|" and appended one-by-one to this string
		String dn_out_string = "";
		
		try
		{
	        Properties syntax = new Properties();
	        syntax.setProperty("jndi.syntax.direction", "left_to_right");
	        syntax.setProperty("jndi.syntax.separator", ",");
	        syntax.setProperty("jndi.syntax.ignorecase", "true");
	        syntax.setProperty("jndi.syntax.escape", "\\");
	        syntax.setProperty("jndi.syntax.trimblanks", "true");
	        // syntax.setProperty("jndi.syntax.separator.ava", ",");
	        syntax.setProperty("jndi.syntax.separator.typeval", "=");
	        syntax.setProperty("jndi.syntax.beginquote", "\"");

	        String name = dn_string;
	        
	        

	        CompoundName cn = new CompoundName(name, syntax);

	        for (int i = 0; i < cn.size(); i++) {

	        	if (cn.get(i).toString().trim().startsWith("CN=") )
	        	{
	        		//Fish out the group string from the full dn of group e.g Group_Main from 
	        		//"CN=Group_Main,OU=Izo,OU=Izogeek,DC=johnk,DC=local" and append "|" delimiter
	            dn_out_string = dn_out_string +  cn.remove(i).toString().split("\\=")[1]+ "|" ;
	            
	        	}	        	
	        }	        
	        
		} catch (NamingException nex)
		{
			nex.printStackTrace();
		}
			return dn_out_string;
	    }
	
	class AdUser {

	    private String email;
	    private String first;
	    private String groups;
	    private String last;

	    public String getMail() {
	        return email;
	    }
	    public String getFirst() {
		    return first;
	    }
		public String getGroup() {
			return groups;
		}
		public String getLast() {
			return last;
	    }

	    public String setMail(String email) {
	        this.email = email;
	        return this.email;
	    }
	    public String setFirst(String firstname) {
		    this.first = firstname;
		    return this.first;
	    }
		public String setGroup(String group) {
		    this.groups = group;
		    return this.groups;
		}
		public String setLast(String lastname) {
		    this.last = lastname;
		    return this.last;
	    }
	}
	

}
	