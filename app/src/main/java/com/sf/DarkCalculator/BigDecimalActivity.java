package com.sf.DarkCalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import java.math.BigDecimal;

public class BigDecimalActivity extends BaseActivity implements View.OnClickListener {

    private EditText edit1;
    private EditText edit2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_decimal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edit1 = (EditText) findViewById(R.id.edit1_big);
        edit2 = (EditText) findViewById(R.id.edit2_big);
        findViewById(R.id.button_add).setOnClickListener(this);
        findViewById(R.id.button_sub).setOnClickListener(this);
        findViewById(R.id.button_mul).setOnClickListener(this);
        findViewById(R.id.button_div).setOnClickListener(this);
    }

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, BigDecimalActivity.class));
    }

    public void onClick(View v) {
        BigDecimal b1, b2;
        String s1 = edit1.getText().toString().length() == 0 ? "0" : edit1.getText().toString();
        String s2 = edit2.getText().toString().length() == 0 ? "0" : edit2.getText().toString();
        if (s1.indexOf("..") != -1 || s2.indexOf("..") != -1) {
            Snackbar.make(v, "小数点不能大于一个", Snackbar.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.button_add:
                b1 = new BigDecimal(s1);
                b2 = new BigDecimal(s2);
                ResultsActivity.actionStart(this, b1.add(b2).toString());
                break;
            case R.id.button_sub:
                b1 = new BigDecimal(s1);
                b2 = new BigDecimal(s2);
                ResultsActivity.actionStart(this, b1.subtract(b2).toString());
                break;
            case R.id.button_mul:
                b1 = new BigDecimal(s1);
                b2 = new BigDecimal(s2);
                ResultsActivity.actionStart(this, b1.multiply(b2).toString());
                break;
            case R.id.button_div:
                b1 = new BigDecimal(s1);
                b2 = new BigDecimal(s2);
                if (b2.doubleValue() == 0) {
                    Snackbar.make(v, "除数不能为零", Snackbar.LENGTH_SHORT).show();
                    break;
                }
                ResultsActivity.actionStart(this, b1.divide(b2, 100000, BigDecimal.ROUND_HALF_UP).toString());
                break;
        }
    }
}
