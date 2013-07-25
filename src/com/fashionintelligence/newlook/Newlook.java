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
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

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
					bw.write("ID,Name,Size Choice,Colour Choice,Availability,Price,Category1,Category2,Sale,URL");
					bw.newLine();
					WebDriver driver = new FirefoxDriver();
					for (int i = 1500; i < 11500; i++) {
						String[] propertyString = null;
						try {
							driver.get(siteList.get(i));
							System.out.println(i);
							propertyString = extractProperties(driver);
							if (propertyString != null) {
								int productCount = 1;
								int colours = Integer
										.parseInt(propertyString[3]);
								if (colours == 0) {
									propertyString[3] = "1";
								}
								String[] colourList = null;
								if (colours > 1) {
									// TODO sizes
									productCount = colours;
									colourList = getColours(driver);
								}
								for (int k = 0; k < productCount; k++) {
									if (colourList != null) {
										propertyString[3] = colourList[k];
									} else {
										propertyString[3] = "-";
									}
									for (int j = 0; j < propertyString.length; j++) {
										bw.write(propertyString[j] + ",");
									}
									bw.write(siteList.get(i));
									bw.newLine();
									// bw.write(doc.toString());
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
					driver.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// System.out.println("Array empty");
			}
		}
	}

	public static String[] extractProperties(WebDriver driver) {
		if (!productPageCheck(driver)) {
			return null;
		} else {
			String colour = extractColour(driver);
			String[] breadcrumb = extractBreadcrumb(driver);
			String[] string = { extractId(driver), extractName(driver),
					extractSize(driver), colour, extractAvailability(driver),
					extractPrice(driver), breadcrumb[0], breadcrumb[1],
					sale(driver) };
			return string;
		}
	}

	public static boolean productPageCheck(WebDriver driver) {
		WebElement productPage = driver.findElement(By
				.className("product_display"));
		if (productPage != null) {
			return true;
		} else {
			return false;
		}
	}

	public static String extractId(WebDriver driver) {
		// System.out.println("Element: "+driver.findElement(By.cssSelector("span[itemprop=productID]")));
		// System.out.println("Text: "+driver.findElement(By.cssSelector("span[itemprop=productID]")).getText());
		return driver.findElement(By.cssSelector("span[itemprop=productID]"))
				.getText();
	}

	public static String extractName(WebDriver driver) {
		return driver.findElement(By.cssSelector("h1[itemprop=name]"))
				.getText();
	}

	public static String extractSize(WebDriver driver) {
		try {
			List<WebElement> sizeDropdown = driver.findElements(By
					.id("size_standard"));
			if (sizeDropdown.size() > 0 && sizeDropdown.get(0).isDisplayed()) {
				List<WebElement> sizes = sizeDropdown.get(0).findElements(
						By.cssSelector("option[value]"));
				return Integer.toString(sizes.size());
			} else {
				return "-";
			}
		} catch (Exception e) {
			return "-";
		}
	}

	public static String extractColour(WebDriver driver) {
		try {
			waitForElement(driver.findElement(By.className("colour")));
			WebElement colourOptions = driver.findElements(
					By.className("colour")).get(0);
			if (colourOptions != null) {
				return Integer.toString(colourOptions.findElements(
						By.className("colour-option")).size());
			} else {
				return "0";
			}
		} catch (Exception e) {
			return "0";
		}
	}

	public static String extractAvailability(WebDriver driver) {
		WebElement stock = driver.findElements(By.id("out_stock_message")).get(
				0);
		if (stock.isDisplayed()) {
			return "Out of stock";
		} else {
			return "In stock";
		}
	}

	public static String extractPrice(WebDriver driver) {
		return driver.findElement(By.cssSelector("span[itemprop=price]"))
				.getText();
	}

	public static String sale(WebDriver driver) {
		try {
			WebElement colourOptions = driver.findElement(By.className("was"));
			if (colourOptions != null) {
				return colourOptions.findElement(By.className("promovalue"))
						.getText();
			} else {
				return "No sale";
			}
		} catch (Exception e) {
			return "No sale";
		}
	}

	public static String[] extractBreadcrumb(WebDriver driver) {
		WebElement breadcrumb = driver.findElement(By.className("breadcrumb"));
		List<WebElement> crumbs = breadcrumb.findElements(By.cssSelector("li"));
		String mainCat = crumbs.get(1).getText().replace(",", "");
		String subCat = crumbs.get(2).getText().replace(",", "");
		String[] crumb = { mainCat, subCat };
		return crumb;
	}

	public static String[] getColours(WebDriver driver) {
		List<WebElement> colourOptions = driver.findElements(By
				.className("colour-option"));
		String[] colourList = new String[colourOptions.size()];
		for (int i = 0; i < colourOptions.size(); i++) {
			colourList[i] = colourOptions.get(i)
					.findElement(By.cssSelector("a")).getAttribute("title");
		}
		return colourList;

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
