package com.example.easyhome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyhome.data.MyDBHandler;
import com.example.easyhome.model.Transaction;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class IncomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private LinearLayout expandableLinearLayout;
    private ImageView expandImageView;
    private boolean expanded = false;
    private RelativeLayout openCalenderLayout;
    private TextView incomeDateTextView;
    private TextView incomeExpandText;
    private ImageView incomeCalculator;
    private long mLastClickTime;
    private TextView incomeAmountText;
    private EditText incomeCategoryTextView;
    private EditText incomeDescriptionTextView;
    private EditText incomeSourceTextView;
    private EditText incomeCommentTextView;


    private int CALCULATOR_CODE = 1001;
    private MyDBHandler db;
    private boolean newData = true;
    private int oldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        db = new MyDBHandler(IncomeActivity.this);

        mToolbar = findViewById(R.id.income_toolbar);
        setSupportActionBar(mToolbar);

        if (getIntent().getStringExtra("Title") != null)
            getSupportActionBar().setTitle("Edit Income");
        else
            getSupportActionBar().setTitle("Add Income");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        expandableLinearLayout = findViewById(R.id.optional_items_income);
        expandImageView = findViewById(R.id.expand_income_button);
        openCalenderLayout = findViewById(R.id.open_calender_layout_income);
        incomeDateTextView = findViewById(R.id.income_date_textview);
        incomeExpandText = findViewById(R.id.income_expand_more_text);
        incomeCalculator = findViewById(R.id.income_calculator);
        incomeAmountText = findViewById(R.id.income_amount_text_view);
        incomeCategoryTextView = findViewById(R.id.income_category);
        incomeDescriptionTextView = findViewById(R.id.income_description);
        incomeSourceTextView = findViewById(R.id.income_source);
        incomeCommentTextView = findViewById(R.id.income_comment);


        incomeAmountText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});



        String todayDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        incomeDateTextView.setText(todayDate);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        expandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expanded) {
                    expandImageView.setBackgroundResource(R.drawable.slide_down_icon);
                    expandableLinearLayout.setVisibility(View.GONE);
                    incomeExpandText.setText("More Options");
                    expanded = false;
                } else {
                    expandImageView.setBackgroundResource(R.drawable.slide_up_icon);
                    expandableLinearLayout.setVisibility(View.VISIBLE);
                    incomeExpandText.setText("Less Options");
                    expanded = true;
                }
            }
        });

        openCalenderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                    showCalender();
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

        incomeCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                    Intent calculatorIntent = new Intent(IncomeActivity.this, CalculatorActivity.class);
                    calculatorIntent.putExtra("Amount", incomeAmountText.getText().toString());
                    startActivityForResult(calculatorIntent, CALCULATOR_CODE);
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        Transaction initialValues = (Transaction) getIntent().getSerializableExtra("Transaction");
        if (initialValues != null) {

            newData = false;
            oldId = initialValues.getTransactionId();

            incomeCategoryTextView.setText(initialValues.getTransactionCategory());
            incomeDescriptionTextView.setText(initialValues.getTransactionDescription());
            incomeCommentTextView.setText(initialValues.getTransactionComment());
            incomeSourceTextView.setText(initialValues.getTransactionSource());
            incomeDateTextView.setText(initialValues.getTransactionDate());

            double amt = initialValues.getTransactionAmount();
            incomeAmountText.setText(String.valueOf(amt));

        }

    }

    private void showCalender() {

        Dialog dialog = new Dialog(IncomeActivity.this);

        dialog.setContentView(R.layout.calender_layout);
        dialog.setCancelable(true);
        dialog.show();

        final CalendarView cal = dialog.findViewById(R.id.calender_view);

        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {

                String dd = dayOfMonth + "";
                if (dd.length() != 2)
                    dd = "0" + dd;
                String mm = month + 1 + "/";
                if (mm.length() != 3)
                    mm = "0" + mm;

                String selectedDate = year + "/" + mm + dd;
                incomeDateTextView.setText(selectedDate);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.income_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.save_income) {

            double income_amount;
            String income_Category;
            String income_description;
            String income_date;


            TextInputLayout income_category_layout = findViewById(R.id.income_category_layout);
            TextInputLayout income_description_layout = findViewById(R.id.income_description_layout);
            TextInputLayout income_amount_layout = findViewById(R.id.income_amount_layout);


            //getting category
            if (!TextUtils.isEmpty(incomeCategoryTextView.getText().toString().trim())) {
                income_Category = incomeCategoryTextView.getText().toString().trim();
                income_category_layout.setError(null);
            }
            else {
                income_category_layout.setError("* Required Field");
                incomeCategoryTextView.setText("");
                if(incomeCategoryTextView.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                return true;
            }

            //getting description
            if (!TextUtils.isEmpty(incomeDescriptionTextView.getText().toString().trim())) {
                income_description = incomeDescriptionTextView.getText().toString().trim();
                income_description_layout.setError(null);
            }
            else {
                income_description_layout.setError("* Required Field");
                incomeDescriptionTextView.setText("");
                if(incomeDescriptionTextView.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                return true;
            }

            //getting amount
            if (!TextUtils.isEmpty(incomeAmountText.getText().toString().trim())) {
                income_amount = Double.parseDouble(incomeAmountText.getText().toString().trim());
                income_amount_layout.setError(null);
            }
            else {
                income_amount_layout.setError("* Required Field");
                if(incomeAmountText.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                return true;
            }

            //getting date
            income_date = incomeDateTextView.getText().toString();

            //getting source
            String income_source = null;
            try {
                income_source = incomeSourceTextView.getText().toString().trim();
            }catch (Exception e) {
                e.printStackTrace();
            }

            //getting comment
            String income_comment = null;
            try {
                income_comment = incomeCommentTextView.getText().toString().trim();
            }catch (Exception e) {
                e.printStackTrace();
            }



            Transaction transaction = new Transaction();

            transaction.setTransactionType("income");
            transaction.setTransactionAmount(income_amount);
            transaction.setTransactionCategory(income_Category);
            transaction.setTransactionComment(income_comment);
            transaction.setTransactionDate(income_date);
            transaction.setTransactionDescription(income_description);
            transaction.setTransactionSource(income_source);
            transaction.setTransactionPayee(null);

            if (newData)
                db.addTransaction(transaction);

            else
                db.updateTransaction(transaction, oldId);

            finish();
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CALCULATOR_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    String Amount = null;
                    if (data != null) {
                        Amount = data.getStringExtra("Amount");
                    }
                    incomeAmountText.setText(Amount);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
