package com.fashionintelligence.newlook;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

public class Newlook {

	public static void main(String[] args) {
		ArrayList<String> siteList = new ArrayList<String>();
		try {
			URL url = new URL("http://www.newlook.com/sitemap.xml");
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream("information.xml");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			BufferedReader rd = new BufferedReader(new FileReader(
					"information.xml"));
			String pageLine;
			while ((pageLine = rd.readLine()) != null) {
				System.out.println(pageLine);
				if (pageLine.contains("<loc>")
						&& pageLine.contains("newlook.com/shop/")
						&& pageLine.length() > 18)
					siteList.add(pageLine.substring(11, pageLine.length() - 6));
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			if(siteList.size()>0){
			for (int i = 0; i < siteList.size(); i++) {
				System.out.println(siteList.get(i));
			}
			}else{
				System.out.println("Array empty");
			}
		}
	}
}
