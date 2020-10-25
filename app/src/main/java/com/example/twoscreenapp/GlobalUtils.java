package com.example.twoscreenapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.example.twoscreenapp.R;
import com.example.twoscreenapp.DialogCallback;
import com.example.twoscreenapp.CustomDialog;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import static android.content.ContentValues.TAG;

public class GlobalUtils {

    public static String rating = "Not given yet";

    public static void showDialog(Context context, final DialogCallback dialogCallback) {
        //create the dialogbg
        final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialogTheme);
        LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.layout_dialog,null);

        dialog.setContentView(v);

        TextView btn_done = (TextView)dialog.findViewById(R.id.btn_done);
        SmileRating smileRating = (SmileRating)dialog.findViewById(R.id.smile_rating);

        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley, boolean reselected) {
                switch(smiley) {
                    case SmileRating.BAD:
                        Log.i(TAG, "Bad");
                        rating = "Bad";
                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");
                        rating = "Good";
                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");
                        rating = "Great";
                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "Okay");
                        rating = "Okay";
                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");
                        rating = "Terrible";
                        break;
                }
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogCallback != null) {
                    dialogCallback.callback(rating);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}