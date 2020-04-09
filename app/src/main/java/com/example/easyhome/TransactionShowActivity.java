package com.example.easyhome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.easyhome.model.Transaction;

import java.io.Serializable;
import java.text.DecimalFormat;

public class TransactionShowActivity extends AppCompatActivity implements Serializable {

    private TextView trnType;
    private TextView trnDesc;
    private TextView trnCat;
    private TextView trnDate;
    private TextView trnSrc;
    private TextView trnComm;
    private TextView trnPayee;
    private TextView trnSrcHead;
    private TextView trnCommHead;
    private TextView trnPayeeHead;
    private TextView trnAmt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_transaction_show);

        Transaction transaction = (Transaction) getIntent().getSerializableExtra("Transaction");

        trnType = findViewById(R.id.show_transactions_type);
        trnCat = findViewById(R.id.show_transactions_category);
        trnDesc = findViewById(R.id.show_transactions_description);
        trnDate = findViewById(R.id.show_transactions_date);
        trnAmt = findViewById(R.id.show_transactions_amount);
        trnComm = findViewById(R.id.show_transactions_comment);
        trnSrc = findViewById(R.id.show_transactions_source);
        trnPayee = findViewById(R.id.show_transactions_payee);
        trnCommHead = findViewById(R.id.show_transactions_cmmhead);
        trnSrcHead = findViewById(R.id.show_transactions_srchead);
        trnPayeeHead = findViewById(R.id.show_transactions_pyehead);


        trnType.setText(transaction.getTransactionType());
        trnCat.setText(transaction.getTransactionCategory());
        trnDate.setText(transaction.getTransactionDate());
        trnDesc.setText(transaction.getTransactionDescription());

        double amt = transaction.getTransactionAmount();

        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(2);
        df.setMaximumIntegerDigits(10);

        trnAmt.setText(df.format(amt));

        if (!TextUtils.isEmpty(transaction.getTransactionComment())) {
            trnComm.setText(transaction.getTransactionComment());
        } else {
            trnComm.setVisibility(View.GONE);
            trnCommHead.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(transaction.getTransactionSource())) {
            trnSrc.setText(transaction.getTransactionSource());
        } else {
            trnSrc.setVisibility(View.GONE);
            trnSrcHead.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(transaction.getTransactionPayee())) {
            trnPayee.setText(transaction.getTransactionPayee());
        } else {
            trnPayee.setVisibility(View.GONE);
            trnPayeeHead.setVisibility(View.GONE);
        }

    }
}
