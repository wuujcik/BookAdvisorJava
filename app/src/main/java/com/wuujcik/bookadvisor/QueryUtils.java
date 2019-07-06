package com.wuujcik.bookadvisor;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from API.
 */
public final class QueryUtils {

    //Tag for the log messages
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    //Returns new URL object from the given string URL
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    //Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();


        // Try to parse the JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            //Create JSONObject from JSON_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            //Extract the JSONArray associated with key "items"
            // that represents a list of books
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            //For each bookArray, create an {@link Book} object
            for (int i = 0; i < bookArray.length() ; i++){
                //get currentBookObject
                JSONObject currentBookObject = bookArray.getJSONObject(i);
                //extract from the currentBookObject a JSONObject associated with key "volumeInfo"
                JSONObject volumeInfo = currentBookObject.getJSONObject("volumeInfo");

                //Extract the values from volumeInfo for the key: title
                String title = volumeInfo.getString("title");

                //Extract the values from volumeInfo for the key: authors
 //               String author = volumeInfo.getString("authors");

                //String[] author;
                JSONArray authors = volumeInfo.getJSONArray("authors");
                String[] author = authors.toString().replace("\",\"", "; ").split(",");



                String description;
                try{
                description = volumeInfo.getString("description");
                } catch(Exception oe){
                    description = "";
                }


                //extract from the "volumeInfo" a JSONObject associated with key "imageLinks"
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                //Extract the values from imageLinks for the key: smallThumbnail
                String image = imageLinks.getString("smallThumbnail");



                //extract from the currentBookObject a JSONObject associated with key "saleInfo"
                JSONObject saleInfo = currentBookObject.getJSONObject("saleInfo");

                //Extract the values from saleInfo for the key: saleability
                //in case the book is not for sale, return early with no price or link available
                if (saleInfo.getString("saleability").equals("FOR_SALE")) {

                    //Extract the values from saleInfo for the key: buyLink
                    String web = saleInfo.getString("buyLink");
                    //extract from the "saleInfo" a JSONObject associated with key "retailPrice"
                    JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
                    //Extract the values from retailPrice for the key: amount
                    double price = retailPrice.getDouble("amount");

                    books.add(new Book(author, title, description, price, web, image));

                } else if (saleInfo.getString("saleability").equals("NOT_FOR_SALE")) {
                    books.add(new Book(author, title, description, 0, "not available",image));
            }}


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the books JSON results", e);
        }

        // Return the list of books
        return books;
    }

    /**
     * Query the API dataset and return a list of {@link Book} objects to represent a single book.
     * @param requestUrl
     */
    public static List<Book> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Book> book = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return book;
    }



}
