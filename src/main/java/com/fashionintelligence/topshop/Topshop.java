package com.fashionintelligence.topshop;

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
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Topshop {

	public static void main(String[] args) {
		ArrayList<String> siteList = new ArrayList<String>();
		System.setProperty("http.agent", "");
		try {
			URL url = new URL("http://www.topshop.com/sitemap.xml");
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
						&& pageLine.contains("ProductDisplay?")
						) {
					siteList.add(pageLine.substring(9,pageLine.length()- 6).replace("#x26;", ""));
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
					bw.write("ID,Name,Price,Colour,Size,Stock,Category 1, Category 2, Sale, Date, URL");
					bw.newLine();
					WebDriver driver = new HtmlUnitDriver();
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd HH:mm:ss");
					for (int i = 0;i<siteList.size(); i++) {
						String[] propertyString = null;
						try {
							driver.get(siteList.get(i));
							System.out.println(siteList.get(i));
							propertyString = extractProperties(driver);
							if(propertyString!=null){
							for (int j=0; j<propertyString.length; j++)
							{
							System.out.println(propertyString[j]);
							}
							if (propertyString[4] != null) 
								{
								String[] sizeString = null;
								sizeString = getSizes(driver);
								String [] stockString = null;
								stockString = getStock(driver);
								int sizes = sizeString.length;
								if(!propertyString[8].contains("sale")){
									String sale=propertyString[2];
									propertyString[2]=propertyString[8];
									propertyString[8]=sale;
								}
								for(int k=0; k<sizes; k++)
								{
									if(sizes>1)
									{
									propertyString[4] = sizeString[k];
									propertyString[5] = stockString[k];
									}
									else
									{
										propertyString[4] = "-";
										propertyString[5] = "-";
									}
									for (int j = 0; j < propertyString.length; j++) 
									{
										bw.write(propertyString[j]
														+ ",");
									}	
									Date date = new Date();
									bw.write(dateFormat.format(date) + ",");
									bw.write(siteList.get(i));
									bw.newLine();
									}
								}
							}
						}
						catch (Exception e) {
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
			
			String[] breadcrumb = extractBreadcrumb(driver);
			String[] string = { extractId(driver),extractName(driver),  extractPrice(driver), extractColour(driver), extractSize(driver), extractAvailability(driver),  breadcrumb[0], breadcrumb[1],sale(driver)};
			return string;
		}
	}

	public static boolean productPageCheck(WebDriver driver) {
		List<WebElement> productPage = driver.findElements(By
				.className("error"));
		if (productPage.size()==0) {
			return true;
		} else {
			System.out.println("Error page returned");
			return false;
		}
	}

	public static String extractId(WebDriver driver) {
				return driver.findElement(By.cssSelector("li.product_code"))
				.getText().substring(11);
	}

	public static String extractName(WebDriver driver) {
		return driver.findElement(By.cssSelector("h1"))
				.getText();
	}

	
	public static String extractSize(WebDriver driver)
	{
		String multipleValue = driver.findElement(By.id("item0")).getAttribute("value");
		if (multipleValue != null)
		{
			String problemSolver = multipleValue.replace("[", "START").replace("],", "");
			String[] divider = problemSolver.split("START");
			String size = divider[1].replaceAll("'", "");
			System.out.println(size);	
			return size;
		}
		else return "Size not available";
	}			
			
	public static String extractAvailability(WebDriver driver)
	{
		String multipleValue = driver.findElement(By.id("item0")).getAttribute("value");
		if (multipleValue != null)
		{
			String problemSolver = multipleValue.replace("[", "START").replace("]);", "");
			String[] divider = problemSolver.split("START");
			String stock = divider[2].replaceAll("'", "");
			return stock;
		}
		else return "Data not available";
	}
	
 
	public static String extractColour(WebDriver driver) {
		String multipleValue = driver.findElement(By.id("item0")).getAttribute("value");
		if (multipleValue != null)
		{
			String[] divider = multipleValue.split(",");
			if (divider!= null){
				String color = divider[0].substring(16).replace("'", "");
				System.out.println(color);
				return color;
			}							
		}
		return "Not Applicable";
	}


	public static String extractPrice(WebDriver driver) {
		return driver.findElement(By.cssSelector("li.product_price span"))
				.getText();
	}

	public static String sale(WebDriver driver) {
		try {
			String salePrice = driver.findElement(By.cssSelector("li.now_price")).getText();
			if (salePrice != null) {
				return driver.findElement(By.cssSelector("li.now_price span"))
						.getText();
			} else {
				return "No sale";
			}
		} catch (Exception e) {
			return "No sale";
		}
	}

	public static String[] extractBreadcrumb(WebDriver driver) {
		WebElement breadcrumb = driver.findElement(By.id("nav_breadcrumb"));
	    List<WebElement> crumbs = breadcrumb.findElements(By.cssSelector("li"));
		String mainCat = crumbs.get(1).getText();
		String subCat = crumbs.get(2).getText();
		String[] crumb = { mainCat, subCat };
		return crumb;
	}

		
	public static String[] getSizes(WebDriver driver) {
		String multipleValue = driver.findElement(By.id("item0")).getAttribute("value");
		String problemSolver = multipleValue.replace("[", "START").replace("],", "");
		String[] divider = problemSolver.split("START");
		String size = divider[1].replaceAll("'", "");
		String[] sizeList = size.split(",");						
		return sizeList;
	}
	public static String[] getStock(WebDriver driver) {
		String multipleValue = driver.findElement(By.id("item0")).getAttribute("value");
		String problemSolver = multipleValue.replace("[", "START").replace("]);", "");
		String[] divider = problemSolver.split("START");
		String stock = divider[2].replaceAll("'", "");
		String[] stockList = stock.split(",");						
		return stockList;
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
