package com.example.JyotiKumari_22ucs096;  

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.util.Map;

@Component
public class StartupRunner implements CommandLineRunner {

    private final WebClient webClient = WebClient.builder().build();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("App started - running webhook flow");

        Map<String, Object> response = webClient.post()
            .uri("https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "name", "John Doe",
                "regNo", "REG12347",
                "email", "john@example.com"
            ))
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        String webhook = (String) response.get("webhook");
        String accessToken = (String) response.get("accessToken");

        System.out.println("Webhook URL: " + webhook);
        System.out.println("Access Token: " + accessToken);

        String finalQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, "
                  + "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT "
                  + "FROM EMPLOYEE e1 "
                  + "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID "
                  + "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e1.DOB "
                  + "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME "
                  + "ORDER BY e1.EMP_ID DESC";


        Map<String, Object> submitResponse = webClient.post()
            .uri(webhook)
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("finalQuery", finalQuery))
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        System.out.println("Submission response: " + submitResponse);
    }
}

