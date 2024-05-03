package example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class ApiTesting {

	private static final String API_URL = "https://dev-hubble-api.miraclesoft.com/hubble-v6/sqs/addemployee";

	public String insertData(String id, String interaction_id, String notes) {
	    try {
	        String requestBody = String.format("{\"id\": %s, \"interaction_id\": %s, \"notes\": \"%s\"}", id, interaction_id, notes);
	        System.out.println("Request Body : " + requestBody);

	        URL url = new URL(API_URL);

	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setDoOutput(true);

	        connection.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));

	        int responseCode = connection.getResponseCode();

	        StringBuilder response = new StringBuilder();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
	                String line;
	                while ((line = reader.readLine()) != null) {
	                    response.append(line);
	                }
	            }
	        } else {
	            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
	                String line;
	                while ((line = reader.readLine()) != null) {
	                    response.append(line);
	                }
	            }
	        }

	        System.out.println("Response Code: " + responseCode);
	        System.out.println("Response Body: " + response.toString());

	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
	            boolean success = jsonResponse.get("success").getAsBoolean();
	            if (success) {
	                return "Data inserted successfully - ID: " + id + ", interaction_id: " + interaction_id + ", notes: " + notes;
	            } else {
	                String message = jsonResponse.get("message").getAsString();
	                return "Failed to insert data: " + message;
	            }
	        } else {
	            return "Failed to insert data. Response code: " + responseCode + ", Response: " + response.toString();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Failed to insert data";
	    }
	}


   
	public String handleRequest(SQSEvent event, Context context) {
        if (event == null || event.getRecords() == null || event.getRecords().isEmpty()) {
            return "No records found in the SQSEvent.";
        }

        StringBuilder logMessage = new StringBuilder();
        List<SQSEvent.SQSMessage> records = event.getRecords();
        for (SQSEvent.SQSMessage message : records) {
            String body = message.getBody();
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            String id = json.get("id").getAsString();
            String interaction_id = json.get("interaction_id").getAsString();
            String notes = json.get("notes").getAsString();

            String insertionResult = insertData(id, interaction_id, notes);
            logMessage.append(insertionResult).append("\n");
        }
        String result = logMessage.toString();
        context.getLogger().log(result);
        return result;
    }


    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ID:");
        String id = scanner.nextLine();
        
        System.out.println("Enter intrection_id:");
        String intrection_id = scanner.nextLine();
        
        System.out.println("Enter notes:");
        String notes = scanner.nextLine();
        ApiTesting rdsExample = new ApiTesting();
        rdsExample.insertData(id,intrection_id,notes);
        System.out.println("Data inserted successfully");
        scanner.close();
    }
}

