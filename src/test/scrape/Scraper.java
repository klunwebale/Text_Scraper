/* This class scrapes the given document and returns result of the 2 queries  */

package test.scrape;

import java.util.ArrayList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraper {
	DocumentLoader documentLoader = null;

	public Scraper(DocumentLoader documentLoader) {
		this.documentLoader = documentLoader;
	}

	/* This method specifies Answer to 1st query */
	public Integer getTotalResults(String keyword) {

		String url = "http://www.walmart.com/search/?query="+keyword;
		Document doc = documentLoader.loadDocument(url);

		/* The total  number of results is displayed on the left hand side of 
		 * page. So extracted count data from the div containing results */
		String[] resultData = null;
		try {
			String resultSummary = doc.select("div[class=result-summary-container]").html();
			int start = resultSummary.indexOf("Showing");
			int end = resultSummary.indexOf("results");
			resultData = resultSummary.substring(start, end).split("of");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return Integer.parseInt(resultData[1].trim());
	}

	/* This method specifies Answer to 2nd query */
	public void getItemsAsPerPage(String keyword, int page) {
		if(getTotalResults(keyword) == 0){
			/* If the keyword is a random string not specifying any product name 
			 * Then the result for that product is going to be zero, so we need proceed further */
			System.out.println("No results exists for this product");
		}
		else{
			String url = "http://www.walmart.com/search/?page="+page+"&query=" + keyword;
			Document doc = documentLoader.loadDocument(url);

			try {
				int totalPages = getTotalPages(doc);
				System.out.println("pages:" + totalPages);
				/* if the given page number is greater than the total number of pages fetched 
				 * in the query result we give a message and show the contents of the first page */
				if(page > totalPages) {
					System.out.println("Given page index - "+page+" is greater than the total pages for this product.");
					System.out.println("Hence showing the contents of the first page");
				}
				/* Iterate over the list of result objects to print details of individual result object */
				ArrayList<Result> results = extractItems(doc);
				for(Result result : results) {
					System.out.println("\nTitle: " + result.getTitle() + "\nPrice: " + result.getPrice());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int getTotalPages(Document doc) {
		int totalPages = 0;
		// get the max page number for the query result
		Element content = doc.getElementById("paginator-container");
		Elements links = content.select("a[href*=query]"); 
		for(Element l : links) {
			try {
				int i = Integer.parseInt(l.html());
				if(i > totalPages) {
					totalPages = i;
				}
			} catch (Exception e1) { }
		}
		return totalPages;
	}

	/* This method find the title for the item.
	 * the title of the item is in an anchor tag with class=js-product-title */
	private String getTitle(Element element) {
		try {
			String itemName = element.select("a[class=js-product-title]").get(0).text();
			return itemName;
		} catch (Exception e3) {
			System.out.println("Title : **** ERROR ***");
			System.err.println(e3.getMessage());
		}
		return "";
	}

	private String getPrice(Element e) {
		/**
		 * To find the price, we need to check 3 different things
		 * 1. if an absolute price is given, then it will be displayed in a div tag having class=item-price-container
		 * 
		 * 2. if the div in (1) does not exists, then the item is "In stores only"
		 * 
		 * 3. within the price-container div, we check for a span with class=price price-display 
		 * if exists, then we extract the actual price.
		 * Else we print "shown after checkout" 
		 * */
		try {
			Elements priceContainer = e.select("div[class=item-price-container]");
			if(priceContainer.size()==0) {
				return "In stores only";
			}
			Element valueElement = priceContainer.get(0);
			if(valueElement.select("span[class=price price-display]").size()>0) {
				return valueElement.text();
			} else {
				return "Shown in Checkout";
			}
		} catch (Exception e4) {
			System.out.println("Price : **** ERROR **** ");
			System.err.println(e4.getMessage());
		}
		return "";
	}

	/* This method obtains total items per page. Each item is created as a new object 'result' and 
	 * pushed into the list containing all results per page. FInally this list is returned. */
	private ArrayList<Result> extractItems(Document doc) {
		ArrayList<Result> results = new ArrayList<Result>();

		/* every item has a row. this row is identified by a div element which contains the 
		 * item-id as the data element i.e., data-item-id="xxxxxx". 
		 * hence we select all such div that have the data attribute which gives us the total products in the page 
		 */
		Elements items = doc.select("div[data-item-id]");
		System.out.println("Total items in current page : " + items.size());

		for(Element item : items) {
			results.add(new Result(getTitle(item), getPrice(item)));
		}
		return results;
	}

}
