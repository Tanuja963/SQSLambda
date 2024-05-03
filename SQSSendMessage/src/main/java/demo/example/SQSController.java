package demo.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SQSController {
	
	@Autowired
	QueryTest test;
	@GetMapping("/sendQuery")
    public String sendQuery() {
		test.sendQuery();
        return "Employee details sent successfully through query!";
    }
	
	 @GetMapping("/sendJsonData")
	    public String sendEmployeeDetailsToQueue() {
		 test.sendEmployeeDetailsToQueue();
	        return "Employee details sent successfully!";
	    }
	 
	 
	 @GetMapping("/senddatatosqs")
	    public String sendBatch() {
	        test.send(); 
	        return "Employee details sent successfully!";
	    }

}
