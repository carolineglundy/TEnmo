package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TEBucksController {

    private UserDao userDao;
    private AccountDao accountDao;
    private TransferDao transferDao;

    public TEBucksController(UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }




//As an authenticated user of the system, I need to be able to see my Account Balance.
    //might need principal
    @RequestMapping(path = "balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        accountDao.getAccount(userId);
        BigDecimal currentBalance = accountDao.getAccount(userId).getBalance();
        return currentBalance;
    }

    //1. I should be able to choose from a list of users to send TE Bucks to.
    @RequestMapping(path = "list", method = RequestMethod.GET)
    public List<User> getUsers() { return userDao.findAll(); }

//2. A transfer includes the User IDs of the from and to users and the amount of TE Bucks.
    @NotBlank()
    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public Transfer addTransfer(Principal principal, @Valid @RequestBody Transfer transfer) {
        transfer.setTransferStatusId(2);
        transfer.setTransferTypeId(2);

        int accountTo = transfer.getAccountTo();
        BigDecimal amount = transfer.getAmount();
        int userId = userDao.findIdByUsername(principal.getName());

        transfer.setAccountFrom(accountDao.getAccount(userId).getAccountId());
        Transfer newTransfer = transferDao.sendTransfer(transfer);



        return newTransfer;
    }
        //3-6 The receiver's account balance is increased by the amount of the transfer.
        //The sender's account balance is decreased by the amount of the transfer.
        //I can't send more TE Bucks than I have in my account.

    //As an authenticated user of the system, I need to be able to see transfers I have sent or received.
    //As an authenticated user of the system, I need to be able to retrieve the details of any transfer based upon the transfer ID.

    @RequestMapping(path = "transferlist", method = RequestMethod.GET)
    public List<Transfer> getTransfers(Principal principal) { return transferDao.findAll();
        int userId = userDao.findIdByUsername(principal.getName());
        int transferId
    }


//    @RequestMapping(path = "transfer/from", method = RequestMethod.PUT)
//    public Account update(Principal principal, @Valid @RequestBody Account account) {
//        int userId = userDao.findIdByUsername(principal.getName());
//        //return accountDao.update(account, userId);
//
//    }
    // if amount > account balance






//    public int transferTo(BankAccount destinationAccount, int transferAmount) {
//        withdraw(transferAmount);
//        destinationAccount.deposit(transferAmount); //syntax: object.method
//        return balance;
//    }

}
