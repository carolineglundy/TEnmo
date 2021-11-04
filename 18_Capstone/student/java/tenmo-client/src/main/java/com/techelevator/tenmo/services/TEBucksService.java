package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
//import com.techelevator.util.BasicLogger;
import java.math.BigDecimal;
import java.net.http.HttpHeaders;

public class TEBucksService {
    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();

    public TEBucksService(String url) { this.baseUrl = url; }

    private BigDecimal getBalance (String token){
        HttpEntity<?> entity = getHttpEntity(token);
        BigDecimal balance = BigDecimal.ZERO;
        try{
            ResponseEntity<BigDecimal> response = restTemplate.exchange(baseUrl+"balance", HttpMethod.GET,entity,BigDecimal.class);
            balance = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balance;
    }

    private HttpEntity<?> getHttpEntity(String token) {
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity(header);
        return entity;
    }

    private HttpEntity<?> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(reservation, headers);
    }
}
