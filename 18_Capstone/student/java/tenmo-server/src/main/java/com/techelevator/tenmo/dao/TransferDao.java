package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;

public interface TransferDao {

    Transfer addTransfer(Transfer transfer);

    Transfer getTransfer(int transferId);

    void updateFrom(Transfer newTransfer, BigDecimal amount);

    void updateTo(Transfer newTransfer, BigDecimal amount);

}
