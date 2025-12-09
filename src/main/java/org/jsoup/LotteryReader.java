package org.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LotteryReader {

    public static void main(String[] args) {

        try {
            String url = "https://results.govdoc.lk/results/govi-setha-4279";

            System.out.println("Connecting to: " + url);

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            System.out.println("\n===== EXTRACTED LOTTERY RESULTS =====\n");

            // Extract the title <h1>
            Element titleEl = doc.selectFirst("h1.mb-0");
            String title = (titleEl != null) ? titleEl.text() : "No title found";

            System.out.println("Title: " + title + "\n");

            // SELECT ONLY THE NUMBERS (4, 19, 54, 74)
            Elements nums = doc.select(".result-block.number");

            if (nums.isEmpty()) {
                System.out.println("Could not find lottery numbers!");
                return;
            }

            int i = 1;
            for (Element num : nums) {
                System.out.println("Number " + i + ": " + num.text());
                i++;
            }

            System.out.println("\n======================================");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
