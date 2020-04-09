package com.example.easyhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.example.easyhome.data.MyDBHandler;
import com.example.easyhome.model.Transaction;

import java.text.DecimalFormat;
import java.util.List;

public class AllTransactionActivity extends AppCompatActivity {

    private MyDBHandler db;
    private RecyclerView allTransactionRecyclerView;
    private List<Transaction> transactionList;
    private Toolbar toolbar;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transaction);

        db = new MyDBHandler(AllTransactionActivity.this);

        toolbar = findViewById(R.id.all_transaction_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All Transactions");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllTransactionActivity.this);
        linearLayoutManager.setReverseLayout(true);

        allTransactionRecyclerView = findViewById(R.id.all_transaction_recycler_view);
        allTransactionRecyclerView.setLayoutManager(linearLayoutManager);
        allTransactionRecyclerView.setHasFixedSize(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        transactionList = db.getAllTransactions();
        linearLayout = findViewById(R.id.no_transaction_layout);

        final TransactionAdapter adapter = new TransactionAdapter();
        allTransactionRecyclerView.setAdapter(adapter);
        if (!transactionList.isEmpty())
            allTransactionRecyclerView.smoothScrollToPosition(transactionList.size() - 1);

        allTransactionRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(AllTransactionActivity.this
                , allTransactionRecyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                else
                    v.vibrate(50);

                new AlertDialog.Builder(AllTransactionActivity.this).setTitle("Warning")
                        .setMessage("This item will be deleted permanently")
                        .setCancelable(true)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteTransaction(transactionList.get(position));
                                transactionList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .show();

            }
        }));

        if (transactionList.isEmpty()) {
            linearLayout.setVisibility(View.VISIBLE);
        }

    }

    private class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

        public TransactionAdapter() {
        }

        @NonNull
        @Override
        public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(AllTransactionActivity.this).inflate(R.layout.all_transaction_recycler_layout, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

            holder.setValues(transactionList.get(position));

        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }
    }

    private class TransactionViewHolder extends RecyclerView.ViewHolder {
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

         TransactionViewHolder(View view) {
            super(view);


             trnType = view.findViewById(R.id.all_transactions_type);
             trnCat = view.findViewById(R.id.all_transactions_category);
             trnDesc = view.findViewById(R.id.all_transactions_description);
             trnDate = view.findViewById(R.id.all_transactions_date);
             trnAmt = view.findViewById(R.id.all_transactions_amount);
             trnComm = view.findViewById(R.id.all_transactions_comment);
             trnSrc = view.findViewById(R.id.all_transactions_source);
             trnPayee = view.findViewById(R.id.all_transactions_payee);
             trnCommHead = view.findViewById(R.id.all_transactions_cmmhead);
             trnSrcHead = view.findViewById(R.id.all_transactions_srchead);
             trnPayeeHead = view.findViewById(R.id.all_transactions_pyehead);

        }

        public void setValues(Transaction transaction) {


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
}
