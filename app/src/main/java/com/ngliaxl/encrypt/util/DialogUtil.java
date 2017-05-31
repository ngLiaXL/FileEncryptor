package com.ngliaxl.encrypt.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ngliaxl.encrypt.R;

public class DialogUtil {


    public static void showEditDialog(Context context, String cancelText, String sureText, String
            hintText, final View.OnClickListener onClickListener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_view, null);
        dialog.setView(layout);
        final EditText editText = (EditText) layout.findViewById(R.id.et_dialog);
        editText.setHint(hintText);
        dialog.setPositiveButton(sureText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (onClickListener != null)
                    onClickListener.onClick(editText);
            }
        });

        dialog.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }

        });
        dialog.show();
    }

    public static void showConfirmDialog(Context context, String title, String message, String left,
                                         String right, final View.OnClickListener onClickListener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(left, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(right, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClickListener != null)
                            onClickListener.onClick(null);
                    }
                });
        dialog.create().show();
    }
}
