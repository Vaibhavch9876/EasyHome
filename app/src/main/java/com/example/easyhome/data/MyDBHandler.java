package com.example.easyhome.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.easyhome.model.Transaction;
import com.example.easyhome.params.Params;

import java.util.ArrayList;
import java.util.List;


public class MyDBHandler extends SQLiteOpenHelper {

    public MyDBHandler(Context context) {
        super(context, Params.DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String create_budget = "CREATE TABLE IF NOT EXISTS " + Params.TRANSACTION_TABLE_NAME + "(" +
                Params.TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                Params.TRANSACTION_TYPE + " VARCHAR NOT NULL, " +
                Params.TRANSACTION_CATEGORY + " VARCHAR NOT NULL, " +
                Params.TRANSACTION_DESCRIPTION + " VARCHAR NOT NULL, " +
                Params.TRANSACTION_AMOUNT + " DECIMAL(12, 2) NOT NULL, " +
                Params.TRANSACTION_DATE + " DATE NOT NULL, " +
                Params.TRANSACTION_SOURCE + " VARCHAR, " +
                Params.TRANSACTION_PAYEE + " VARCHAR, " +
                Params.TRANSACTION_COMMENT + " VARCHAR " + ")";

        Log.e("database_query", create_budget);

        db.execSQL(create_budget);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addTransaction(Transaction transaction) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.TRANSACTION_TYPE, transaction.getTransactionType());
        values.put(Params.TRANSACTION_CATEGORY, transaction.getTransactionCategory());
        values.put(Params.TRANSACTION_DESCRIPTION, transaction.getTransactionDescription());
        values.put(Params.TRANSACTION_AMOUNT, transaction.getTransactionAmount());
        values.put(Params.TRANSACTION_DATE, transaction.getTransactionDate());
        values.put(Params.TRANSACTION_SOURCE, transaction.getTransactionSource());
        values.put(Params.TRANSACTION_PAYEE, transaction.getTransactionPayee());
        values.put(Params.TRANSACTION_COMMENT, transaction.getTransactionComment());

        db.insert(Params.TRANSACTION_TABLE_NAME, null, values);

        Log.e("Insertion", "Succesful");
        db.close();
    }

    public List<Transaction> getIncomeTransactions() {

        List<Transaction> transactions = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectIncomeTransaction = "SELECT * FROM " + Params.TRANSACTION_TABLE_NAME + " WHERE "
                + Params.TRANSACTION_TYPE + " = \"income\" ORDER BY " + Params.TRANSACTION_DATE + "," + Params.TRANSACTION_ID +  " DESC;";

        Cursor cursor = db.rawQuery(selectIncomeTransaction, null);

        if (cursor.moveToFirst()) {
            do {

                Transaction transaction = new Transaction();

                transaction.setTransactionId(cursor.getInt(0));
                transaction.setTransactionType(cursor.getString(1));
                transaction.setTransactionCategory(cursor.getString(2));
                transaction.setTransactionDescription(cursor.getString(3));
                transaction.setTransactionAmount(cursor.getDouble(4));
                transaction.setTransactionDate(cursor.getString(5));
                transaction.setTransactionSource(cursor.getString(6));
                transaction.setTransactionPayee(cursor.getString(7));
                transaction.setTransactionComment(cursor.getString(8));

                transactions.add(transaction);

            } while (cursor.moveToNext());
        }

        db.close();

        return transactions;
    }

    public List<Transaction> getExpenseTransactions() {

        List<Transaction> transactions = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectIncomeTransaction = "SELECT * FROM " + Params.TRANSACTION_TABLE_NAME + " WHERE "
                + Params.TRANSACTION_TYPE + " = \"expense\" ORDER BY " + Params.TRANSACTION_DATE + "," + Params.TRANSACTION_ID + " DESC;";

        Cursor cursor = db.rawQuery(selectIncomeTransaction, null);

        if (cursor.moveToFirst()) {
            do {

                Transaction transaction = new Transaction();

                transaction.setTransactionId(cursor.getInt(0));
                transaction.setTransactionType(cursor.getString(1));
                transaction.setTransactionCategory(cursor.getString(2));
                transaction.setTransactionDescription(cursor.getString(3));
                transaction.setTransactionAmount(cursor.getDouble(4));
                transaction.setTransactionDate(cursor.getString(5));
                transaction.setTransactionSource(cursor.getString(6));
                transaction.setTransactionPayee(cursor.getString(7));
                transaction.setTransactionComment(cursor.getString(8));

                transactions.add(transaction);

            } while (cursor.moveToNext());
        }

        db.close();

        return transactions;
    }

    public List<Transaction> getAllTransactions() {

        List<Transaction> transactions = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectIncomeTransaction = "SELECT * FROM " + Params.TRANSACTION_TABLE_NAME
                + " ORDER BY " + Params.TRANSACTION_DATE + "," + Params.TRANSACTION_ID + " DESC;";

        Cursor cursor = db.rawQuery(selectIncomeTransaction, null);

        if (cursor.moveToFirst()) {
            do {

                Transaction transaction = new Transaction();

                transaction.setTransactionId(cursor.getInt(0));
                transaction.setTransactionType(cursor.getString(1));
                transaction.setTransactionCategory(cursor.getString(2));
                transaction.setTransactionDescription(cursor.getString(3));
                transaction.setTransactionAmount(cursor.getDouble(4));
                transaction.setTransactionDate(cursor.getString(5));
                transaction.setTransactionSource(cursor.getString(6));
                transaction.setTransactionPayee(cursor.getString(7));
                transaction.setTransactionComment(cursor.getString(8));

                transactions.add(transaction);

            } while (cursor.moveToNext());
        }

        db.close();

        return transactions;
    }

    public void deleteTransaction(Transaction transaction) {

        SQLiteDatabase db = this.getWritableDatabase();

        String deleteTransaction = "DELETE FROM " + Params.TRANSACTION_TABLE_NAME + " WHERE " + Params.TRANSACTION_ID + " = " + transaction.getTransactionId() + ";";

        Log.e("Delete", deleteTransaction);

        db.execSQL(deleteTransaction);

        db.close();

    }

    public void updateTransaction(Transaction transaction, int oldId) {

        transaction.setTransactionId(oldId);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.TRANSACTION_TYPE, transaction.getTransactionType());
        values.put(Params.TRANSACTION_CATEGORY, transaction.getTransactionCategory());
        values.put(Params.TRANSACTION_DESCRIPTION, transaction.getTransactionDescription());
        values.put(Params.TRANSACTION_AMOUNT, transaction.getTransactionAmount());
        values.put(Params.TRANSACTION_DATE, transaction.getTransactionDate());
        values.put(Params.TRANSACTION_SOURCE, transaction.getTransactionSource());
        values.put(Params.TRANSACTION_PAYEE, transaction.getTransactionPayee());
        values.put(Params.TRANSACTION_COMMENT, transaction.getTransactionComment());

        db.update(Params.TRANSACTION_TABLE_NAME, values, Params.TRANSACTION_ID + " = " + oldId, null);

        db.close();
    }
}

