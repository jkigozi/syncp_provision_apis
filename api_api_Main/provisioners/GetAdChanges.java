package provisioners;

//Retrieve any newly modified object and display the changed attributes


import java.io.*;
import java.util.Hashtable;

import javax.naming.*;
import javax.naming.ldap.*;
import javax.naming.directory.*;
import javax.naming.ldap.PagedResultsResponseControl;

import com.sun.jndi.ldap.BasicControl.*;
import com.sun.jndi.ldap.ctl.DirSyncControl;
import com.sun.jndi.ldap.ctl.DirSyncControl.*;
import com.sun.jndi.ldap.ctl.DirSyncResponseControl.*;
import com.sun.jndi.ldap.ctl.DirSyncResponseControl;

// HehHHHHHAttribute: isDeleted			TRUE
// HehHHHHHAttribute: accountExpires	9223372036854775807
// HehHHHHHAttribute: whenCreated		20150707230528.0Z
// givenName, sn, mail,
//Attribute for enable/disable user is useraccountcontrol 512=Enabled
//514= Disabled
//66048 = Enabled, password never expires
//66050 = Disabled, password never expires


public class GetAdChanges     {
     public static void main (String[] args)     {
          Hashtable env = new Hashtable();
          String adminName = "CN=Administrator,CN=Users,DC=johnk,DC=local";
          String adminPassword = "Francisco!2";
          String ldapURL = "LDAP://win2k8-R2.johnk.local:389";
          env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
          //set security credentials, note using simple cleartext authentication
          env.put(Context.SECURITY_AUTHENTICATION,"simple");
          env.put(Context.SECURITY_PRINCIPAL,adminName);
          env.put(Context.SECURITY_CREDENTIALS,adminPassword);
                    
          //connect to my domain controller
          env.put(Context.PROVIDER_URL,ldapURL);
          
          try {

               // Create the initial directory context
               LdapContext ctx = new InitialLdapContext(env,null);
          
               //Create the search controls           
               SearchControls searchCtls = new SearchControls();
          
               //Specify the attributes to return, null initially return all values     
               //and in subsequent calls, null returns all modified attributes
               //String returnedAtts[]={"givenName","sn","mail","userAccountControl"};
               String returnedAtts[]={};
               searchCtls.setReturningAttributes(returnedAtts);
          
               //Specify the search scope
               searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

               //specify the LDAP search filter
               String searchFilter = "(objectClass=user)";

               //Specify the Base for the search
               String searchBase = "DC=johnk,DC=local";

               //initialize counter to total the results
               int totalResults = 0;

               //Specify the DirSync and DirSyncResponse controls
               byte[] cookie = null;
               Control[] rspCtls;
               Control[] ctls = new Control[] {new DirSyncControl()};
               ctx.setRequestControls(ctls);

               //Search for objects using the filter
               NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter, searchCtls);

               //Loop through the search results
               while (answer.hasMoreElements()) {
                        SearchResult sr = (SearchResult)answer.next();

                    totalResults++;

                    //System.out.println(">>>" + sr.getName());

                    //First time around, no need to print any attributes 
          
               }

                System.out.println("Current results: " + totalResults);

               //save the response controls
               if ((rspCtls = ctx.getResponseControls()) != null) {
                    System.out.println("Response Controls: " + rspCtls.length);
                    for (int i=0; i < rspCtls.length;i++) {
                         if (rspCtls[i] instanceof DirSyncResponseControl) {
                              DirSyncResponseControl rspCtl = (DirSyncResponseControl)rspCtls[i];
                              

                              cookie = rspCtl.getCookie();

                              System.out.println("Flag: " + rspCtl.getFlag());

                              System.out.println("Length: " + rspCtl.getMaxReturnLength());

                              System.out.println("Cookie: " + cookie.toString());

                         }

                    }

               }



               //Now start a loop, prompt the user to continue/quit and continue searching for modified objects

               BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

               while (true) {

                    try {

                         String input;

                         while (true) {

                              System.out.println("Press x to exit, return to continue");

                              input = in.readLine();

                              if (input.startsWith("x") || input.startsWith("X")) {

                                   ctx.close();

                                   System.exit(0);

                              }

                              else {

                                   break;

                              }

                         }

                    }     

                    catch (IOException e)     {

                         System.err.println();

                    }



                    //now lets do the search again using the cookie

                    totalResults = 0;



                    //use the saved cookie in the DirSync control

                    ctx.setRequestControls(new Control[]{new DirSyncControl(1,Integer.MAX_VALUE,cookie,true)});



                    //execute the search again

                    answer = ctx.search(searchBase, searchFilter, searchCtls);



                    //Loop through the search results

                    while (answer.hasMoreElements()) {

                         SearchResult sr = (SearchResult)answer.next();



                         totalResults++;



                         System.out.println(">>>" + sr.getName());
                         //System.out.println(">>>" + sr.getAttributes());



                         //Print out the modified attributes

                         //instanceType and objectGUID are always returned

                         Attributes attrs = sr.getAttributes();

                         if (attrs != null) {

                              try {
                            	  	 


                                   for (NamingEnumeration ae = attrs.getAll();ae.hasMore();) {

                                        Attribute attr = (Attribute)ae.next();
                                        try
                                        {
                                        	if (attr.getID().equals("TRUE"))
                                        	
                                        if (attr != null && attr.get().toString().trim() != null && !attr.get().toString().trim().isEmpty() && !attr.get().toString().trim().equals("") )
                                        {
                                        	System.out.println("HehHHHHHAttribute: " + attr.getID());
                                        	System.out.println("HehHHHHHAttribute: " + attr.get().toString().trim());

                                        for (NamingEnumeration e = attr.getAll();e.hasMore();System.out.println(" " + e.next().toString()));
                                        }
                                        } catch (NullPointerException ne) {
                                        	System.err.println();
                                        }

                                   }

                              } 

                              catch (NullPointerException e)     {

                                   System.err.println();

                              }                  
                         }
                    }



                    System.out.println("Recently Modified: " + totalResults);



                    //Save the response control again

                    if ((rspCtls = ctx.getResponseControls()) != null) {

                         System.out.println("Response Controls: " + rspCtls.length);

                         for (int i=0; i < rspCtls.length;i++) {

                              if (rspCtls[i] instanceof DirSyncResponseControl) {

                                   DirSyncResponseControl rspCtl = (DirSyncResponseControl)rspCtls[i];

                                   cookie = rspCtl.getCookie();

                                   System.out.println("Flag: " + rspCtl.getFlag());

                                   System.out.println("Length: " + rspCtl.getMaxReturnLength());

                                   System.out.println("Cookie: " + cookie.toString());

                              }
                         }
                    }
               }

          } 

          catch (NamingException e) {

               System.err.println("Problem searching directory: " + e);

          } 

          catch (java.io.IOException e) {

               System.err.println("Problem searching directory: " + e);

          }

     }

}

