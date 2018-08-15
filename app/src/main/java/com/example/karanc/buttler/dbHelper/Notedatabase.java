package com.example.karanc.buttler.dbHelper;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.karanc.buttler.R;
import com.example.karanc.buttler.interfaces.NoteDao;
import com.example.karanc.buttler.model.Note;

import static com.example.karanc.buttler.appUtils.Utils.DB_NAME;
import static com.example.karanc.buttler.appUtils.Utils.TABLE_NAME;


@Database(entities = {Note.class}, version = 2)
public abstract class Notedatabase extends RoomDatabase {
    public static Migration FROM_1_TO_2;
    private static Notedatabase ourInstance;

    public static Notedatabase getInstance(Context context) {
        if (null == ourInstance) {
            ourInstance = buildInst(context);
        }
        return ourInstance;
    }

    static void getMigration() {
        FROM_1_TO_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN Color INTEGER default " + R.color.colorPrimaryPurple + " NOT NULL");
            }
        };
    }

    private static Notedatabase buildInst(Context context) {
        getMigration();
        return Room.databaseBuilder(context, Notedatabase.class, DB_NAME).addMigrations(FROM_1_TO_2).allowMainThreadQueries().build();
    }

    public abstract NoteDao getNoteDao();

    public void cleanUp() {
        ourInstance = null;
    }


}
