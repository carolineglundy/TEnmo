package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;

import com.techelevator.tenmo.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.math.BigDecimal;


public class TEBucksService {
    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();

    public TEBucksService(String url) { this.baseUrl = url; }

    public BigDecimal getBalance(String token){
        HttpEntity<?> entity = getHttpEntity(token);
        BigDecimal balance = BigDecimal.ZERO;
        try{
            ResponseEntity<BigDecimal> response = restTemplate.exchange(baseUrl+"balance", HttpMethod.GET,entity,BigDecimal.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("Error getting balance: " + e.getMessage());
        }
        return balance;
    }

    public User[] listUsers(String token) {
        User[] users = null;
        HttpEntity<?> entity = getHttpEntity(token);
       try {
           ResponseEntity<User[]> response = restTemplate.exchange(baseUrl + "list", HttpMethod.GET, entity, User[].class);
           users = response.getBody();
       } catch (RestClientResponseException | ResourceAccessException e) {
           System.out.println("Error getting User List: " + e.getMessage());
       }  return users;
    }

    public Transfer sendTransfer(String token, Transfer newTransfer) {
        Transfer returnedTransfer = null;
        HttpEntity<?> entity = getHttpEntityTransfer(token, newTransfer);
        try {
            returnedTransfer = restTemplate.postForObject(baseUrl + "transfer", entity, Transfer.class);
        } catch (RestClientResponseException e) {
            System.out.println(("Error returned from server: "+e.getRawStatusCode()+": "+e.getStatusText()));
        } catch (ResourceAccessException e) {
            System.out.println("Error: Couldn't reach server.");
        }
        return returnedTransfer;
    }

    public Transfer[] viewTransfers(String token) {
        Transfer[] transfers = null;
        HttpEntity<?> entity = getHttpEntity(token);
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl +"transfer/list", HttpMethod.GET, entity, Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("Error getting Transfer List: " + e.getMessage());
        }  return transfers;
    }

    public Transfer viewTransferById(String token, int transferId) {
        Transfer transfer = null;
        HttpEntity<?> entity = getHttpEntity(token);
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl +"transfer/"+transferId, HttpMethod.GET, entity, Transfer.class);
            transfer  = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("Error getting Transfer Id: " + e.getMessage());
        }  return transfer;

    }

    private HttpEntity<?> getHttpEntity(String token) {
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity(header);
        return entity;
    }

    private HttpEntity<Transfer> getHttpEntityTransfer(String token, Transfer transfer) {
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(token);
        HttpEntity<Transfer> entity = new HttpEntity(transfer, header);
        return entity;
    }

//    private HttpEntity<?> makeTransferEntity(Transfer transfer) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(authToken);
//        return new HttpEntity<>(transfer, headers);
//    }
}
