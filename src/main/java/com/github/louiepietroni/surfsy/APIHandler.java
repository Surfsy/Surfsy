package com.github.louiepietroni.surfsy;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import org.json.*;

//hello world
public class APIHandler {
    private static HashMap<String, JSONObject> hashMap = loadCache();
    private static final String CACHE_PATH = "./src/main/java/com/github/louiepietroni/surfsy/data/api_cache.properties";

    private APIHandler() {}

    private static HashMap<String, JSONObject> loadCache() {
        HashMap<String, JSONObject> cacheMap = new HashMap<>();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(CACHE_PATH));
            for(String key : properties.stringPropertyNames()) {
                cacheMap.put(key, new JSONObject(properties.get(key).toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cacheMap;
    }

    private static void storeCache() {
        Properties properties = new Properties();

        for(Map.Entry<String, JSONObject> entry : hashMap.entrySet()) {
            properties.put(entry.getKey(), entry.getValue().toString());
        }
        try {
            properties.store(new FileOutputStream(CACHE_PATH), null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to cache the data into the file.");
        }
    }

    private static JSONObject checkLocation(Double latitude, Double longitude) throws IOException, ParseException {
        String hashKey = latitude.toString() + longitude.toString();
        JSONObject hashValue = hashMap.getOrDefault(hashKey, null);

        if (hashValue == null) {
            hashMap.put(hashKey, apiCall(latitude, longitude));
            storeCache();
        }
        else {
            String latestTimeUpdate = hashValue.getJSONObject("meta").getString("start");

            Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(latestTimeUpdate);
            String date_string = new SimpleDateFormat("dd/MM/yyyy").format(date);
            String today_string = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            if (!date_string.equals(today_string)) {
                hashMap.put(hashKey, apiCall(latitude, longitude));
                storeCache();
            }
        }
        return hashMap.get(hashKey);
    }

    private static HashMap<String, List<Double>> jsonToHashMap(JSONObject jsonData) {
        HashMap<String, List<Double>> map = new HashMap<>();
        JSONArray hours = jsonData.getJSONArray("hours");

        for(String key : hours.getJSONObject(0).keySet()){
            if(key.equals("time")) continue;
            map.put(key, new LinkedList<>());
        }

        for(int i = 0; i < hours.length(); i++) {
            for(String key : hours.getJSONObject(i).keySet()){
                if(key.equals("time")) continue;
//                map.putIfAbsent(key, new LinkedList<>());
                map.get(key).add(hours.getJSONObject(i).getJSONObject(key).getDouble("sg"));
            }
        }
        return map;
    }

    public static HashMap<String, List<Double>> fetchLocation(Double latitude, Double longitude) throws IOException, ParseException {
        JSONObject jsonData = checkLocation(latitude, longitude);
        return jsonToHashMap(jsonData);
    }

    public static JSONObject apiCall(double latitude, double longitude) throws IOException {
        List<String> parameters = Arrays.asList("airTemperature", "cloudCover", "currentDirection", "currentSpeed", "precipitation", "seaLevel", "visibility", "waterTemperature", "waveDirection", "waveHeight", "wavePeriod", "windDirection", "windSpeed");
        String params = String.join(",", parameters);
        String stormglassURL = String.format("https://api.stormglass.io/v2/weather/point?lat=%f&lng=%f&params=%s&source=sg", latitude, longitude, params);

        HttpURLConnection connection;

        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();

        URL url = new URL(stormglassURL);
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "08d87bfa-c860-11ec-9863-0242ac130002-08d87c68-c860-11ec-9863-0242ac130002");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        reader.close();
        connection.disconnect();

        return new JSONObject(responseContent.toString());
    }

    private static void printHashMap(HashMap<String, List<Double>> map) {
        for (String key : map.keySet()) {
            List<Double> values = map.get(key);
            System.out.println("Key: " + key + " " + values.toString());
        }
    }

    public static void main(String[] args) throws ParseException, IOException {
        HashMap<String, List<Double>> testMap = fetchLocation(50.2816397,-3.8950334);
        printHashMap(testMap);
        System.out.println();

        HashMap<String, List<Double>> testMap1 = fetchLocation(50.41747,-5.10384);
        printHashMap(testMap1);
        System.out.println();
    }
}