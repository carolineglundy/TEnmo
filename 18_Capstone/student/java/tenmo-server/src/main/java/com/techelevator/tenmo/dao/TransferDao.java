package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    Transfer addTransfer(Transfer transfer) throws Exception;

    Transfer getTransfer(int transferId);

    Transfer sendTransfer(Transfer transfer) throws Exception;

    List<Transfer> transferList(int transferId);

    int updateFrom(Transfer newTransfer, BigDecimal amount);

    void updateTo(Transfer newTransfer, BigDecimal amount);

    Transfer getTransferWithUsername(int transferId);



}
