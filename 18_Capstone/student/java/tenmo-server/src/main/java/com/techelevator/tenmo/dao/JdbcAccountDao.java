package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public Account getAccount(int userId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance " +
                "FROM accounts " +
                "WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
          if (results.next()) {
              account = mapRowToAccount(results);
          }
        return account;
    }

    @Override
    public void update(Account account, int userId) {
        String sql = "UPDATE account " +
                "SET balance = balance - (SELECT amount FROM transfer WHERE account_from = ?)  " +
                "WHERE account_id = ?;";
        jdbcTemplate.update(sql, userId, account.getAccountId());

    }

    private Account mapRowToAccount(SqlRowSet results) {
        Account account = new Account();
        account.setAccountId(results.getInt("account_id"));
        account.setUserId(results.getInt("user_id"));
        account.setBalance(results.getBigDecimal("balance"));

        return account;
    }


}
