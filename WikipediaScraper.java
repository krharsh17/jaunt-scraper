import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;

public class WikipediaScraper {
    public static void main(String[] args) {
        try {
            // Create a UserAgent instance
            UserAgent userAgent = new UserAgent();
            
            // Visit the Wikipedia page
            userAgent.visit("https://en.wikipedia.org/wiki/Web_scraping");

            // Find the title by its HTML tag
            Element title = userAgent.doc.findFirst("<span class=\"mw-page-title-main\">");

            // Print the title of the page
            System.out.println("Page title: " + title.innerHTML());
            
            // Find the "References" section by its HTML tag
            Element referencesSection = userAgent.doc.findFirst("<ol class=\"references\">");
            
            // Extract the list of references within the section
            Elements referencesList = referencesSection.findEvery("<span class=\"reference-text\">");

            System.out.println(referencesList.findAttributeValues("<a href>"));
            
        } catch (JauntException e) {
            e.printStackTrace();
        }
    }
}