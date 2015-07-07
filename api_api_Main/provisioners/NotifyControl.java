package provisioners;

/**
 * ldapnotify.java
 * December 2004
 * Sample JNDI application that uses AD LDAP Notification Control.
 * https://community.oracle.com/thread/1158217?tstart=0
 * http://jeftek.com/222/using-java-code-with-active-directory/
 * https://community.oracle.com/thread/1158217
 **/

import java.util.Hashtable;
import java.util.Enumeration;
import javax.naming.*;
import javax.naming.ldap.*;
//import com.sun.jndi.ldap.ctl.*;
import javax.naming.directory.*;

import util.ConfigurationHelper;

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

class ldapnotify {

     public static void main(String[] args) {
        
          try {
        	  String adminName = "CN=Administrator,CN=Users,DC=antipodes,DC=com";
              String adminPassword = ConfigurationHelper.getAdminPass();
              String ldapURL = ConfigurationHelper.getLdapUrl();
              String searchBase = "OU=SyncplicityUsers,DC=support12r2,DC=com";
                   
              //For persistent search can only use objectClass=*
              String searchFilter = "(objectClass=user)";
              


                     //Specifiy the search time limit, in this case unlimited
               searchCtls.setTimeLimit(0);

               //Request the LDAP Persistent Search control
                     Control[] rqstCtls = new Control[]{new NotifyControl()};
                     ctx.setRequestControls(rqstCtls);

               //Now perform the search
               NamingEnumeration answer = ctx.search(searchBase,searchFilter,searchCtls);
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

                              for (NamingEnumeration ae = attrs.getAll();ae.hasMore();) {
                                   Attribute attr = (Attribute)ae.next();
                                   System.out.println("Attribute: " + attr.getID());
                                   for (NamingEnumeration e = attr.getAll();e.hasMore();System.out.println("   " + e.next().toString()));
                              }
                         } 
                         catch (NullPointerException e)     {
                              System.err.println("Problem listing attributes: " + e);
                         }
                    }
               }
              } 

          catch (NamingException e) {
                      System.err.println("LDAP Notifications failure. " + e);
              } 
       } 
} 