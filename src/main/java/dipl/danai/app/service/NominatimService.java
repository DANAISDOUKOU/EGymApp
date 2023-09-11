package dipl.danai.app.service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NominatimService {
    private final static String nominatimBaseUrl = "https://nominatim.openstreetmap.org/";
    private final static String apiKey = "1lKTiR5sbiOjVJKu6U9TDWpFcHNdQWRiF6KXfXc9"; 

    public static Map<String, Double> getCoordinatesForAddressInCity(String address,String city) {
        Map<String, Double> coordinates = new HashMap<>();

        try {
            // Encode the address parameter
            String encodedAddress = URLEncoder.encode(address, "UTF-8");

            // Construct the Nominatim API request URL
            String apiUrl = nominatimBaseUrl + "search?q=" + encodedAddress + "&format=json";
            
            // Include the API key if required
            if (apiKey != null && !apiKey.isEmpty()) {
                apiUrl += "&apikey=" + apiKey;
            }

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());

            if (rootNode.isArray() && rootNode.size() > 0) {
                JsonNode firstResult = rootNode.get(0);
                double latitude = firstResult.get("lat").asDouble();
                double longitude = firstResult.get("lon").asDouble();
                coordinates.put("latitude", latitude);
                coordinates.put("longitude", longitude);
            } else {
                System.out.println("No results found for the given address.");
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return coordinates;
    }
}
