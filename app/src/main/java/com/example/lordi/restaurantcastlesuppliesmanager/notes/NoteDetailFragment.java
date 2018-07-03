package com.example.lordi.restaurantcastlesuppliesmanager.notes;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.lordi.restaurantcastlesuppliesmanager.R;
import com.example.lordi.restaurantcastlesuppliesmanager.notes.data.Note;
import com.example.lordi.restaurantcastlesuppliesmanager.notes.data.NotesContentContract;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Objects;

/**
 * A fragment representing a single Note detail screen.
 * This fragment is either contained in a {@link NoteListActivity}
 * in two-pane mode (on tablets) or a {@link NoteDetailActivity}
 * on handsets.
 */
public class NoteDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "noteId";

    /**
     * The dummy content this fragment is presenting.
     */
    private Note mItem;
    private Uri itemUri;

    /**
     * Content Resolver
     */
    private ContentResolver contentResolver;

    /**
     * Is this an insert or an update?
     */
    private boolean isUpdate;

    /**
     * The component bindings
     */
    EditText editTitle;
    EditText editContent;

    /**
     * The timer for saving the record back to SQL
     */
    final Handler timer = new Handler();
    final Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            saveData();                             // Save the data
            timer.postDelayed(timerTask, 5000);     // Every 5 seconds
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteDetailFragment() {
    }

    /**
     * Lifecycle event handler - called when the fragment is created.
     * @param savedInstanceState the saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the ContentResolver
        contentResolver = Objects.requireNonNull(getContext()).getContentResolver();

        // Unbundle the arguments if any.  If there is an argument, load the data from
        // the content resolver aka the content provider.
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_ITEM_ID)) {
            String itemId = getArguments().getString(ARG_ITEM_ID);
            itemUri = NotesContentContract.Notes.uriBuilder(itemId);
            Cursor data = contentResolver.query(itemUri, NotesContentContract.Notes.PROJECTION_ALL, null, null, null);
            if (data != null) {
                data.moveToFirst();
                mItem = Note.fromCursor(data);
                isUpdate = true;
            }
        } else {
            mItem = new Note();
            isUpdate = false;
        }

        // Start the timer for the delayed start
        timer.postDelayed(timerTask, 5000);
    }

    /**
     * Lifecycle event handler - called when the fragment is paused.  Use this to do any
     * saving of data as it is the last opportunity to reliably do so.
     */
    @Override
    public void onPause() {
        super.onPause();
        timer.removeCallbacks(timerTask);
        saveData();
    }

    /**
     * Save the data from the form back into the database.
     */
    private void saveData() {
        // Save the edited text back to the item.
        boolean isUpdated = false;
        if (!mItem.getTitle().equals(editTitle.getText().toString().trim())) {
            mItem.setTitle(editTitle.getText().toString().trim());
            mItem.setUpdated(DateTime.now(DateTimeZone.UTC));
            isUpdated = true;
        }
        if (!mItem.getContent().equals(editContent.getText().toString().trim())) {
            mItem.setContent(editContent.getText().toString().trim());
            mItem.setUpdated(DateTime.now(DateTimeZone.UTC));
            isUpdated = true;
        }

        // Convert to ContentValues and store in the database.
        if (isUpdated) {
            ContentValues values = mItem.toContentValues();
            if (isUpdate) {
                contentResolver.update(itemUri, values, null, null);
            } else {
                itemUri = contentResolver.insert(NotesContentContract.Notes.CONTENT_URI, values);
                isUpdate = true;    // Anything from now on is an update
                itemUri = NotesContentContract.Notes.uriBuilder(mItem.getNoteId());
            }
        }
    }

    /**
     * Returns the current note.
     * @return the current data
     */
    public Note getNote() {
        return mItem;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get a reference to the root view
        View rootView = inflater.inflate(R.layout.note_detail, container, false);

        // Update the text in the editor
        editTitle = rootView.findViewById(R.id.edit_title);
        editContent = rootView.findViewById(R.id.edit_content);

        editTitle.setText(mItem.getTitle());
        editContent.setText(mItem.getContent());

        return rootView;
    }
}
