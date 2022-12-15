package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistantAccount implements AccountDAO {

    private final DBHandler db;

    public PersistantAccount(Context context) {
        db = DBHandler.getInstance(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return db.getAccountNumbers();
    }

    @Override
    public List<Account> getAccountsList() {
        return db.getAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return db.getAcc(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        db.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        db.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        db.updateBalance(accountNo, expenseType, amount);
    }
}
