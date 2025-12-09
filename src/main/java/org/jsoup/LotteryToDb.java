package org.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class LotteryToDb {

    private static final String JDBC_URL = "jdbc:mysql://13.234.242.165:3306/a1?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "iDevice";
    private static final String DB_PASS = "iDevice_123456";

    private static final String PAGE_URL = "https://results.govdoc.lk/results/govi-setha-4279";

    public static void main(String[] args) {
        try {
            System.out.println("Connecting to page: " + PAGE_URL);
            Document doc = Jsoup.connect(PAGE_URL)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            Elements numberElements = doc.select(".result-block.number");

            if (numberElements.isEmpty()) {
                System.out.println("Could not find lottery numbers on the page.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Element e : numberElements) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(e.text().trim());
            }
            String numbers = sb.toString();

            Element letterEl = doc.selectFirst(".result-block.letter");
            String letter = letterEl == null ? "" : letterEl.text().trim();

            Element h1 = doc.selectFirst("h1");
            String drawTitle = (h1 != null) ? h1.text() : "Unknown Title";

            System.out.println("Scraped data:");
            System.out.println(" - Title: " + drawTitle);
            System.out.println(" - Letter: " + letter);
            System.out.println(" - Numbers: " + numbers);

            System.out.println("Connecting to database...");
            try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {

                String sql = "INSERT INTO lottery_results (source, draw_no, draw_date, numbers, bonus) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, PAGE_URL);
                    ps.setString(2, drawTitle);
                    ps.setNull(3, java.sql.Types.DATE);  // IMPORTANT FIX HERE
                    ps.setString(4, numbers);
                    ps.setString(5, letter);

                    int rows = ps.executeUpdate();
                    System.out.println("Inserted rows: " + rows);

                }
            }

            System.out.println("Done. Check the DB table lottery_results.");

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
