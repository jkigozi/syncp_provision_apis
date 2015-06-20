package mains;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;



public class test_playssl_protocol {
	
	public static void main(String[] arstring) {
	try {
		
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		URL url = new URL("https://10.10.167.172/ping");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setSSLSocketFactory(sslsocketfactory);
		InputStream inputstream = conn.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		String string = null;
		
		while ((string = bufferedreader.readLine()) != null) {
			System.out.println("Received " + string);
		}
	} catch (Exception exception) {
		exception.printStackTrace();

	}
	}
	


}
