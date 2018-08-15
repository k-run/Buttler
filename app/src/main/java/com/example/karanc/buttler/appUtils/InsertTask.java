package com.example.karanc.buttler.appUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.karanc.buttler.activities.NoteActivity;
import com.example.karanc.buttler.model.Note;

import java.lang.ref.WeakReference;

public class InsertTask extends AsyncTask<Void, Void, Boolean> {

    private WeakReference<NoteActivity> weakReference;
    private Note note;
    private String TAG = InsertTask.class.getSimpleName();


    public InsertTask(NoteActivity noteActivity, Note temp_note) {
        weakReference = new WeakReference<>(noteActivity);
        this.note = temp_note;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        long i = weakReference.get().notedatabase.getNoteDao().insert(note);
        note.setId(i);
        Log.d(TAG, "doInBackground: " + i);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            weakReference.get().setResult(note, 1);
            weakReference.get().finish();
        }
    }

}
