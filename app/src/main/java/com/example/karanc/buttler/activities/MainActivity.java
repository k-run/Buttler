package com.example.karanc.buttler.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.RelativeLayout;

import com.example.karanc.buttler.interfaces.ClickListener;
import com.example.karanc.buttler.model.Note;
import com.example.karanc.buttler.dbHelper.Notedatabase;
import com.example.karanc.buttler.adapter.NotesAdapter;
import com.example.karanc.buttler.R;
import com.example.karanc.buttler.appUtils.RecyclerTouchListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.example.karanc.buttler.appUtils.Utils.REQUEST_CODE;
import static com.example.karanc.buttler.appUtils.Utils.displaySnackbar;
import static com.example.karanc.buttler.appUtils.Utils.filter;

public class MainActivity extends AppCompatActivity {

    public NotesAdapter notesAdapter;
    private StaggeredGridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private List<Note> noteList, multiSelectList, filteredList;
    private Notedatabase notedatabase;
    private String TAG = MainActivity.class.getSimpleName();
    private FloatingActionButton maddbtn;
    private int position;
    private SearchView mSearchView;
    private boolean isMultiSelect = false;
    private Menu context_menu;
    private ActionMode mActionMode;
    private CoordinatorLayout mCoordinatorLayout;
    private RelativeLayout mEmptyContainer;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //Prepare the menu
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_action, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            //mode.getCustomView().setBackgroundColor(getApplicationContext().getResources().getColor(android.R.color.white));
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // display alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage("Delete " + multiSelectList.size() + " notes?");
                    builder.setCancelable(true);
                    builder.setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                        // Deleting notes
                        for (int i = 0; i < multiSelectList.size(); i++) {
                            Log.d(TAG, "Deleting: " + multiSelectList.get(i));
                            notedatabase.getNoteDao().deleteNotes(multiSelectList.get(i));
                            noteList.remove(multiSelectList.get(i));
                        }
                        // Display Snackbar
                        displaySnackbar(mCoordinatorLayout, R.string.delete_success);
                        dialog.cancel();
                        // Refresh adapter
                        refreshAdapter();
                        // dismiss the contextual action bar
                        if (mActionMode != null) mActionMode.finish();
                        // check for empty state
                        if (noteList.isEmpty()) displayEmptyState();
                    });

                    builder.setNegativeButton(R.string.alert_no, (dialog, which) -> {
                        dialog.cancel();
                        if (mActionMode != null) mActionMode.finish();
                    });

                    builder.setOnCancelListener(dialog1 -> {
                        Log.d(TAG, "onCancelListener: ");
                        if (mActionMode != null) mActionMode.finish();
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();


                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiSelectList = new ArrayList<>();
            refreshAdapter();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        displayList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Searchable config with searchview
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        mSearchView.setMaxWidth(Integer.MAX_VALUE);


        // Set Listener
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter when query is submitted
                Log.d(TAG, "onQueryTextSubmit: " + query);
                filteredList = filter(query, noteList);
                notesAdapter.filterList(filteredList);
                if (filteredList.isEmpty()) displayEmptyState();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter progressively as query changes
                Log.d(TAG, "onQueryTextChange: " + newText);
                if (!newText.isEmpty() && mEmptyContainer.getVisibility() == View.VISIBLE) {
                    Log.d(TAG, "Query is not empty & empty state is visible: " + newText);
                    mEmptyContainer.setVisibility(View.GONE);
                }

                if (newText.isEmpty() && mEmptyContainer.getVisibility() == View.VISIBLE) {
                    Log.d(TAG, "Empty query");
                    mEmptyContainer.setVisibility(View.GONE);
                    // Close the keyboard
                    mSearchView.post(() -> {
                        mSearchView.clearFocus();
                        if (!mSearchView.isIconified())
                            mSearchView.setIconified(true);
                    });
                }

                filteredList = filter(newText, noteList);
                notesAdapter.filterList(filteredList);
                if (filteredList.isEmpty()) displayEmptyState();
                return false;
            }
        });

        mSearchView.setOnCloseListener(() -> {
            if (mEmptyContainer.getVisibility() == View.VISIBLE)
                mEmptyContainer.setVisibility(View.GONE);
            //displayList();
            return false;
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // search action
        return item.getItemId() == R.id.action_search || super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        // close searchview
        if (!mSearchView.isIconified()) {
            // Clears the text
            mSearchView.setIconified(true);
            mSearchView.onActionViewCollapsed();
            refreshAdapter();
            if (mEmptyContainer.getVisibility() == View.VISIBLE)
                mEmptyContainer.setVisibility(View.GONE);

        } else super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == 1) {
                Log.d(TAG, "Request code is 1");
                noteList.add((Note) data.getSerializableExtra("note"));
                notesAdapter.notifyDataSetChanged();
                // displayList();
            } else if (resultCode == 2) {
                Log.d(TAG, "Request code 2, update return");
                noteList.set(position, (Note) data.getSerializableExtra("note"));
                notesAdapter.notifyDataSetChanged();
            } else if (resultCode == 3) {
                Log.d(TAG, "Request code 3, delete return");
                Log.d(TAG, "Deleting this from list:" + (Note) data.getSerializableExtra("note"));
                noteList.remove(MainActivity.this.position);
                notesAdapter.notifyItemRemoved(MainActivity.this.position);
                if (noteList.isEmpty()) displayEmptyState();
            }
        }
    }

    private void displayList() {
        notedatabase = Notedatabase.getInstance(MainActivity.this);
        new RetrieveTask(this).execute();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerview);
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);

        mEmptyContainer = findViewById(R.id.empty_container);

        recyclerView.setLayoutManager(gridLayoutManager);

        noteList = new ArrayList<>();
        multiSelectList = new ArrayList<>();

        notesAdapter = new NotesAdapter(noteList, MainActivity.this, multiSelectList);


        maddbtn = findViewById(R.id.add_fab);
        maddbtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    private void multi_select(int pos) {
        if (mActionMode != null) {
            if (multiSelectList.contains(noteList.get(pos))) {
                Log.d(TAG, "multi_select removed: " + multiSelectList.contains(noteList.get(pos)));
                multiSelectList.remove(noteList.get(pos));
            } else {
                Log.d(TAG, "multi_select added: " + multiSelectList.contains(noteList.get(pos)));
                multiSelectList.add(noteList.get(pos));
            }

            if (multiSelectList.size() > 0)
                mActionMode.setTitle("" + multiSelectList.size() + " selected");
            else mActionMode.setTitle("");
            refreshAdapter();
        }
    }

    private void refreshAdapter() {
        notesAdapter.selectednotes_list = multiSelectList;
        notesAdapter.list = noteList;
        notesAdapter.notifyDataSetChanged();
    }

    private void displayEmptyState() {
        mEmptyContainer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        notedatabase.cleanUp();
        super.onDestroy();
    }

    class RetrieveTask extends AsyncTask<Void, Void, List<Note>> {
        private WeakReference<MainActivity> weakReference;

        public RetrieveTask(MainActivity mainActivity) {
            weakReference = new WeakReference<>(mainActivity);
        }

        @Override
        protected List<Note> doInBackground(Void... voids) {
            if (weakReference.get() != null)
                return weakReference.get().notedatabase.getNoteDao().getNotes();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            if (notes != null & notes.size() > 0) {
                if (mEmptyContainer.getVisibility() == View.VISIBLE)
                    mEmptyContainer.setVisibility(View.GONE);
                weakReference.get().noteList = notes;
                weakReference.get().filteredList = noteList;
                //weakReference.get().noteList.addAll(weakReference.get().getNotes());
                Log.d(TAG, "Result: " + notes);
                weakReference.get().notesAdapter = new NotesAdapter(notes, weakReference.get(), multiSelectList);
/*
                // Randomly set note background
                for(Note n:notes) {
                    n.setColor(getRandomMaterialColor(MainActivity.this,"500"));
                }

*/
                weakReference.get().recyclerView.addOnItemTouchListener(new RecyclerTouchListener(recyclerView, getApplicationContext(), new ClickListener() {
                    @Override
                    public void onClick(View view, int pos) {
                        if (isMultiSelect)
                            multi_select(pos);
                        else {
                            Log.d(TAG, "Recylerview item onClick: ");
                            MainActivity.this.position = pos;
                            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                            intent.putExtra("note", filteredList.get(pos));
                            Log.d(TAG, "pos: " + pos);
                            Log.d(TAG, "list.get(position): " + weakReference.get().filteredList.get(position).getDesc());
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    }

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick with args as note");
                        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                        intent.putExtra("note", noteList.get(recyclerView.getChildAdapterPosition(view)));
                        startActivityForResult(intent, REQUEST_CODE);
                    }

                    @Override
                    public void onLongClick(View view, int pos) {
                        Log.d(TAG, "onLongClick ");
                        if (!isMultiSelect) {
                            multiSelectList = new ArrayList<>();
                            isMultiSelect = true;

                            if (mActionMode == null) {
                                mActionMode = startActionMode(mActionModeCallback);
                            }
                        }

                        multi_select(pos);
                    }
                }));

                weakReference.get().recyclerView.setAdapter(weakReference.get().notesAdapter);
                weakReference.get().notesAdapter.notifyDataSetChanged();
            } else {
                // empty state
                displayEmptyState();
            }
        }
    }

}
