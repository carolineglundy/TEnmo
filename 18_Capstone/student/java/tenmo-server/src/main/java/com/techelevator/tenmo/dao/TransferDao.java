package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    Transfer addTransfer(Transfer transfer) throws Exception;

    Transfer getTransfer(int transferId);

    Transfer sendTransfer(Transfer transfer) throws Exception;

    List<Transfer> transferList(int transferId);

    boolean updateTransfer(Transfer updatedTransfer, int TransferStatus);
    boolean updateTransferAndBalances(Transfer transfer, int TransferStatus) throws ResponseStatusException;
    public List<Transfer> pendingTransfersList(int accountId, int transferStatus);

    int updateFrom(Transfer newTransfer, BigDecimal amount);

    void updateTo(Transfer newTransfer, BigDecimal amount);

    Transfer getTransferWithUsername(int transferId);


    Transfer requestTransfer(Transfer transfer) throws Exception;
}
