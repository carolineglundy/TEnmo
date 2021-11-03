package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TEBucksController {

    private UserDao userDao;
    private AccountDao accountDao;
    private TransferDao transferDao;

    public TEBucksController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
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
    @RequestMapping(path = "transfer/", method = RequestMethod.POST)
    public Transfer addTransfer(Principal principal,@Valid @RequestBody Transfer transfer) {

        int accountTo = transfer.getAccountTo();
        BigDecimal amount = transfer.getAmount();

        int userId = userDao.findIdByUsername(principal.getName());

        transfer.setAccountFrom(accountDao.getAccount(userId).getAccountId());

        return transferDao.addTransfer(transfer);
    }








//    public int transferTo(BankAccount destinationAccount, int transferAmount) {
//        withdraw(transferAmount);
//        destinationAccount.deposit(transferAmount); //syntax: object.method
//        return balance;
//    }

}
