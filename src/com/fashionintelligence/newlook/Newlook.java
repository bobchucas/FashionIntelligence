package com.fashionintelligence.newlook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Newlook {

	public static void main(String[] args) {
		ArrayList<String> siteList = new ArrayList<String>();
		try {
			// URL url = new URL("http://www.newlook.com/sitemap.xml");
			// ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			// FileOutputStream fos = new FileOutputStream("information.xml");
			// fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			// fos.close();
			BufferedReader rd = new BufferedReader(
					new FileReader("sitemap.xml"));
			String pageLine;
			while ((pageLine = rd.readLine()) != null) {
				if (pageLine.contains("<loc>")
						&& pageLine.contains("newlook.com/shop/")
						&& pageLine.length() > 18)
					siteList.add(pageLine.substring(11, pageLine.length() - 6));
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
						// System.out.println(siteList.get(i));
						productUrl = new URL(siteList.get(i).toString());
						in = new BufferedReader(new InputStreamReader(
								productUrl.openStream()));
						String productLine = in.readLine();
						// while ((productLine=in.readLine())!=null){
						System.out
								.println(siteList.get(i) + ", " + productLine);
						// }
					} catch (Exception e) {

					} finally {

					}
				}
			} else {
				System.out.println("Array empty");
			}
		}
	}
}
