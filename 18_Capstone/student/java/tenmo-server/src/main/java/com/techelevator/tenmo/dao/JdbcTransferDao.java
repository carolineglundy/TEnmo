package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
    // we need a big method that calls addTransfer and updateAccount
    @Override
    public Transfer addTransfer(Transfer newTransfer) {
        String sql ="INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)  " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";
        int newId = jdbcTemplate.queryForObject(sql, Integer.class, newTransfer.getTransferTypeId(), newTransfer.getTransferStatusId(),
                newTransfer.getAccountFrom(), newTransfer.getAccountTo(), newTransfer.getAmount());

        return getTransfer(newId);
    }


    public void updateFrom(Transfer newTransfer, BigDecimal amount) {
        String sql = "UPDATE accounts " +
                "SET balance = balance - ? " +
                "WHERE account_id = ? ; ";
        jdbcTemplate.update(sql, amount, newTransfer.getAccountFrom());

    }


    public void updateTo(Transfer newTransfer, BigDecimal amount) {
        String sql = "UPDATE accounts " +
                "SET balance = balance + ?  " +
                "WHERE account_id = ? ;";
        jdbcTemplate.update(sql, amount, newTransfer.getAccountTo());

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


// if
}
