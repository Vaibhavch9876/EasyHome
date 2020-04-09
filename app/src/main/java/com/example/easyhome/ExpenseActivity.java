package com.example.easyhome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.easyhome.data.MyDBHandler;
import com.example.easyhome.model.Transaction;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpenseActivity extends AppCompatActivity {

    private static final int CALCULATOR_CODE = 1001;
    private MyDBHandler db;

    private Toolbar mToolbar;
    private LinearLayout expandableLinearLayout;
    private ImageView expandImageView;
    private boolean expanded = false;
    private RelativeLayout openCalenderLayout;
    private TextView expenseDateTextView;
    private TextView expenseExpandText;
    private ImageView expenseCalculator;
    private long mLastClickTime;
    private TextView expenseAmountText;
    private EditText expenseCategoryTextView;
    private EditText expenseDescriptionTextView;
    private EditText expenseSourceTextView;
    private EditText expenseCommentTextView;
    private EditText expensePayeeTextView;

    private boolean newData = true;
    private int oldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        db = new MyDBHandler(ExpenseActivity.this);


        mToolbar = findViewById(R.id.expense_toolbar);
        setSupportActionBar(mToolbar);

        if (getIntent().getStringExtra("Title") != null)
            getSupportActionBar().setTitle("Edit Expense");
        else
            getSupportActionBar().setTitle("Add Expense");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        expandableLinearLayout = findViewById(R.id.optional_items_expense);
        expandImageView = findViewById(R.id.expand_expense_button);
        openCalenderLayout = findViewById(R.id.open_calender_layout_expense);
        expenseDateTextView = findViewById(R.id.expense_date_textview);
        expenseExpandText = findViewById(R.id.expense_expand_more_text);
        expenseCalculator = findViewById(R.id.expense_calculator);
        expenseAmountText = findViewById(R.id.expense_amount_text_view);
        expenseCategoryTextView = findViewById(R.id.expense_category);
        expenseDescriptionTextView = findViewById(R.id.expense_description);
        expenseSourceTextView = findViewById(R.id.expense_source);
        expenseCommentTextView = findViewById(R.id.expense_comment);
        expensePayeeTextView = findViewById(R.id.expense_payee);

        expenseAmountText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});

        String todayDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        expenseDateTextView.setText(todayDate);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        expandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expanded) {
                    expandImageView.setBackgroundResource(R.drawable.slide_down_icon);
                    expandableLinearLayout.setVisibility(View.GONE);
                    expenseExpandText.setText("More Options");
                    expanded = false;
                }
                else {
                    expandImageView.setBackgroundResource(R.drawable.slide_up_icon);
                    expandableLinearLayout.setVisibility(View.VISIBLE);
                    expenseExpandText.setText("Less Options");
                    expanded = true;
                }
            }
        });

        openCalenderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalenderLayout.setClickable(false);
                showCalender();
                openCalenderLayout.setClickable(true);
            }
        });

        expenseCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                    Intent calculatorIntent = new Intent(ExpenseActivity.this, CalculatorActivity.class);
                    calculatorIntent.putExtra("Amount", expenseAmountText.getText().toString());
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

            expenseCategoryTextView.setText(initialValues.getTransactionCategory());
            expenseDescriptionTextView.setText(initialValues.getTransactionDescription());
            expenseCommentTextView.setText(initialValues.getTransactionComment());
            expenseSourceTextView.setText(initialValues.getTransactionSource());
            expenseDateTextView.setText(initialValues.getTransactionDate());
            expensePayeeTextView.setText(initialValues.getTransactionPayee());

            double amt = initialValues.getTransactionAmount();
            expenseAmountText.setText(String.valueOf(amt));

        }

    }

    private void showCalender() {

        Dialog dialog = new Dialog(ExpenseActivity.this);

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
                expenseDateTextView.setText(selectedDate);

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


            String expense_date;
            double expense_amount;
            String expense_Category;
            String expense_description;


            TextInputLayout expense_category_layout = findViewById(R.id.expense_category_layout);
            TextInputLayout expense_description_layout = findViewById(R.id.expense_description_layout);
            TextInputLayout expense_amount_layout = findViewById(R.id.expense_amount_layout);


            //getting category
            if (!TextUtils.isEmpty(expenseCategoryTextView.getText().toString().trim())) {
                expense_Category = expenseCategoryTextView.getText().toString().trim();
                expense_category_layout.setError(null);
            }
            else {
                expense_category_layout.setError("* Required Field");
                expenseCategoryTextView.setText("");
                if(expenseCategoryTextView.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                return true;
            }

            //getting description
            if (!TextUtils.isEmpty(expenseDescriptionTextView.getText().toString().trim())) {
                expense_description = expenseDescriptionTextView.getText().toString().trim();
                expense_description_layout.setError(null);
            }
            else {
                expense_description_layout.setError("* Required Field");
                expenseDescriptionTextView.setText("");
                if(expenseDescriptionTextView.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                return true;
            }

            //getting amount
            if (!TextUtils.isEmpty(expenseAmountText.getText().toString().trim())) {
                expense_amount = Double.parseDouble(expenseAmountText.getText().toString().trim());
                expense_amount_layout.setError(null);
            }
            else {
                expense_amount_layout.setError("* Required Field");
                if(expenseAmountText.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                return true;
            }

            //getting date
            expense_date = expenseDateTextView.getText().toString();

            //getting source
            String expense_source = null;
            try {
                expense_source = expenseSourceTextView.getText().toString().trim();
            }catch (Exception e) {
                e.printStackTrace();
            }

            //getting comment
            String expense_comment = null;
            try {
                expense_comment = expenseCommentTextView.getText().toString().trim();
            }catch (Exception e) {
                e.printStackTrace();
            }

            //getting payee
            String expense_payee = null;
            try {
                expense_payee = expensePayeeTextView.getText().toString().trim();
            }catch (Exception e) {
                e.printStackTrace();
            }

            /*

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date expense_date = null;
            try {
                expense_date = sdf.parse(expenseDateTextView.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date expense_entry_datetime = Calendar.getInstance().getTime();


            Calendar cal = Calendar.getInstance();
            cal.setTime(expense_date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime());

            // Final values here
            Log.d("Income Values",expense_Category + "\n" + expense_description + "\n" + expense_source + "\n"
                    + expense_payee + "\n" + expense_comment + "\n" + expense_amount + "\n" + sqlDate.toString());
*/

            Transaction transaction = new Transaction();

            transaction.setTransactionType("expense");
            transaction.setTransactionAmount(expense_amount);
            transaction.setTransactionCategory(expense_Category);
            transaction.setTransactionComment(expense_comment);
            transaction.setTransactionDate(expense_date);
            transaction.setTransactionDescription(expense_description);
            transaction.setTransactionSource(expense_source);
            transaction.setTransactionPayee(expense_payee);

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
                    expenseAmountText.setText(Amount);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

