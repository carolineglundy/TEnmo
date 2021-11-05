package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public Transfer getTransfer(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfers " +
                "WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public Transfer getTransferWithUsername(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, amount, u1.username AS user_from, u2.username AS user_to " +
                " FROM transfers " +
                    "INNER JOIN accounts a1 ON account_from = a1.account_id  " +
                    "INNER JOIN users u1 ON a1.user_id = u1.user_id " +
                    "INNER JOIN accounts a2 ON account_to = a2.account_id " +
                    "INNER JOIN users u2 ON a2.user_id = u2.user_id " +
                    "INNER JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " +
                    "INNER JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id " +
                    "WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            transfer = mapRowToTransferUserName(results);
        }
        return transfer;
    }

    // we need a big method that calls addTransfer and updateAccount
    @Transactional
    @Override
    public Transfer sendTransfer(Transfer transfer) throws ResponseStatusException {
        Transfer resultTransfer = null;
        int result = updateFrom(transfer, transfer.getAmount());

        if (result == 0) {
            transfer.setTransferStatusId(3);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Enough Money In Account.");
        } else {
          updateTo(transfer, transfer.getAmount());
          resultTransfer = addTransfer(transfer);
        }
        return resultTransfer;
    }

    @Override
    public Transfer addTransfer(Transfer newTransfer) {
        String sql ="INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)  " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";
        int newId = jdbcTemplate.queryForObject(sql, Integer.class, newTransfer.getTransferTypeId(), newTransfer.getTransferStatusId(),
                newTransfer.getAccountFrom(), newTransfer.getAccountTo(), newTransfer.getAmount());

        return getTransfer(newId);
    }

    @Override
    public int updateFrom(Transfer newTransfer, BigDecimal amount) {
        String sql = "UPDATE accounts " +
                "SET balance = balance - ? " +
                "WHERE account_id = ? AND balance >= ?; ";
       return jdbcTemplate.update(sql, amount, newTransfer.getAccountFrom(), amount); //returning an int gives us the rows and lets us know it was actually updated
        //if int doesn't equal zero, do updateTo
    }

    @Override
    public void updateTo(Transfer newTransfer, BigDecimal amount) {
        String sql = "UPDATE accounts " +
                "SET balance = balance + ?  " +
                "WHERE account_id = ? ;";
        jdbcTemplate.update(sql, amount, newTransfer.getAccountTo());

    }



    @Override
    public List<Transfer> transferList(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, amount, u1.username AS user_from, u2.username AS user_to " +
                        "FROM transfers " +
                            "INNER JOIN accounts a1 ON account_from = a1.account_id  " +
                            "INNER JOIN users u1 ON a1.user_id = u1.user_id " +
                            "INNER JOIN accounts a2 ON account_to = a2.account_id " +
                            "INNER JOIN users u2 ON a2.user_id = u2.user_id " +
                            "INNER JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " +
                            "INNER JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id " +
                            "WHERE  account_from = ? OR account_to = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
            while (results.next()) {
                Transfer transfer = mapRowToTransferUserName(results);
                transfers.add(transfer);
            }
            return transfers;
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {

        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setTransferTypeId(results.getInt("transfer_type_id"));
        transfer.setTransferStatusId(results.getInt("transfer_status_id"));
        transfer.setAccountFrom(results.getInt("account_from"));
        transfer.setAccountTo(results.getInt("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));

        return transfer;
    }

    private Transfer mapRowToTransferUserName(SqlRowSet results) {

        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setAccountFromUsername(results.getString("user_from"));
        transfer.setAccountToUsername(results.getString("user_to"));
        transfer.setTransferType(results.getString("transfer_type_desc"));
        transfer.setTransferStatus(results.getString("transfer_status_desc"));
        transfer.setAmount(results.getBigDecimal("amount"));

        return transfer;
    }




}
