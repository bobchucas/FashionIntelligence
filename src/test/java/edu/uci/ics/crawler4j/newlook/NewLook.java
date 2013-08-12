package edu.uci.ics.crawler4j.newlook;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NewLook {

	public static void main(String[] args) {
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
