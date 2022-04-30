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
        double latitude = 50.316368;
        double longitude = -3.606911;
        List<String> parametersNeeded = Arrays.asList("waveHeight", "windDirection", "windSpeed", "waterTemperature");
        String parameters = String.join(",", parametersNeeded);
        String stormglassURL = String.format("https://api.stormglass.io/v2/weather/point?lat=%f&lng=%f&params=%s&source=sg", latitude, longitude, parameters);
        System.out.println(stormglassURL);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(stormglassURL))
                .header("Authorization", "08d87bfa-c860-11ec-9863-0242ac130002-08d87c68-c860-11ec-9863-0242ac130002")
                .build();
//        Tells server we want to receive response body as a string
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }


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

