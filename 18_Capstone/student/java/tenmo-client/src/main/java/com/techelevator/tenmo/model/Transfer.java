package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int transferId;
    private int transferTypeId;
    private int transferStatusId;
    private int accountFrom;
    private int accountTo;
    private BigDecimal amount;
    private String accountFromUsername;
    private String accountToUsername;
    private String transferType;
    private String transferStatus;

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }


    public String getAccountFromUsername() {
        return accountFromUsername;
    }

    public void setAccountFromUsername(String accountFromUsername) {
        this.accountFromUsername = accountFromUsername;
    }

    public String getAccountToUsername() {
        return accountToUsername;
    }

    public void setAccountToUsername(String accountToUsername) {
        this.accountToUsername = accountToUsername;
    }


    public int getTransferId() { return transferId; }
    public void setTransferId(int transferId) { this.transferId = transferId; }

    public int getTransferTypeId() { return transferTypeId; }
    public void setTransferTypeId(int transferTypeId) { this.transferTypeId = transferTypeId; }

    public int getTransferStatusId() { return transferStatusId; }
    public void setTransferStatusId(int transferStatusId) { this.transferStatusId = transferStatusId; }

    public int getAccountFrom() { return accountFrom; }
    public void setAccountFrom(int accountFrom) { this.accountFrom = accountFrom; }

    public int getAccountTo() { return accountTo; }
    public void setAccountTo(int accountTo) { this.accountTo = accountTo; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }


    @Override
    public String toString() {
        return
                "\n"+"Transfer Id:   " + transferId +
                "\n"+"From:   " + accountFromUsername +
                "\n"+"To:   " + accountToUsername +
                "\n"+"Type:   " + transferType +
                "\n"+"Status:   " + transferStatus +
                "\n"+"amount:   " + amount;

    }
}
