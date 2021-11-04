package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class Transfer {

    private int transferId;
    private int transferTypeId;
    private int transferStatusId;
    private int accountFrom;
    @Min( value = 1, message = "The field 'accountTo' is required.")
    private int accountTo;
    @Min( value = 1, message = "The field 'amount' is required.")
    private BigDecimal amount;

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

    public Transfer() {}

    public Transfer(int transferId, int transferTypeId, int transferStatusId, int accountFrom, int accountTo, BigDecimal amount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }
    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", transferTypeId='" + transferTypeId + '\'' +
                ", transferStatusId=" + transferStatusId +
                ", accountFrom=" + accountFrom +
                ", accountTo=" + accountTo +
                ", amount=" + amount +
                '}';

    }

}
