package com.example.easyhome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.udojava.evalex.Expression;

import java.math.BigDecimal;

public class CalculatorActivity extends AppCompatActivity {

    Button btn0,btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btnPlus,btnMinus,btnMultiply,btnDivision,btnEqual,btnDot,tvClear,brckOpen, brckClose;
    TextView tvInput, tvDel;
    String process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calculator);

        btn0 = findViewById(R.id.zero);
        btn1 = findViewById(R.id.one);
        btn2 = findViewById(R.id.two);
        btn3 = findViewById(R.id.three);
        btn4 = findViewById(R.id.four);
        btn5 = findViewById(R.id.five);
        btn6 = findViewById(R.id.six);
        btn7 = findViewById(R.id.seven);
        btn8 = findViewById(R.id.eight);
        btn9 = findViewById(R.id.nine);

        btnPlus = findViewById(R.id.add);
        btnMinus = findViewById(R.id.sub);
        btnDivision = findViewById(R.id.divide);
        btnMultiply = findViewById(R.id.multi);

        btnEqual = findViewById(R.id.equal);
        btnDot=findViewById(R.id.decimal);

        brckOpen = findViewById(R.id.opn_bracket);
        brckClose = findViewById(R.id.cls_bracket);

        tvInput = findViewById(R.id.expressonTextView);
        tvClear=findViewById(R.id.clearTextView);
        tvDel = findViewById(R.id.delTextView);

        final Button cancelButton = findViewById(R.id.cancel_calculator_button);
        Button saveButton = findViewById(R.id.save_calculator_button);

        process = "0.00";
        try {
            process = getIntent().getStringExtra("Amount");
        }catch (Exception e) {
            e.printStackTrace();
        }

        tvInput.setText(process);

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.setText("");
            }
        });

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("0");
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("1");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("2");
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("3");
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("4");
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("5");
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("6");
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("7");
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("8");
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("9");
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("+");
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("-");
            }
        });

        btnMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("*");
            }
        });

        btnDivision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("/");
            }
        });

        brckOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append("(");
            }
        });

        brckClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append(")");
            }
        });

        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvInput.append(".");
            }
        });

        tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                if (!TextUtils.isEmpty(process))
                    tvInput.setText(process.substring(0, process.length() - 1));
            }
        });

        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluate();
            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluate();
                Intent data = new Intent();

                String currAmount = tvInput.getText().toString();

                double current_amt = Double.parseDouble(currAmount) * 100;
                current_amt = Math.round(current_amt);
                current_amt /= 100;

                data.putExtra("Amount", String.valueOf(current_amt));
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }

    private void evaluate() {

        process = tvInput.getText().toString();

        Expression expression = new Expression(process);

        BigDecimal finalResult;

        try {
            finalResult = expression.eval();
        }catch (Exception e) {
            //Toast.makeText(CalculatorActivity.this, "Illegal", Toast.LENGTH_SHORT).show();
            finalResult = new BigDecimal(0);
        }
        Double res = finalResult.doubleValue();

        tvInput.setText(String.valueOf(res));
    }

}
