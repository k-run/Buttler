package com.example.karanc.buttler.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.karanc.buttler.model.Note;

import java.util.List;

import static com.example.karanc.buttler.appUtils.Utils.TABLE_NAME;

@Dao
public interface NoteDao {
    /**
     * Select query - get all notes
     *
     * @return List<Note>
     */
    @Query("SELECT * FROM " + TABLE_NAME)
    List<Note> getNotes();

    /**
     * Insert into DB
     *
     * @param note - to be inserted
     * @return list of notes
     */
    @Insert
    long insert(Note note);

    /**
     * Update the DB
     *
     * @param note - to be upated
     */
    @Update
    void Update(Note note);

    /**
     * Delete from DB
     *
     * @param note - to be deleted
     */
    @Delete
    void delete(Note note);

    /**
     * Delete multiple notes
     *
     * @param note array - to be deleted
     */
    @Delete
    void deleteNotes(Note... note);
}
