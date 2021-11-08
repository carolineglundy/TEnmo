package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.TransferDetailsNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TEBucksController {

    private static final int TRANSFER_STATUS_PENDING = 1;
    private static final int TRANSFER_STATUS_APPROVED = 2;
    private static final int TRANSFER_STATUS_REJECTED = 3;

    private static final int TRANSFER_TYPE_REQUEST = 1;
    private static final int TRANSFER_TYPE_SEND = 2;

    private UserDao userDao;
    private AccountDao accountDao;
    private TransferDao transferDao;

    public TEBucksController(UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    /**
     * Return User Account Balance
     *
     * @param principal - user who is logged in
     * @return current balance of the logged in user
     */
    @RequestMapping(path = "balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        accountDao.getAccount(userId);
        BigDecimal currentBalance = accountDao.getAccount(userId).getBalance();
        return currentBalance;
    }

    /**
     * Return All Users
     *
     *
     * @return a list of users in the system
     */
    @RequestMapping(path = "list", method = RequestMethod.GET)
    public List<User> getUsers(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        return userDao.findAll(userId); }



    /**
     * Send a Transfer to Another User and Updating Both Account Balances
     *
     * @param principal - the logged in user
     * @param transfer - the transfer to send
     * @return the transfer object that was created
     */
    @NotBlank()
    @RequestMapping(path = "transfer/send", method = RequestMethod.POST)
    public Transfer sendTransfer(Principal principal, @Valid @RequestBody Transfer transfer) throws Exception {

        setTransferDetails(principal, transfer, TRANSFER_STATUS_APPROVED, TRANSFER_TYPE_SEND);
        Transfer newTransfer = transferDao.sendTransfer(transfer);

        return newTransfer;
    }


    @NotBlank
    @RequestMapping(path = "transfer/request", method = RequestMethod.POST)
    public Transfer requestTransfer(Principal principal, @Valid @RequestBody Transfer transfer) throws Exception {

        setTransferDetails(principal, transfer, TRANSFER_STATUS_PENDING, TRANSFER_TYPE_REQUEST);
        Transfer newTransfer = transferDao.requestTransfer(transfer);

        return newTransfer;
}
    @RequestMapping(path = "transfer/status/{choiceId}", method = RequestMethod.PUT)
    public boolean updateTransfer(Principal principal, @Valid @RequestBody Transfer transfer, @PathVariable int choiceId) throws Exception {
        boolean result = false;
        //Transfer updatedTransfer = null;
//        Account userAccount = accountDao.getAccount(userDao.findIdByUsername(principal.getName()));
//        Account receiverAccount = accountDao.getAccount(newTransfer.getAccountTo());

//        transfer.setAccountTo(receiverAccount.getAccountId());
//        transfer.setAccountFrom(userAccount.getAccountId());

        if (choiceId == 1) {
            transfer.setTransferStatusId(TRANSFER_STATUS_APPROVED);
            //newTransfer.setTransferStatusId(TRANSFER_STATUS_APPROVED);
            result = transferDao.updateTransferAndBalances(transferDao.getTransferWithUsername(transfer.getTransferId()),TRANSFER_STATUS_APPROVED);
        }
        else if (choiceId == 2) {
            transfer.setTransferStatusId(TRANSFER_STATUS_REJECTED);
            //newTransfer.setTransferStatusId(TRANSFER_STATUS_REJECTED);
           result = transferDao.updateTransfer(transferDao.getTransferWithUsername(transfer.getTransferId()), TRANSFER_STATUS_REJECTED);
        }

        return result;

    }


    /**
     * Return all Transfers That Have Been Sent or Received for the Logged In User
     *
     * @param principal - the logged in user
     * @return - all the transfer details for the logged in user
     */
    @RequestMapping(path = "transfer/list", method = RequestMethod.GET)
    public List<Transfer> listApprovedTransfers(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        int accountId = accountDao.getAccount(userId).getAccountId();

        return transferDao.transferList(accountId);


    }

    @RequestMapping(path = "transfer/pending/list", method = RequestMethod.GET)
    public List<Transfer> listPendingTransfers(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        int accountId = accountDao.getAccount(userId).getAccountId();

        return transferDao.pendingTransfersList(accountId, TRANSFER_STATUS_PENDING);
    }

    /**
     * Get a Specific Transfer by Id
     *
     * @param transferId - the transfer id to return
     * @return - the transfer details with that id
     */
    @RequestMapping(path = "transfer/{transferId}", method = RequestMethod.GET)
    public Transfer getTransfersById(@PathVariable int transferId) throws TransferDetailsNotFoundException {
        Transfer transfer = transferDao.getTransferWithUsername(transferId);
        if (transfer == null) {
            throw new TransferDetailsNotFoundException();
        } else {
            return transfer;
        }
    }

    //method to set Transfer Details
    private void setTransferDetails(Principal principal, Transfer transfer, int transferStatus, int transferType) {
        Account userAccount = accountDao.getAccount(userDao.findIdByUsername(principal.getName()));
        Account receiverAccount = accountDao.getAccount(transfer.getAccountTo());
        if (transferStatus == TRANSFER_STATUS_APPROVED && transferType == TRANSFER_TYPE_SEND)  {

            transfer.setTransferStatusId(TRANSFER_STATUS_APPROVED);
            transfer.setTransferTypeId(TRANSFER_TYPE_SEND);
            transfer.setAccountTo(receiverAccount.getAccountId());
            transfer.setAccountFrom(userAccount.getAccountId());

        } else if (transferStatus == TRANSFER_STATUS_PENDING && transferType == TRANSFER_TYPE_REQUEST){
            transfer.setTransferStatusId(TRANSFER_STATUS_PENDING);
            transfer.setTransferTypeId(TRANSFER_TYPE_REQUEST);
            receiverAccount = accountDao.getAccount(transfer.getAccountFrom());
            transfer.setAccountTo(userAccount.getAccountId());
            transfer.setAccountFrom(receiverAccount.getAccountId());
        } if (userAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Your account does not exist");
        }
        if (receiverAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Your friend's account: " + receiverAccount.getAccountId() + " does not exist");
        }
        BigDecimal amount = transfer.getAmount();
        int userId = userDao.findIdByUsername(principal.getName());

    }



}
