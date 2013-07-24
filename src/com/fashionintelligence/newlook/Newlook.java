package com.fashionintelligence.newlook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.soap.Node;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
				try {
					File file = new File("output.csv");
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("ID,Name,Size,Colour,Availability,Price");
					bw.newLine();
					for (int i = 1500; i < 1510; i++) {
						String[] propertyString = null;
						try {
							Document doc = Jsoup
									.connect(siteList.get(i))
									.userAgent(
											"Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.2 Safari/537.36")
									.get();
							propertyString = extractProperties(doc);
							if(propertyString!=null){
							for (int j = 0; j < propertyString.length; j++) {
								bw.write(propertyString[j] + ",");
							}
							bw.newLine();
							System.out.println(i);
							}else{
								System.out.println(siteList.get(i)+" not a product page");
							}
						} catch (Exception e) {
							e.printStackTrace();

						} finally {

						}
					}
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// System.out.println("Array empty");
			}
		}
	}

	public static String[] extractProperties(Document doc) {
		if (!productPageCheck(doc)) {
			return null;
		} else {
			String[] string = { extractId(doc), extractName(doc) , "Size",extractColour(doc),"Availability",extractPrice(doc)};
			return string;
		}
	}

	public static boolean productPageCheck(Document doc) {
		Elements productPage = doc.getElementsByClass("product_display");
		if (productPage.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String extractId(Document doc) {
		return doc.getElementsByAttributeValueMatching("itemprop", "productID")
				.first().text();
	}

	public static String extractName(Document doc) {
		return doc.getElementsByAttributeValueMatching("itemprop", "name")
				.first().text();
	}

	public static String extractSize(Document doc) {
		Elements sizeDropdown = doc.getElementsByClass("sizes");
		// Elements sizes = sizeDropdown.getElementsByAttribute("value");
		// String sizeValue =
		// sizes.get(0).getElementsByAttribute("value").toString();
		// System.out.println("size"+sizeDropdown);
		return null;
	}

	public static String extractColour(Document doc) {
		Elements colourOptions = doc.getElementsByClass("colour");
		if(colourOptions.size()!=0){
		return Integer.toString(colourOptions.first().getElementsByClass("colour-option").size());
		}else{
			return "No colour option";
		}
	}

	public static String extractAvailability(Document doc) {
		Element size = doc.getElementById("out_stock_message");
		// System.out.println(size);
		// if(size.getElementsByAttributeValue("style",
		// "display: table").size()>0){
		// System.out.println("in stock");
		// }else{
		// System.out.println("out of stock");
		// }
		return null;
	}

	public static String extractPrice(Document doc) {
		return doc.getElementsByAttributeValueMatching("itemprop", "price")
				.first().text();
	}
}
