package mains;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import oauth.OAuth;
import util.APIContext;
import examples.APIHomeWork_AD;
import examples.APIHomeWork_F;

public class SampleApp
{
  static
  {
    disableSslVerification();
  }

  private static void disableSslVerification()
  {
    try
    {
      TrustManager[] trustAllCerts = { new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
        public X509Certificate[] getAcceptedIssuers() { return null;
        }
      }
       };
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      HostnameVerifier allHostsValid = new HostnameVerifier()
      {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      };
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    System.out.println("Java Sample App starting...");
    System.out.println("");

    OAuth.authenticate();

    System.out.println("");

    if (!APIContext.isAuthenticated()) {
      System.err.println("The OAuth authentication has failed, the app cannot continue.");
      System.exit(1);
    }
    else {
      System.out.println("Authentication was successful.");
    }

    //APIHomeWork_F.execute();
    APIHomeWork_AD.execute();
    //ProvisioningExampleAdMain.execute();
    


    System.out.println("");
    System.out.println("Provisioning part is completed.");
  }
}