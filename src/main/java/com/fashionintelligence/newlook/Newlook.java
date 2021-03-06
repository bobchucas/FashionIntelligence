package com.fashionintelligence.newlook;

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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Newlook {

	// TODO: Export extraction methods to external classes. Establish if
	// possible to scale with other stores using abstract classes (difficult due
	// to conflicting nature of elicitation of properties). Increase logging
	// quality/output. Mavenise
	public static void main(String[] args) {
		ArrayList<String> siteList = new ArrayList<String>();
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
					bw.write("ID,Name,Size Choice,Colour Choice,Availability,Price,Category1,Category2,Sale,Image,Date,URL");
					bw.newLine();
					WebDriver driver = new FirefoxDriver();
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd HH:mm:ss");
					for (int i = 1600; i < 1603; i++) {
						String[] propertyString = null;
						try {
							try {
								driver.get(siteList.get(i));
							} catch (Exception e) {
								driver = new FirefoxDriver();
								driver.get(siteList.get(i));
							}
							System.out.println(i);
							propertyString = extractProperties(driver);
							if (propertyString != null) {
								int colours = Integer
										.parseInt(propertyString[3]);
								int sizes = Integer.parseInt(propertyString[2]);
								if (colours > 1 || sizes > 1) {
									String[] colourString = null;
									String[] sizeString = null;
									for (int k = 0; k < sizes; k++) {
										if (sizes > 1) {
											sizeString = getSizes(driver);
											propertyString[2] = sizeString[k]
													.split("-")[0];
											propertyString[4] = sizeString[k]
													.split("-")[1].trim();
										} else {
											propertyString[2] = "-";
										}
										for (int l = 0; l < colours; l++) {
											if (colours > 1) {
												colourString = getColours(driver);
												propertyString[3] = colourString[l];
											} else {
												propertyString[3] = "-";
											}
											for (int j = 0; j < propertyString.length; j++) {
												bw.write(propertyString[j]
														+ ",");
											}
											Date date = new Date();
											bw.write(dateFormat.format(date)
													+ ",");
											bw.write(siteList.get(i));
											bw.newLine();
										}
									}
								} else {
									propertyString[2] = "-";
									propertyString[3] = "-";
									for (int j = 0; j < propertyString.length; j++) {
										bw.write(propertyString[j] + ",");
									}
									Date date = new Date();
									bw.write(dateFormat.format(date) + ",");
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
						if (i % 100 == 0) {
							try {
								driver.quit();
							} catch (Exception e) {
							}
							driver = new FirefoxDriver();
						}
					}
					bw.close();
					driver.quit();
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
					sale(driver), extractImage(driver) };
			return string;
		}
	}

	public static boolean productPageCheck(WebDriver driver) {
		List<WebElement> productPage = driver.findElements(By
				.className("product_display"));
		if (productPage.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String extractId(WebDriver driver) {
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
				return "1";
			}
		} catch (Exception e) {
			return "1";
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
				return "1";
			}
		} catch (Exception e) {
			return "1";
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

	public static String[] getSizes(WebDriver driver) {
		WebElement sizeDropdown = driver.findElement(By.className("sizes"));
		List<WebElement> sizeOptions = sizeDropdown.findElements(By
				.cssSelector("option[value]"));
		String[] sizeList = new String[sizeOptions.size()];
		for (int i = 0; i < sizeOptions.size(); i++) {
			sizeList[i] = sizeOptions.get(i).getText();
		}
		return sizeList;

	}

	public static String extractImage(WebDriver driver) {
		String imageUrl = driver.findElement(By.cssSelector("li.li_thumb img"))
				.getAttribute("src");
		return imageUrl.substring(0, imageUrl.indexOf("?"));
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
