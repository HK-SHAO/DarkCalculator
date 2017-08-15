package com.sf.DarkCalculator;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.BigInteger;

public class BaseConversionActivity extends BaseActivity {

    private EditText textIn;
    private TextView textOut;
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private int progress1 = 10;
    private int progress2 = 2;
    private TextView textviewIn;
    private TextView textviewOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_conversion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initTextIn();
        initTextOut();
        initSeekBar();
        textviewIn = (TextView) findViewById(R.id.textview_in);
        textviewOut = (TextView) findViewById(R.id.textview_out);
    }

    private void initSeekBar() {
        seekBar1 = (SeekBar) findViewById(R.id.seekbar1);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser) {
                progress1 = p + 2;
                textviewIn.setText(progress1 + " 进制:");
                String str = "···";
                try {
                    str = new BigInteger(textIn.getText().toString(), progress1).toString(progress2);
                } catch (Exception e) {
                }
                textOut.setText(str);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2 = (SeekBar) findViewById(R.id.seekbar2);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser) {
                progress2 = p + 2;
                textviewOut.setText(progress2 + " 进制:");
                String str = "···";
                try {
                    str = new BigInteger(textIn.getText().toString(), progress1).toString(progress2);
                } catch (Exception e) {
                }
                textOut.setText(str);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initTextOut() {
        textOut = (TextView) findViewById(R.id.text_out);
        AutofitHelper.create(textOut).setMaxLines(5);
        textOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(textOut.getText());
                Snackbar.make(v, "已复制转换结果", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void initTextIn() {
        textIn = (EditText) findViewById(R.id.text_in);
        textIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = "···";
                try {
                    str = new BigInteger(s.toString(), progress1).toString(progress2);
                } catch (Exception e) {
                }
                textOut.setText(str);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, BaseConversionActivity.class));
    }
}
