package provisioners;

/**
 * delchanges.java
 * Sample JNDI application to perform a DirSync search against AD
 * Use both DirSync & Deletion controls to detect deleted objects
 * https://community.oracle.com/thread/1157474?tstart=0
  */

import java.io.*;
import java.util.Hashtable;
import javax.naming.*;
import javax.naming.ldap.*;
import javax.naming.directory.*;
import com.sun.jndi.ldap.ctl.*;
import com.sun.jndi.ldap.ctl.DirSyncControl.*;
import com.sun.jndi.ldap.ctl.DirSyncResponseControl.*;


class delchanges  {
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

public class deletedControl    {

     public static void main (String[] args)     {
          Hashtable<String,String> env = new Hashtable<String,String>();
          String adminName = "CN=Administrator,CN=Users,DC=johnk,DC=local";
          String adminPassword = "XXXXXXX";
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
          
               // Create the search controls           
               SearchControls searchCtls = new SearchControls();
          
               //Specify the attributes to return
               //For dirsync use null to return all changes
               String returnedAtts[]={};
               searchCtls.setReturningAttributes(returnedAtts);
          
               //Specify the search scope
               searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

               //specify the LDAP search filter
               String searchFilter = "(&(objectClass=user)(isDeleted=TRUE))";

               //Specify the Base for the search
               String searchBase = "DC=johnk,DC=local";

               //initialize counter to total the results
               int totalResults = 0;

               //Specifiy the Deleted Control
               Control[] delCtl = new Control[]{new DeletedControl()};
               ctx.setRequestControls(delCtl);
                
               //Specify the DirSync and DirSyncResponse controls
               byte[] cookie = null;
               Control[] rspCtls;
               Control[] ctls = new Control[] {new DirSyncControl()};
               ctx.setRequestControls(ctls);

               // Search for objects using the filter
               NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter, searchCtls);

               //Loop through the search results
               while (answer.hasMoreElements()) {
                        SearchResult sr = (SearchResult)answer.next();

                    totalResults++;

                    System.out.println(">>>" + sr.getName());

               }

                System.out.println("Current deleted users: " + totalResults);

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



     //Now start a loop, prompt the user to continue/quit and continue searching for newly deleted objects

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



                    //use the saved cookie

                    ctx.setRequestControls(new Control[]{new DirSyncControl(1,Integer.MAX_VALUE,cookie,true)});



                    //execute the search again

                    answer = ctx.search(searchBase, searchFilter, searchCtls);



                    //Loop through the search results

                    while (answer.hasMoreElements()) {

                         SearchResult sr = (SearchResult)answer.next();



                         totalResults++;



                         System.out.println(">>>" + sr.getName());



                    }



                    System.out.println("Recently deleted users: " + totalResults);



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
