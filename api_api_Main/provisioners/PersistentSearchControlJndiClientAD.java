package provisioners;
	 
	//LDAP Persistent Search Control -- JNDI Client for AD

	//The smart guys in Microsoft support Persistent Search Control in a clever way using OID "1.2.840.113556.1.4.528" instead of the standard OID "2.16.840.1.113730.3.4.3". Definitely, they have a name for it: NotifyControl.


	import javax.naming.*;
	import javax.naming.directory.*;
	import javax.naming.event.*;
	import javax.naming.ldap.*;

	import java.util.Hashtable;
	import java.io.*;
	
	import provisioners.ADConnect;

	public class PersistentSearchControlJndiClientAD 
	{
	     
	  // no longer "2.16.840.1.113730.3.4.3"
	  static final String PERSISTENT_SEARCH_OID = "1.2.840.113556.1.4.528";
	  
	  public static void main(String[] args)
	  {
	    LdapContext   rootContext;

	    //create initial context
	    Hashtable env = new Hashtable();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    env.put(Context.PROVIDER_URL, "ldap://myserevr.mycompany.com:389");
	    env.put(Context.SECURITY_AUTHENTICATION, "simple");
	    env.put(Context.SECURITY_PRINCIPAL, "mytest@mydomain.com");
	    env.put(Context.SECURITY_CREDENTIALS, "mypassword");
	    env.put(Context.BATCHSIZE, "1");  //return each change as it occurs
	    env.put("java.naming.ldap.derefAliases", "never");
	    
	    try {
	      // Create the initial directory context
	      rootContext = new InitialLdapContext(env, null);
	    
	      // verify that persistent search is supported, exit if not supported
	      if (!isPersistentSearchSupported(rootContext)){
	        System.out.println(
	            "The LDAP Server does not support persistent search");
	        System.exit(1);
	      }

	      // Set up the search constraints
	      SearchControls constraints = new SearchControls();
	      constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
	      constraints.setTimeLimit(0);
	  
	      //Request the LDAP Persistent Search control
	      Control[] rqstCtls = new Control[]{new NotifyControl()};
	      rootContext.setRequestControls(rqstCtls);

	      //Specify the base for the search
	      String searchBase = "dc=mydomain,dc=com";
	      
	      //For persistent search can only use objectClass=*
	      String searchFilter = "(objectClass=*)";
	 
	      //Now perform the search
	      NamingEnumeration answer = 
	            rootContext.search(searchBase,searchFilter,constraints);
	      SearchResult sr;
	      Attributes attrs;
	      
	      //Continue waiting for changes....forever
	      while(true) {
	        System.out.println("Waiting for changes..., press Ctrl C to exit");
	 
	         sr = (SearchResult)answer.next();
	            System.out.println(">>>" + sr.getName());
	 
	        //Print out the modified attributes
	        //instanceType and objectGUID are always returned
	        attrs = sr.getAttributes();
	        if (attrs != null) {
	          try {
	            for (NamingEnumeration ae = attrs.getAll();ae.hasMore();) 
	            {
	              Attribute attr = (Attribute)ae.next();
	              System.out.println("Attribute: " + attr.getID());
	              for (NamingEnumeration e = attr.getAll();e.hasMore();
	                System.out.println("   " + e.next().toString()));
	            }
	          } 
	          catch (NullPointerException e)  {
	            System.err.println("Problem listing attributes: " + e);
	          }
	        }
	      }
	    }
	    catch (NamingException e) 
	    {
	      System.err.println("Problem searching directory: " + e);
	    } 
	    catch (Exception e) 
	    {
	      System.err.println("Problem searching directory: " + e);
	    }
	  }

	  /**
	   * isPersistentSearchSupported
	   *
	   * Query the rootDSE to find out if the persistent search control
	   * is supported.
	   */
	  static boolean isPersistentSearchSupported(
	    LdapContext   rootContext) throws NamingException
	  {
	    SearchResult     rootDSE;
	    NamingEnumeration  searchResults;
	    Attributes       attrs;
	    NamingEnumeration  attrEnum;
	    Attribute      attr;
	    NamingEnumeration  values;
	    String         value;
	    String[]       attrNames = {"supportedControl"};
	    SearchControls     searchControls = new SearchControls();

	    searchControls.setCountLimit(0);  //0 means no limit
	    searchControls.setReturningAttributes(attrNames);
	    searchControls.setSearchScope(SearchControls.OBJECT_SCOPE);

	    // search for the rootDSE object
	    searchResults =
	      rootContext.search("", "(objectClass=*)", searchControls);

	    while (searchResults.hasMore())
	    {
	      rootDSE = (SearchResult)searchResults.next();

	      attrs = rootDSE.getAttributes();
	      attrEnum = attrs.getAll();
	      while (attrEnum.hasMore())
	      {
	        attr = (Attribute)attrEnum.next();
	        values = attr.getAll();
	        while (values.hasMore())
	        {
	          value = (String) values.next();
	          if (value.equals(PERSISTENT_SEARCH_OID))
	            return true;
	        }
	      }
	    }
	    return false;
	  }
	} 

	class DeletedControl implements Control {

	  public byte[] getEncodedValue() {
	    return new byte[] {};
	  }

	  public String getID() {
	    return "1.2.840.113556.1.4.417";
	  }

	  public boolean isCritical() {
	    return true;
	  }

	}

	class NotifyControl implements Control {

	  public byte[] getEncodedValue() {
	          return new byte[] {};
	  }

	  public String getID() {
	    return "1.2.840.113556.1.4.528";
	  }

	  public boolean isCritical() {
	    return true;
	  }
	}
