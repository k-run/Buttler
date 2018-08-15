package com.example.karanc.buttler.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.example.karanc.buttler.appUtils.InsertTask;
import com.example.karanc.buttler.model.Note;
import com.example.karanc.buttler.interfaces.NoteDao;
import com.example.karanc.buttler.dbHelper.Notedatabase;
import com.example.karanc.buttler.R;
import com.github.ag.floatingactionmenu.OptionsFabLayout;

import static com.example.karanc.buttler.appUtils.Utils.displaySnackbar;
import static com.example.karanc.buttler.appUtils.Utils.getDate;
import static com.example.karanc.buttler.appUtils.Utils.getShareIntent;

public class NoteActivity extends AppCompatActivity {
    public Notedatabase notedatabase;
    EditText mtext;
    //FloatingActionButton mfab;
    OptionsFabLayout fab_menu;
    CoordinatorLayout mCoordinatorLayout;
    private String TAG = NoteActivity.class.getSimpleName();
    private NoteDao note_dao; // Sql access object
    private Note temp_note; //Used for creating a note
    private boolean update = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mtext = findViewById(R.id.note_text);
        fab_menu = findViewById(R.id.fab);

        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);

        notedatabase = Notedatabase.getInstance(NoteActivity.this);

        if ((temp_note = (Note) getIntent().getSerializableExtra("note")) != null) {
            update = true;
            mtext.setText(temp_note.getDesc());
        }


        fab_menu.setMainFabOnClickListener(v -> {
            if (fab_menu.isOptionsMenuOpened()) fab_menu.closeOptionsMenu();
        });


        fab_menu.setMiniFabSelectedListener(fabItem -> {

            switch (fabItem.getItemId()) {

                case R.id.fab_save:
                    // Call update method of DAO
                    if (update) {
                        temp_note.setDesc(mtext.getText().toString());
                        temp_note.setTime(getDate());
                        notedatabase.getNoteDao().Update(temp_note);
                        setResult(temp_note, 2);
                    } else {
                        // Insert
                        if (TextUtils.isEmpty(mtext.getText().toString())) {
                            displaySnackbar(mCoordinatorLayout, R.string.empty_insert);
                        } else {
                            temp_note = new Note(mtext.getText().toString(), getDate());
                            Log.d(TAG, "Inserting values as : " + temp_note.getId() + temp_note.getDesc() + temp_note.getTime());
                            new InsertTask(NoteActivity.this, temp_note).execute();
                        }
                    }
                    break;

                case R.id.fab_delete:
                    // Delete
                    if (TextUtils.isEmpty(mtext.getText().toString())) {
                        displaySnackbar(mCoordinatorLayout, R.string.empty_insert);
                    }
                    if ((temp_note = (Note) getIntent().getSerializableExtra("note")) != null) {
                        displayAlert(NoteActivity.this, temp_note);
                    }
                    break;

                case R.id.fab_share:
                    // Share
                    getShareIntent(NoteActivity.this, temp_note);
                    break;
            }
        });
    }

    public void displayAlert(Context context, Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.alert_message);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.alert_yes, (dialog, which) -> {
            // Delete note
            Log.d(TAG, "Deleting values :" + note.getDesc() + note.getTime());
            notedatabase.getNoteDao().delete(note);
            displaySnackbar(mCoordinatorLayout, R.string.delete_success);
            setResult(note, 3);
            dialog.cancel();
        });

        builder.setNegativeButton(R.string.alert_no, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        if (fab_menu.isOptionsMenuOpened()) fab_menu.closeOptionsMenu();
        super.onBackPressed();

    }

    public void setResult(Note note, int flag) {
        setResult(flag, new Intent().putExtra("note", note));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            Snackbar.make(mCoordinatorLayout, R.string.success_insert, Snackbar.LENGTH_SHORT).show();
        } else
            Snackbar.make(mCoordinatorLayout, R.string.failure_insert, Snackbar.LENGTH_SHORT).show();
    }
}
