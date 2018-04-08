package com.thumbsup;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class YsHttpClients {

	public boolean checkUserIp(String userIp) {
		String url = "http://check.getipintel.net/check.php?ip=" + 
				userIp + "&contact=GeneKnight4@GMail.com&flags=m" ;
		try {
		    CloseableHttpClient client = HttpClients.createDefault();
		    HttpGet httpGet = new HttpGet(url);
		    CloseableHttpResponse response = client.execute(httpGet);
		    String resp = EntityUtils.toString(response.getEntity(), "UTF-8") ;
//		    System.out.println("response: " + resp);
		    return (Integer.parseInt(resp) == 1) ;
		} catch (ClientProtocolException ioe) {
			System.out.println("CP Exception");
			return false ;
		} catch (IOException ioe) {
			System.out.println("IO Exception");
			return false ;
		}

	}
	
}
