package com.mahaoyuan.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ResultDialog extends Dialog {
    private String text = "";
    public ResultDialog(Context context,String mtext) {
        super(context);
        text = mtext;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_dialog);
        TextView textView = findViewById(R.id.result);
        textView.setText(text);
    }


}