package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistantTransaction implements TransactionDAO {
    DBHandler db;

    public PersistantTransaction(Context context) {
        db = DBHandler.getInstance(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){
        db.addLog(date, accountNo, expenseType, amount);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        return db.getLog();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactions = db.getLog();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
