package com.fashionintelligence.asos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Asos {

	public static void main(String[] args) {
		ArrayList<String> siteList = new ArrayList<String>();
		ArrayList<String> pageList = new ArrayList<String>();
		Date fileDate = new Date();
		DateFormat fileFormat = new SimpleDateFormat("yyyyMMdd");
		String retailer = "Asos";
		String availabilityString = "Not Available";
		try {
			URL url = new URL("http://www.asos.com/sitemap.ashx");
			siteList = extractBaseSitemap(url);
			pageList = extractSitemap(siteList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pageList.size() > 0) {
				try {
					File file = new File(retailer + "_"
							+ fileFormat.format(fileDate));
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("Retailer,ID,Name,Size Choice,Colour Choice,Availability,Price,Category1,Category2,Sale,Image,Date,URL");
					bw.newLine();
					WebDriver driver = new FirefoxDriver();
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd HH:mm:ss");
					for (int i = 0; i < pageList.size(); i++) {
						String address = pageList.get(i);
						if (address.contains("pgeproduct")) {
							String[] propertyString = null;
							try {
								driver.get(address);
								System.out.println("Accessing product " + i
										+ ", " + address);
								propertyString = extractProperties(driver);
								if (propertyString != null) {
									int colours = Integer
											.parseInt(propertyString[3]);
									int sizes = Integer
											.parseInt(propertyString[2]);
									if (!propertyString[5].contains("£")) {
										driver.quit();
										driver = new FirefoxDriver();
										// i--;
										System.out
												.println("Browser interpreted as from wrong locale");
									} else {
										String[] colourString = null;
										String[] sizeString = null;
										for (int l = 0; l < colours; l++) {
											colourString = getColours(driver);
											propertyString[3] = colourString[l];
											for (int k = 0; k < sizes; k++) {
												if (sizes > 1) {
													sizeString = getSizes(driver);
													if (sizeString[k]
															.contains(availabilityString)) {
														sizeString[k] = sizeString[k]
																.substring(
																		0,
																		sizeString[k]
																				.lastIndexOf("-"))
																.trim();
														propertyString[4] = availabilityString;
													} else {
														propertyString[4] = "In Stock";
													}
													propertyString[2] = sizeString[k];
												} else {
													propertyString[2] = "-";
												}
												bw.write("ASOS,");
												for (int j = 0; j < propertyString.length; j++) {
													bw.write(propertyString[j]
															+ ",");
												}
												Date date = new Date();
												bw.write(dateFormat
														.format(date) + ",");
												bw.write(pageList.get(i));
												bw.newLine();
											}
										}
									}
								} else {
									System.out.println(pageList.get(i)
											+ " not a product page");
								}
							} catch (Exception e) {
								e.printStackTrace();

							} finally {

							}
						} else {
							System.out.println("Non-product URL");
						}
					}
					System.out.println("Loop finished");
					bw.close();
					System.out.println("Writer closed");
					driver.quit();
					System.out.println("Driver quit");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String[] extractProperties(WebDriver driver) {
		String colour = extractColour(driver);
		String[] breadcrumb = extractBreadcrumb(driver);
		String sale = sale(driver);
		String[] string = { extractId(driver), extractName(driver),
				extractSize(driver), colour, extractAvailability(driver),
				extractPrice(driver), breadcrumb[0], breadcrumb[1], sale,
				extractImage(driver) };
		if (sale != "No sale") {
			string[8] = string[5];
			string[5] = sale;
		}
		return string;
	}

	public static String extractId(WebDriver driver) {
		return driver.findElement(By.cssSelector("span.productcode")).getText();
	}

	public static String extractName(WebDriver driver) {
		return driver.findElement(By.cssSelector("span.product_title"))
				.getText();
	}

	public static String extractSize(WebDriver driver) {
		try {
			WebElement sizeDropdown = driver.findElement(By.className("size"));
			if (sizeDropdown != null) {
				List<WebElement> sizes = sizeDropdown.findElements(By
						.cssSelector("option"));
				return Integer.toString(sizes.size() - 1);
			} else {
				return "1";
			}
		} catch (Exception e) {
			return "1";
		}
	}

	public static String extractColour(WebDriver driver) {
		List<WebElement> colourOptions = driver.findElements(By
				.className("colour"));
		if (colourOptions.size() > 0) {
			return Integer.toString(colourOptions.get(0)
					.findElements(By.cssSelector("option")).size() - 1);
		} else {
			return "1";
		}
	}

	public static String extractAvailability(WebDriver driver) {
		List<WebElement> stock = driver
				.findElements(By.className("outofstock"));
		if (stock.size() > 0 && stock.get(0).isDisplayed()) {
			return "Out of stock";
		} else {
			return "In stock";
		}
	}

	public static String extractPrice(WebDriver driver) {
		return driver.findElement(
				By.cssSelector("div.product_price span.product_price_details"))
				.getText();
	}

	public static String sale(WebDriver driver) {
		try {
			WebElement colourOptions = driver.findElement(By
					.cssSelector("div.product_price span.previousprice"));
			if (colourOptions != null) {
				return colourOptions.getText().split(" ")[1].trim();
			} else {
				return "No sale";
			}
		} catch (Exception e) {
			return "No sale";
		}
	}

	public static String[] extractBreadcrumb(WebDriver driver) {
		List<WebElement> breadcrumb = driver.findElements(By
				.cssSelector("div.other-categories ul li"));
		if (breadcrumb.size() > 0) {
			List<WebElement> crumbs = breadcrumb.get(0).findElements(
					By.cssSelector("a"));
			String mainCat = crumbs.get(0).getText().replace(",", "");
			String subCat = "";
			for (int i = 1; i < crumbs.size(); i++) {
				subCat += crumbs.get(i).getText().replace(",", "") + "; ";
			}
			String[] crumb = { mainCat, subCat };
			return crumb;
		} else {
			String[] crumb = { "-", "-" };
			return crumb;
		}

	}

	public static String[] getColours(WebDriver driver) {
		List<WebElement> colourOptions = driver.findElements(By
				.cssSelector("div.colour select option"));
		String[] colourList = new String[colourOptions.size()];
		for (int i = 0; i < colourOptions.size() - 1; i++) {
			colourList[i] = colourOptions.get(i + 1).getAttribute("value");
		}
		return colourList;

	}

	public static String[] getSizes(WebDriver driver) {
		WebElement sizeDropdown = driver.findElement(By.className("size"));
		List<WebElement> sizeOptions = sizeDropdown.findElements(By
				.cssSelector("option[value]"));
		String[] sizeList = new String[sizeOptions.size()];
		for (int i = 0; i < sizeOptions.size() - 1; i++) {
			sizeList[i] = sizeOptions.get(i + 1).getText();
		}
		return sizeList;

	}

	public static String extractImage(WebDriver driver) {
		String imageUrl = driver.findElement(By.cssSelector("img.main-image"))
				.getAttribute("src");
		return imageUrl;
	}

	public static ArrayList<String> extractBaseSitemap(URL url) {
		ArrayList<String> pageList = new ArrayList<String>();
		try {
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
				String REGEX = ".*?<loc>(?:(?!</loc>))";
				Pattern p = Pattern.compile(REGEX);
				Matcher m = p.matcher(pageLine);
				while (m.find()) {
					String address = m.group().split("</loc>")[0];
					if (isValidProductUrl(address)) {
						pageList.add(address);
					}
				}
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageList;
	}

	public static ArrayList<String> extractSitemap(ArrayList<String> siteList) {
		ArrayList<String> pageList = new ArrayList<String>();
		for (int i = 0; i < siteList.size(); i++) {
			try {
				URL url = new URL(siteList.get(i));
				pageList.addAll(extractBaseSitemap(url));
				System.out.println("Parsing sitemap " + i);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return pageList;
	}

	public static boolean isValidProductUrl(String string) {
		if (string.startsWith("http") && string.contains("asos.com")) {
			return true;
		} else {
			return false;
		}
	}

	public static void waitForElement(WebElement element) {
		for (int second = 0; second < 5; second++) {
			if (element.isDisplayed()) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
