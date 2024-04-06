import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class EarthquakeDataProcessor {

    public static void main(String[] args) throws IOException, URISyntaxException, JSONException {
        // Fetch earthquake data as JSON string
        String jsonData = GetData.getData();

        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray features = jsonObject.getJSONArray("features");

        // Map to store aggregated earthquake data by date
        Map<LocalDate, EarthquakeData> earthquakeDataMap = new HashMap<>();

        // Process each feature in the JSON array
        IntStream.range(0, features.length()).forEach(i -> {
            try {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                double magnitude = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = properties.getLong("time");

                // Convert epoch time to LocalDate
                LocalDate date = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();

                // Add or update earthquake data in the map
                earthquakeDataMap.computeIfAbsent(date, k -> new EarthquakeData())
                        .addMagnitude(magnitude, place);
            } catch (JSONException e) {
                // Log parsing errors but continue processing
                System.err.println("Error processing JSON data at index " + i + ": " + e.getMessage());
            }
        });

        // Write aggregated earthquake data to a file
        String file = "earthquake_data_" + LocalDate.now() + ".txt";
        try (FileWriter fileWriter = new FileWriter(file)) {
            earthquakeDataMap.forEach((date, earthquakeData) -> {
                double averageMagnitude = earthquakeData.getAverageMagnitude();
                String maxMagnitudeLocation = earthquakeData.getMaxMagnitudeLocation();
                String output = String.format("%s %.2f, location: %s%n", date, averageMagnitude, maxMagnitudeLocation);
                try {
                    fileWriter.write(output);
                } catch (IOException e) {
                    // Handle file writing errors
                    System.err.println("An error occurred while writing to the file: " + e.getMessage());
                }
                System.out.print(output);
            });
        }
    }

    private static class EarthquakeData {
        private double magnitudeSum;
        private int count;
        private double maxMagnitude;
        private String maxMagnitudeLocation;

        // Adds a new earthquake's magnitude and updates stats
        public void addMagnitude(double magnitude, String location) {
            magnitudeSum += magnitude;
            count++;
            // Update max magnitude and its location if this earthquake is stronger
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
                maxMagnitudeLocation = location;
            }
        }

        public double getAverageMagnitude() {
            return magnitudeSum / count;
        }

        public String getMaxMagnitudeLocation() {
            return maxMagnitudeLocation;
        }
    }
}
