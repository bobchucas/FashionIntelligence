package edu.uci.ics.crawler4j.zara;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Zara {

	public static void main(String[] args) {
	}

	public static String[] extractProperties(WebDriver driver) {
		String[] breadcrumb = extractBreadcrumb(driver);
		String[] string = { extractId(driver), extractName(driver),
				extractPrice(driver), extractColour(driver),
				extractSize(driver), extractAvailability(driver),
				breadcrumb[0], breadcrumb[1], sale(driver), getImage(driver) };
		return string;
	}

	public static String extractId(WebDriver driver) {
		return driver.findElement(By.className("reference")).getText().trim()
				.split(" ")[1];
	}

	public static String extractName(WebDriver driver) {
		return driver.findElement(By.cssSelector("div.right header h1"))
				.getText().trim();
	}

	public static String extractSize(WebDriver driver) {
		WebElement button = driver.findElement(By.id("size-btn"));
		button.click();
		List<WebElement> multipleValues = driver.findElement(
				By.className("size-select")).findElements(
				By.className("product-size"));
		if (multipleValues != null) {
			return Integer.toString(multipleValues.size());
		} else
			return "1";
	}

	public static String extractAvailability(WebDriver driver) {
		return "In Stock";
	}

	public static String extractColour(WebDriver driver) {
		List<WebElement> colourBox = driver.findElement(By.className("colors")).findElements(By.cssSelector("label"));
		if (colourBox != null) {
			return Integer.toString(colourBox.size());
		}
		else{
		return "1";
	}
	}

	public static String extractPrice(WebDriver driver) {
		String price = driver.findElement(
				By.cssSelector("p.price span.price")).getAttribute("data-price").split(" ")[0];
		return price;
	}

	public static String sale(WebDriver driver) {
		try {
			String salePrice = driver.findElement(
					By.cssSelector("p.price span.diagonal-line")).getText();
			if (salePrice != null) {
				return salePrice;
			} else {
				return "No sale";
			}
		} catch (Exception e) {
			return "No sale";
		}
	}

	public static String[] extractBreadcrumb(WebDriver driver) {
		WebElement breadcrumb = driver.findElement(By.cssSelector("div.breadcrumbs"));
		List<WebElement> crumbs = breadcrumb.findElements(By.cssSelector("li"));
		String mainCat = "-";
		String subCat = "-";
		switch (crumbs.size()) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			mainCat = crumbs.get(1).getText().split(">")[0].trim();
		case 4:
			mainCat = crumbs.get(1).getText().split(">")[0].trim();
			subCat = crumbs.get(2).getText().split(">")[0].trim();
		default:
			mainCat = crumbs.get(1).getText().split(">")[0].trim();
			subCat = crumbs.get(2).getText().split(">")[0].trim();
			for (int i = 0; i < crumbs.size() - 4; i++) {
				subCat += ";" + crumbs.get(i + 3);
			}
			break;
		}
		String[] crumb = { mainCat, subCat };
		return crumb;
	}

	public static String[] getSizes(WebDriver driver) {
		List<WebElement> multipleValues = driver.findElement(
				By.className("size-select")).findElements(
				By.cssSelector("tr.product-size"));
		String[] sizeList = new String[multipleValues.size()];
		for(int i=0;i<multipleValues.size();i++){
			sizeList[i]=multipleValues.get(i).findElement(By.className("size-name")).getText();
			if(multipleValues.get(i).toString().contains("disabled")){
				sizeList[i]+="-OS";
			}
		}
		return sizeList;
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
	
	public static String getImage(WebDriver driver) {
			String image = driver.findElement(
					By.cssSelector("div.bigImageContainer div.full img")).getAttribute("src");
				return image;
	}
	
	public static String[] getColours(WebDriver driver) {
		List<WebElement> colourBox = driver.findElement(By.className("colors")).findElements(By.cssSelector("label"));
		String[] colours = new String[colourBox.size()];
		for(int i=0;i<colours.length;i++){
			colours[i]=colourBox.get(i).findElement(By.cssSelector("a")).getAttribute("title");
//			}
		}
			return colours;
}
}
