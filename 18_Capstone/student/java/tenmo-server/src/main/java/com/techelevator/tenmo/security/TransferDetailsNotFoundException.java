package com.techelevator.tenmo.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.security.core.Exception;


@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "Transfer Details not found.")
public class TransferDetailsNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransferDetailsNotFoundException() {
        super("Transfer Details not found.");
    }
}


