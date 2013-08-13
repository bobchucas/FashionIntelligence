/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.uci.ics.crawler4j.zara;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawler extends WebCrawler {
	private int i = 0;
	private int j = 0;
	private String productString = "id=\"product\"";

	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				&& href.startsWith("http://www.zara.com/uk/");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page, PrintWriter bw, WebDriver driver) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String url = page.getWebURL().getURL();
		System.out.println(i + ": " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			if (html.contains(productString)) {
				String[] propertyString = null;
				try {
					driver.get(url);
					System.out.println(url);
					propertyString = Zara.extractProperties(driver);
					if (propertyString != null) {
						int colours = Integer.parseInt(propertyString[3]);
						int sizes = Integer.parseInt(propertyString[4]);
						if (colours > 1 || sizes > 1) {
							String[] colourString = null;
							String[] sizeString = null;
							for (int k = 0; k < sizes; k++) {
								if (sizes > 1) {
									sizeString = Zara.getSizes(driver);
									if (sizeString[k].contains("-OS")) {
										propertyString[5] = "Out of Stock";
									}else{
										propertyString[5]="In Stock";
									}
									propertyString[4] = sizeString[k].split("-")[0];
								} else {
									propertyString[4] = "-";
								}
								for (int l = 0; l < colours; l++) {
									if (colours > 1) {
										colourString = Zara.getColours(driver);
										propertyString[3] = colourString[l];
									} else {
										propertyString[3] = "-";
									}
									bw.append("Zara,");
									for (int j = 0; j < propertyString.length; j++) {
										bw.append(propertyString[j] + ",");
									}
									Date date = new Date();
									bw.append(dateFormat.format(date) + ",");
									bw.append(url);
									bw.append("\r\n");
								}
							}
						} else {
							propertyString[3] = "-";
							propertyString[4] = "-";
							bw.append("Zara,");
							for (int j = 0; j < propertyString.length; j++) {
								bw.append(propertyString[j] + ",");
							}
							Date date = new Date();
							bw.append(dateFormat.format(date) + ",");
							bw.append(url);
							bw.append("\r\n");
							System.out.println("BW writing");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();

				} finally {

				}
				j++;
			}
		}

		// Header[] responseHeaders = page.getFetchResponseHeaders();
		// if (responseHeaders != null) {
		// System.out.println("Response headers:");
		// for (Header header : responseHeaders) {
		// System.out.println("\t" + header.getName() + ": " +
		// header.getValue());
		// }
		// }

		i++;
	}
}
