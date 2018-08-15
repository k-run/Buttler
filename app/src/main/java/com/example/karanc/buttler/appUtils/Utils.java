package com.example.karanc.buttler.appUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.example.karanc.buttler.R;
import com.example.karanc.buttler.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {
    public static final String TABLE_NAME = "notes";
    public static final String DB_NAME = "notesdb.db";
    public static int REQUEST_CODE = 200;
    public static List<Note> resList = new ArrayList<>();
    public static String TAG = Utils.class.getSimpleName();
    static ArrayList<Note> filteredNotes;

    public static String getDate() {
        Calendar cal = Calendar.getInstance();
        Date d = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        return dateFormat.format(d);
    }

    public static void getShareIntent(Context context, Note temp_note) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        share.putExtra(Intent.EXTRA_SUBJECT, "Get this app!");
        share.putExtra(Intent.EXTRA_TEXT, temp_note);
        context.startActivity(Intent.createChooser(share, "Check this out!"));
    }

    public static void displaySnackbar(CoordinatorLayout mCoordinatorLayout, int messageid) {
        Snackbar.make(mCoordinatorLayout, messageid, Snackbar.LENGTH_SHORT).show();
    }


    public static void displayErrorMsg(Context context) {
        Toast.makeText(context, R.string.failure_display, Toast.LENGTH_SHORT).show();
    }

    public static int getRandomMaterialColor(Context context, String typeColor) {
        int returnColor = context.getResources().getColor(R.color.colorPrimaryPurple);
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, context.getResources().getColor(R.color.colorPrimaryPurple));
            colors.recycle();
        }
        Log.d(TAG, "getRandomMaterialColor: " + returnColor);
        return returnColor;
    }

    public static List<Note> filter(String text, List<Note> noteList) {
        filteredNotes = new ArrayList<>();

        for (Note n : noteList) {
            if (n.getDesc().toLowerCase().contains(text.toLowerCase())) {
                Log.d(TAG, "filter - note desc:" + n.getDesc());
                Log.d(TAG, "filter - args :" + text.toLowerCase());
                filteredNotes.add(n);
            }
        }
        return filteredNotes;
    }
}
