import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;

import java.io.FileWriter;
import java.io.IOException;

public class DynamicScraper {
    public static void main(String[] args) {
        try {
            // Create a UserAgent instance
            UserAgent userAgent = new UserAgent();

            // Go to the main page
            userAgent.visit("https://www.scrapethissite.com/");

            // Find the sandbox link element from the nav links
            Element sandboxNavLink = userAgent.doc.findFirst("<li id=\"nav-sandbox\">");

            // Extract the sandbox page link
            String sandboxLink = sandboxNavLink.findAttributeValues("<a href>").get(0);

            // Navigate to the sandbox link
            userAgent.visit(sandboxLink);

            // List all links on the sandbox page
            Elements sandboxListItems = userAgent.doc.findEvery("<div class=\"page\">");

            // Find the hockey teams sandbox list item from the list
            Element hockeyTeamsListItem = sandboxListItems.toList().get(1);

            // Extract the hockey teams sandbox link
            String formsSandboxLink = hockeyTeamsListItem.findAttributeValues("<a href>").get(0);

            // Navigate to the hockey teams sandbox
            userAgent.visit(formsSandboxLink);

            // Fill out the seach form to find all teams starting with "new"
            userAgent.doc.filloutField("Search for Teams: ", "new");
            userAgent.doc.submit("Search");

            // Extract results table data
            extractTableData(userAgent, "first-page.csv");

            // Find the pagination element in the page
            Element paginationLinks = userAgent.doc.findFirst("<ul class=\"pagination\">");

            // Find the links for pagination
            Elements pageLinks = paginationLinks.findEvery("<li>");

            // Extract the element which has the link to the 2nd page
            Element nextPage = pageLinks.toList().get(1);

            // Extract the link from the element
            String nextPageLink = nextPage.findAttributeValues("<a href>").get(0);

            // Navigate to the next page
            userAgent.visit(nextPageLink);

            // Extract the table data once again
            extractTableData(userAgent, "second-page.csv");
            
        } catch (JauntException e) {
            e.printStackTrace();
        }
    }

    // Helper method to extract table data and store it in a CSV file
    public static void extractTableData(UserAgent userAgent, String fileName) {

        try {
            // Find the results table
            Element results = userAgent.doc.findFirst("<table>");

            // Create a FileWriter to write the data into a CSV file
            FileWriter csvWriter = new FileWriter(fileName);

            // Get the table headers
            Elements headers = results.findEvery("<th>");

            for (Element header: headers) {
                csvWriter.append(header.innerHTML().trim().replaceAll(",", ""));
                csvWriter.append(",");
            }

            // Add a new line after the header row
            csvWriter.append("\n"); 

            // Get the table rows
            Elements rows = results.findEvery("<tr class=\"team\">");
            
            // Iterate over the rows
            for (Element row : rows) {
                
                // Get the table cells within each row
                Elements cells = row.findEvery("<td>");
                
                // Iterate over the cells and write their text content to the CSV file
                for (Element cell : cells) {
                    csvWriter.append(cell.innerHTML().trim().replaceAll(",", ""));  // Remove commas from cell content
                    csvWriter.append(",");
                }

                // Add a new line after each row
                csvWriter.append("\n");  
            }
            
            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
