package com.github.louiepietroni.surfsy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class APIHandler {


    public static void main(String[] args) {
//        Define the coordinates you want to get the data for, this is a beach I found in devon
        double latitude = 50.316368;
        double longitude = -3.606911;
//        Here are the parameters we want to get, to see all the options, visit:
//        https://docs.stormglass.io/?_gl=1*1idq2mb*_ga*MTk3Nzk2ODkxMC4xNjUxMzA3Mzgz*_ga_79XDW52F27*MTY1MTU4NDA1Mi40LjAuMTY1MTU4NDA1Mi4w&_ga=2.110683266.620259759.1651507951-1977968910.1651307383#/weather
        List<String> parametersNeeded = Arrays.asList("waveHeight", "windDirection", "windSpeed", "waterTemperature");
        String parameters = String.join(",", parametersNeeded);
//        This line creates the string which is used to get from the api, also using source as sg which means we guarantee we will always get exactly one value for everything
        String stormglassURL = String.format("https://api.stormglass.io/v2/weather/point?lat=%f&lng=%f&params=%s&source=sg", latitude, longitude, parameters);
//        I just print out the url so you can see the format and it is as you expect
        System.out.println(stormglassURL);

//        This sets up a client for the api
        HttpClient client = HttpClient.newHttpClient();
//        Make a request for the api
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(stormglassURL))
//                This bit here is my key for the API, which is sent as a header
                .header("Authorization", "08d87bfa-c860-11ec-9863-0242ac130002-08d87c68-c860-11ec-9863-0242ac130002")
                .build();
//        Tells server we want to receive response body as a string
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }


//    This was from testing, ignore this block here, from a website which gives you just json test data
////        Example code for generic get
//        String placeholderURL = "https://jsonplaceholder.typicode.com/albums";
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(placeholderURL)).build();
////        Tells server we want to receive response body as a string
//        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                .thenApply(HttpResponse::body)
//                .thenAccept(System.out::println)
//                .join();
//    }


//    This is an alternative method to get the api request, this is a bit naf but i've left it here just in case something doesn't work with the above method. It's also not async so not as good
//    private static HttpURLConnection connection;
//
//    public static void main(String[] args) throws IOException {
//        BufferedReader reader;
//        String line;
//        StringBuffer responseContent = new StringBuffer();
//
//        URL url = new URL("https://jsonplaceholder.typicode.com/albums");
//        connection = (HttpURLConnection) url.openConnection();
//
//        connection.setRequestMethod("GET");
//        connection.setConnectTimeout(5000);
//        connection.setReadTimeout(5000);
//
//        int status = connection.getResponseCode();
////        Test for success, 200
////        System.out.println(status);
//
//        if (status > 200) {
//            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
//            while ((line = reader.readLine()) != null) {
//                responseContent.append(line);
//            }
//            reader.close();
//        } else {
//            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            while ((line = reader.readLine()) != null) {
//                responseContent.append(line);
//            }
//            reader.close();
//        }
//        System.out.println(responseContent.toString());
//
//        connection.disconnect();
//    }
}

