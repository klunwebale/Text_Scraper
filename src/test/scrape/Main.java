package test.scrape;
public class Main {

	public static void main(String [] args) {

		// Check if no arguments specified
		if(args.length < 1) {
			System.out.println("Insufficient arguments.!! \nUsage : java -jar WebScrapper.jar [serach term] [optional - page number] ");
			System.exit(0);
		}
		
		/* Since responsibilities of DocumentLoader and Scraper are different,
		 * they are created as different classes and loader is injected into scraper */
		DocumentLoader documentLoader = new DocumentLoader();
		Scraper scraper = new Scraper(documentLoader);
		String keyword = args[0];
		
		/* If only one argument is specified then call 1st query with keyword 
		 * and print the result obtained */
		if(args.length == 1) {
			Integer totalCount = scraper.getTotalResults(keyword);
			System.out.println("total results : " + totalCount);
		}
	
		/* If more than one parameters specified, then store 2nd argument 
		 * as page_number and call 2nd query */
		if(args.length > 1) {
			int pageNumber = 0;
			try {
				pageNumber = Integer.parseInt(args[1]);
				if(pageNumber <=0 )
					throw new Exception("Negative page number");

			} catch(Exception e) {
				System.out.println("Second argument must be integer and positive");
			}
			scraper.getItemsAsPerPage(keyword,pageNumber);
		}	
	}
}
