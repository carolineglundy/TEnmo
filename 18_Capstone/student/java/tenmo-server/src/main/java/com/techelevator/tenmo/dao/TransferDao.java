package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDao {

    Transfer addTransfer(Transfer transfer);

    Transfer getTransfer(int transferId);
}
