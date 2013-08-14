package edu.uci.ics.crawler4j.topshop;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Topshop {

	public static void main(String[] args) {
	}

	public static String[] extractProperties(WebDriver driver) {
			String[] breadcrumb = extractBreadcrumb(driver);
			String[] string = { extractId(driver), extractName(driver),
					extractPrice(driver), extractColour(driver),
					extractSize(driver), extractAvailability(driver),
					breadcrumb[0], breadcrumb[1], sale(driver), getImage(driver)};
			return string;
	}

	public static String extractId(WebDriver driver) {
		return driver.findElement(By.cssSelector("li.product_code")).getText()
				.substring(11);
	}

	public static String extractName(WebDriver driver) {
		return driver.findElement(By.cssSelector("h1")).getText();
	}

	public static String extractSize(WebDriver driver) {
		String multipleValue = driver.findElement(By.id("item0")).getAttribute(
				"value");
		if (multipleValue != null) {
			String problemSolver = multipleValue.replace("[", "START").replace(
					"],", "");
			String[] divider = problemSolver.split("START");
			String size = divider[1].replaceAll("'", "");
			return size;
		} else
			return "Size not available";
	}

	public static String extractAvailability(WebDriver driver) {
		String multipleValue = driver.findElement(By.id("item0")).getAttribute(
				"value");
		if (multipleValue != null) {
			String problemSolver = multipleValue.replace("[", "START").replace(
					"]);", "");
			String[] divider = problemSolver.split("START");
			String stock = divider[2].replaceAll("'", "");
			return stock;
		} else
			return "Data not available";
	}

	public static String extractColour(WebDriver driver) {
		String multipleValue = driver.findElement(By.id("item0")).getAttribute(
				"value");
		if (multipleValue != null) {
			String[] divider = multipleValue.split(",");
			if (divider != null) {
				String color = divider[0].substring(16).replace("'", "");
				return color;
			}
		}
		return "Not Applicable";
	}

	public static String extractPrice(WebDriver driver) {
		String price = driver.findElement(By.cssSelector("li.product_price span"))
				.getText();
			return price;
	}

	public static String sale(WebDriver driver) {
		try {
			String salePrice = driver.findElement(
					By.cssSelector("li.now_price")).getText();
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
		String mainCat="-";
		String subCat="-";
		switch (crumbs.size()) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			mainCat=crumbs.get(1).getText();
		case 4:
			mainCat=crumbs.get(1).getText();
			subCat=crumbs.get(2).getText();
		default:
			mainCat=crumbs.get(1).getText();
			subCat=crumbs.get(2).getText();
			for(int i=0;i<crumbs.size()-4;i++){
				subCat+=";"+crumbs.get(i+3);
			}
			break;
		}
		String[] crumb = { mainCat, subCat };
		return crumb;
	}

	public static String[] getSizes(WebDriver driver) {
		String multipleValue = driver.findElement(By.id("item0")).getAttribute(
				"value");
		String problemSolver = multipleValue.replace("[", "START").replace(
				"],", "");
		String[] divider = problemSolver.split("START");
		String size = divider[1].replaceAll("'", "");
		String[] sizeList = size.split(",");
		return sizeList;
	}

	public static String[] getStock(WebDriver driver) {
		String multipleValue = driver.findElement(By.id("item0")).getAttribute(
				"value");
		String problemSolver = multipleValue.replace("[", "START").replace(
				"]);", "");
		String[] divider = problemSolver.split("START");
		String stock = divider[2].replaceAll("'", "");
		String[] stockList = stock.split(",");
		return stockList;
	}

	public static String getImage(WebDriver driver) {
			WebElement salePrice = driver.findElement(
					By.cssSelector("div.wrapper_product_view a img"));
				return salePrice.getAttribute("src");
	}
}
