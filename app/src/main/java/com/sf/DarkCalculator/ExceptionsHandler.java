package com.sf.DarkCalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by user on 2017/7/21.
 */

public class ExceptionsHandler {
    public static void show(Context context, String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("程序抛出未知异常");
        builder.setMessage(error + "\n\n请截图发送给开发者");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
