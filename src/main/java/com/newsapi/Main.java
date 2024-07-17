package com.newsapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    private static final String API_KEY = System.getenv("NEWS_API_KEY");
    private static final String BASE_URL = "https://newsapi.org/v2/everything";

    public void fetchNews(String keyword) {
        try {
            System.out.println("Fetching news for keyword: " + keyword);
            if (API_KEY == null || API_KEY.isEmpty()) {
                throw new IllegalStateException("API_KEY is not set");
            }
            String urlString = BASE_URL + "?q=" + keyword + "&apiKey=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            System.out.println("Sending request to: " + BASE_URL);

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            if (responseCode != 200) {
                System.err.println("Failed : HTTP error code : " + responseCode);
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                System.err.println("Error message: " + errorResponse.toString());
                return;
            }

            System.out.println("Request successful. Processing response...");

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String output;
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }
            }

            System.out.println("Response received. Parsing JSON...");

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray articles = jsonResponse.getJSONArray("articles");

            System.out.println("Found " + articles.length() + " articles.");

            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);
                System.out.println("Article " + (i + 1) + ":");
                System.out.println("Title: " + article.getString("title"));
                System.out.println("Description: " + article.getString("description"));
                System.out.println("URL: " + article.getString("url"));
                System.out.println("-------------------------------------------------------");
            }
        } catch (Exception e) {
            System.err.println("An error occurred:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Application starting...");
        
        if (args.length == 0) {
            System.err.println("Please provide a keyword as a command-line argument.");
            return;
        }
        
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("NEWS_API_KEY environment variable is not set. Please set it and try again.");
            return;
        }

        System.out.println("API_KEY is set (not showing for security reasons)");

        Main newsFetcher = new Main();
        newsFetcher.fetchNews(args[0]);

        System.out.println("Application finished.");
    }
}