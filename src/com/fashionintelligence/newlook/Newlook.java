package com.fashionintelligence.newlook;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Newlook {

	public static void main(String[] args) {
		ArrayList<String> siteList = new ArrayList<String>();
		System.setProperty("http.agent", "");
		try {
			URL url = new URL("http://www.newlook.com/sitemap.xml");
			HttpURLConnection httpConnection = (HttpURLConnection) url
					.openConnection();
			httpConnection
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					httpConnection.getInputStream()));
			String pageLine;
			while ((pageLine = rd.readLine()) != null) {
				if (pageLine.contains("<loc>")
						&& pageLine.contains("newlook.com/shop/")
						&& pageLine.length() > 18) {
					siteList.add(pageLine.substring(11, pageLine.length() - 6));
				}
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			if (siteList.size() > 0) {
				for (int i = 0; i < siteList.size(); i++) {
					URL productUrl;
					BufferedReader in;
					try {
						productUrl = new URL(siteList.get(i).toString());
						in = new BufferedReader(new InputStreamReader(
								productUrl.openStream()));
						String productLine = in.readLine();
					} catch (Exception e) {

					} finally {

					}
				}
			} else {
				System.out.println("Array empty");
			}
		}
	}

	public static String readString(InputStream inputStream) throws IOException {

		ByteArrayOutputStream into = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		for (int n; 0 < (n = inputStream.read(buf));) {
			into.write(buf, 0, n);
		}
		into.close();
		return new String(into.toByteArray(), "UTF-8"); // Or whatever encoding
	}
}
