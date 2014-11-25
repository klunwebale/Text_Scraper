/* This class crawls to the given URL and returns the fetched document */

package test.scrape;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocumentLoader {
	public Document loadDocument(String url) {

		try {
			Document doc = Jsoup.connect(url).get();
			return doc;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
