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

package edu.uci.ics.crawler4j.asos;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
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
				&& href.contains("http://www.asos.com")
				&& !href.contains("asos.com/fr/")
				&& !href.contains("asos.com/ru/")
				&& !href.contains("asos.com/it/")
				&& !href.contains("asos.com/au/")
				&& !href.contains("asos.com/de/")
				&& !href.contains("asos.com/sp/")
				&& !href.contains("asos.com/account/")
				&& !href.contains("us.asos.com");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page, PrintWriter bw, WebDriver driver) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();

		System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);
		System.out.println("Domain: '" + domain + "'");
		System.out.println("Sub-domain: '" + subDomain + "'");
		System.out.println("Path: '" + path + "'");
		System.out.println("Parent page: " + parentUrl);
		System.out.println("Anchor text: " + anchor);
		
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			List<WebURL> links = htmlParseData.getOutgoingUrls();
			
			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		System.out.println(i + ": " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			if (url.toLowerCase().contains("pgeproduct")) {
				String[] propertyString = null;
				try {
					driver.get(url);
					System.out.println(url);
					propertyString = Asos.extractProperties(driver);
					if (propertyString != null) {
						if (propertyString[4] != null) {
							String[] sizeString = null;
							sizeString = Asos.getSizes(driver);
							String[] stockString = null;
							stockString = Asos.getStock(driver);
							int sizes = sizeString.length;
							if (!propertyString[8].contains("sale")) {
								String sale = propertyString[2];
								propertyString[2] = propertyString[8];
								propertyString[8] = sale;
							}
							for (int k = 0; k < sizes; k++) {
								if (sizes > 1) {
									propertyString[4] = sizeString[k];
									propertyString[5] = stockString[k];
								} else {
									propertyString[4] = "-";
									propertyString[5] = "-";
								}
								for (int j = 0; j < propertyString.length; j++) {
									bw.append(propertyString[j] + ",");
								}
								Date date = new Date();
								bw.append(dateFormat.format(date) + ",");
								bw.append(url);
								bw.append("\r\n");
							}
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
