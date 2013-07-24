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
					bw.write("ID,Name,Size,Colour,Availability,Price,Category1,Category2,Sale,URL");
					bw.newLine();
					for (int i = 1600; i < 1700; i++) {
						String[] propertyString = null;
						try {
							Document doc = Jsoup
									.connect(siteList.get(i))
									.userAgent(
											"Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.2 Safari/537.36")
									.get();
							propertyString = extractProperties(doc);
							if (propertyString != null) {
								if (Integer.parseInt(propertyString[2]) == 0) {
									propertyString[2] = "1";
								}
								int productCount = 1;
								int colours = Integer
										.parseInt(propertyString[3]);
								int sizes = Integer.parseInt(propertyString[2]);
								if (colours == 0) {
									propertyString[3] = "1";
									propertyString[4] = "Out of stock";
								}
								String[] colourList = null;
								if (colours > 1) {
									// TODO sizes
									productCount = colours;
									colourList = getColours(doc);
								}
								for (int k = 0; k < productCount; k++) {
									if (colourList != null) {
										propertyString[3] = colourList[k];
									} else {
										propertyString[3]="No colour option";
									}
									for (int j = 0; j < propertyString.length; j++) {
										bw.write(propertyString[j] + ",");
									}
									bw.write(siteList.get(i));
									bw.newLine();
								}
							} else {
								System.out.println(siteList.get(i)
										+ " not a product page");
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
			String colour = extractColour(doc);
			String[] breadcrumb = extractBreadcrumb(doc);
			String[] string = { extractId(doc), extractName(doc),
					extractSize(doc), colour, "Available", extractPrice(doc),
					breadcrumb[0], breadcrumb[1], sale(doc) };
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
		Element sizeDropdown = doc.getElementById("size_standard");
		if (sizeDropdown != null) {
			Elements sizes = sizeDropdown.getElementsByAttribute("value");
			return Integer.toString(sizes.first()
					.getElementsByAttribute("value").size());
		} else {
			return "0";
		}
	}

	public static String extractColour(Document doc) {
		Elements colourOptions = doc.getElementsByClass("colour");
		if (colourOptions.size() > 0) {
			return Integer.toString(colourOptions.first()
					.getElementsByClass("colour-option").size());
		} else {
			return "0";
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

	public static String sale(Document doc) {
		Elements colourOptions = doc.getElementsByClass("was");
		if (colourOptions.size() > 0) {
			return colourOptions.first().getElementsByClass("promovalue")
					.first().text();
		} else {
			return "No sale";
		}
	}

	public static String[] extractBreadcrumb(Document doc) {
		Element breadcrumb = doc.getElementsByClass("breadcrumb").first();
		Elements crumbs = breadcrumb.getElementsByTag("li");
		String mainCat = crumbs.get(1).text().replace(",", "");
		String subCat = crumbs.get(2).text().replace(",", "");
		String[] crumb = { mainCat, subCat };
		return crumb;
	}

	public static String[] getColours(Document doc) {
		Elements colourOptions = doc.getElementsByClass("colour-option");
		String[] colourList = new String[colourOptions.size()];
		for (int i = 0; i < colourOptions.size(); i++) {
			colourList[i] = colourOptions.get(i).getElementsByTag("a").first()
					.attr("title");
		}
		return colourList;

	}
}
