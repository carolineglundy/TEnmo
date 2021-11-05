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

    private static final int TRANSFER_STATUS_APPROVED = 2;
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
    public List<User> getUsers() { return userDao.findAll(); }

    /**
     * Send a Transfer to Another User and Updating Both Account Balances
     *
     * @param principal - the logged in user
     * @param transfer - the transfer to send
     * @return the transfer object that was created
     */
    @NotBlank()
    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public Transfer addTransfer(Principal principal, @Valid @RequestBody Transfer transfer) throws Exception {
        transfer.setTransferStatusId(TRANSFER_STATUS_APPROVED);
        transfer.setTransferTypeId(TRANSFER_TYPE_SEND);

        Account fromAccount = accountDao.getAccount(userDao.findIdByUsername(principal.getName()));
            if (fromAccount == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Your account does not exist");
            }
        Account toAccount = accountDao.getAccount(transfer.getAccountTo());
        if (toAccount == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Your friend's account: "+toAccount.getAccountId()+" does not exist");
        }
        BigDecimal amount = transfer.getAmount();
        int userId = userDao.findIdByUsername(principal.getName());

        transfer.setAccountTo(toAccount.getAccountId());
        transfer.setAccountFrom(fromAccount.getAccountId());
        Transfer newTransfer = transferDao.sendTransfer(transfer);

        return newTransfer;
    }

    /**
     * Return all Transfers That Have Been Sent or Received for the Logged In User
     *
     * @param principal - the logged in user
     * @return - all the transfer details for the logged in user
     */
    @RequestMapping(path = "transfer/list", method = RequestMethod.GET)
    public List<Transfer> listTransfers(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        int accountId = accountDao.getAccount(userId).getAccountId();

        return transferDao.transferList(accountId);
    }

    /**
     * Get a Specific Transfer by Id
     *
     * @param transferId - the transfer id to return
     * @return - the transfer details with that id
     */
    @RequestMapping(path = "transfer/{transferId}", method = RequestMethod.GET)
    public Transfer getTransfersById(@PathVariable int transferId) throws TransferDetailsNotFoundException {
        Transfer transfer = transferDao.getTransfer(transferId);
        if (transfer == null) {
            throw new TransferDetailsNotFoundException();
        } else {
            return transfer;
        }
    }


}
