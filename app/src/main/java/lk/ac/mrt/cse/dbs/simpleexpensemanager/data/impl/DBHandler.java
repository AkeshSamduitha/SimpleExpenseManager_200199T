package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "200199T";
    private static final int DB_VERSION = 1;

    private static final String Accounts_Table = "Acoounts";
    private static final String ACC_Number_COL = "accountid";
    private static final String BANK_COL = "bankname";
    private static final String NAME_COL = "name";
    private static final String BALANCE_COL = "balance";

    private static final String LOGS_TABLE = "Transactions";
    private static final String LOG_ID_COL = "id";
    private static final String TIME_COL = "time";
    private static final String TYPE_COL = "type";
    private static final String AMOUNT_COL = "amount";

    // creating a constructor for our database handler.
    private static DBHandler Handler = null;

    private DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHandler getInstance(Context context){
        if (Handler == null) {
            Handler = new DBHandler(context);
        }
        return Handler;
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {

        String query1 = "CREATE TABLE " + Accounts_Table + " ("
                + ACC_Number_COL + " TEXT PRIMARY KEY, "
                + NAME_COL + " TEXT NOT NULL,"
                + BALANCE_COL + " REAL CHECK(" + BALANCE_COL+ " > 0),"
                + BANK_COL + "TEXT NOT NULL)" ;

        String query2 = "CREATE TABLE " + LOGS_TABLE + " ("
                + LOG_ID_COL + " INTGER PRIMARY KEY AUTOINCREMENT, "
                + TIME_COL + " TEXT NOT NULL,"
                + ACC_Number_COL  + "TEXT NOT NULL,"
                + TYPE_COL + " TEXT NOT NULL,"
                + AMOUNT_COL + "REAL NOT NULL,"
                + "FOREIGN KEY(" + ACC_Number_COL + ") REFERENCES " + Accounts_Table + "(" + ACC_Number_COL + "))";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query1);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + Accounts_Table);
        db.execSQL("DROP TABLE IF EXISTS " + LOGS_TABLE);
        onCreate(db);
    }

    public ArrayList<String> getAccountNumbers(){
        Cursor cursorAcc_Nos =  this.getReadableDatabase().rawQuery(" SELECT " + ACC_Number_COL + " FROM " + Accounts_Table, null);
        ArrayList<String> Account_Nos = new ArrayList<>();

        if (cursorAcc_Nos.moveToFirst()) {
            do {
                Account_Nos.add(cursorAcc_Nos.getString(0));
            } while (cursorAcc_Nos.moveToNext());
        }
        cursorAcc_Nos.close();
        return Account_Nos;
    }

    public ArrayList<Account> getAccounts() {
        Cursor cursorAcc = this.getReadableDatabase().rawQuery(" SELECT * FROM " + Accounts_Table, null);
        ArrayList<Account> AccArrayList = new ArrayList<>();

        if (cursorAcc.moveToFirst()) {
            do {
                AccArrayList.add(new Account(cursorAcc.getString(1),
                        cursorAcc.getString(3),
                        cursorAcc.getString(2),
                        cursorAcc.getDouble(4)));
            } while (cursorAcc.moveToNext());
        }
        cursorAcc.close();
        return AccArrayList;
    }

    public Account getAcc(String accountNo) throws InvalidAccountException{
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorAcc = db.rawQuery(" SELECT * FROM " + Accounts_Table + " Where " + ACC_Number_COL + " = ? ", new String[] {accountNo});
        ArrayList<Account> AccArrayList = new ArrayList<>();

        if (cursorAcc.moveToFirst()) {
            do {
                AccArrayList.add(new Account(
                        cursorAcc.getString(0),
                        cursorAcc.getString(2),
                        cursorAcc.getString(1),
                        cursorAcc.getDouble(3)));
            } while (cursorAcc.moveToNext());

            cursorAcc.close();
            return AccArrayList.get(0);
        }
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    public void addAccount(Account account) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ACC_Number_COL, account.getAccountNo());
        values.put(NAME_COL, account.getAccountHolderName());
        values.put(BALANCE_COL, account.getBalance());
        values.put(BANK_COL, account.getBankName());

        db.insert(Accounts_Table, null, values);

        db.close();
    }

    public void removeAccount(String accountNo){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Accounts_Table, ACC_Number_COL +" =? ", new String[]{accountNo});
        db.close();
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        // calling a method to get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Account account = getAcc(accountNo);

        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }

        values.put(ACC_Number_COL, account.getAccountNo());
        values.put(NAME_COL, account.getAccountHolderName());
        values.put(BANK_COL, account.getBankName());
        values.put(AMOUNT_COL, account.getBalance());

        db.update(Accounts_Table, values, ACC_Number_COL + " =? ", new String[]{accountNo});
        db.close();
    }

    public void addLog(Date date, String accountNo, ExpenseType expenseType, double amount){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TIME_COL, date.toString());
        values.put(ACC_Number_COL, accountNo);
        values.put(TYPE_COL, expenseType.toString());
        values.put(AMOUNT_COL, amount);

        db.insert(LOGS_TABLE, null, values);

        db.close();
    }

    public List<Transaction> getLog() throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(" SELECT * FROM " + LOGS_TABLE, null);

        List<Transaction> AccArrayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                ExpenseType T;
                @SuppressLint("SimpleDateFormat") Date date = new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(1));

                if (Objects.equals(cursor.getString(3), "EXPENSE")){T = ExpenseType.EXPENSE;}
                else{T = ExpenseType.INCOME;}
                AccArrayList.add(new Transaction(date,
                        cursor.getString(2),
                        T,
                        cursor.getDouble(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return AccArrayList;
    }
}