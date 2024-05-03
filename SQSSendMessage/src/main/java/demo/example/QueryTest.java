package demo.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@Service
public class QueryTest {
    
    @Autowired
    JdbcTemplate j;
    
    
    
    @SuppressWarnings("unchecked")
    public void sendEmployeeDetailsToQueue() {
        
    	AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/382557156916/lambda-test";

        JSONArray dataArray = new JSONArray();

        JSONObject data1 = new JSONObject();
        data1.put("id", 5);
        data1.put("interaction_id", 4);
        data1.put("notes", "Static");
        dataArray.add(data1);

        JSONObject data2 = new JSONObject();
        data2.put("id", 6);
        data2.put("interaction_id", 1);
        data2.put("notes", "done");
        dataArray.add(data2);

        JSONObject data3 = new JSONObject();
        data3.put("id", 7);
        data3.put("interaction_id", 2);
        data3.put("notes", "data");
        dataArray.add(data3);

        for (Object data : dataArray) {
            try {
                String jsonMessage = data.toString();
                SendMessageRequest sendMessageRequest = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(jsonMessage);
                sqs.sendMessage(sendMessageRequest);

                System.out.println("Message sent: " + jsonMessage);
            } catch (Exception e) {
                System.err.println("Error sending employee details: " + e.getMessage());
            }
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public void sendQuery() {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/382557156916/lambda-test";
  
        try {
            String query = "SELECT id,interaction_id,notes FROM employee where id=3 or id=5";
            List<Map<String, Object>> rows = j.queryForList(query);

            for (Map<String, Object> row : rows) {
                JSONObject data = new JSONObject();
                data.put("id", row.get("id"));
                data.put("interaction_id", row.get("interaction_id"));
                data.put("notes", row.get("notes"));

                String jsonMessage = data.toString();    
                SendMessageRequest sendMessageRequest = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(jsonMessage);
                sqs.sendMessage(sendMessageRequest);

                System.out.println("Message sent: " + jsonMessage);
            }
        } catch (Exception e) {
            System.err.println("Error sending employee details: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void send() {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/382557156916/lambda-test";

        try {
            List<String> notesList = new ArrayList<String>();
            notesList.add("SQS");
            notesList.add("lambda");
            notesList.add("testing");
            notesList.add("data");
            notesList.add("hello");
            notesList.add("ignore");
            notesList.add("test");
            notesList.add("Miracle");
            notesList.add("Software");
            notesList.add("Systems");

            for (int i = 1; i <= 300; i++) { 
//            	 String note = notesList.get(0);
                String note = notesList.get((i - 1) % notesList.size());

                JSONObject data = new JSONObject();
                data.put("interaction_id", i);
                data.put("notes", note);

                String jsonMessage = data.toString();
                SendMessageRequest sendMessageRequest = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(jsonMessage);
                sqs.sendMessage(sendMessageRequest);

                System.out.println("Message sent: " + jsonMessage);
            }

            System.out.println("All messages sent successfully.");
        } catch (Exception e) {
            System.err.println("Error sending employee details: " + e.getMessage());
        }
    }
}
