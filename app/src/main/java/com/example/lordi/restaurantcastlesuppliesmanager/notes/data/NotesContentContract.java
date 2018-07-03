package com.example.lordi.restaurantcastlesuppliesmanager.notes.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Per the official Android documentation, this class defines all publically available
 * elements, like the authority, the content URIs, columns, and content types for each
 * element
 */
public class NotesContentContract {
    /**
     * The authority of the notes content provider - this must match the authority
     * specified in the AndroidManifest.xml provider section
     */
    public static final String AUTHORITY = "com.example.lordi.restaurantcastlesuppliesmanager.notes.data.NotesContentProvider";

    /**
     * The content URI for the top-level notes authority
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Constants for the Notes table
     */
    public static final class Notes implements BaseColumns {
        /**
         * The Table Name
         */
        public static final String TABLE_NAME = "notes";

        /**
         * The internal ID
         */
        public static final String _ID = "id";

        /**
         * The noteId field
         */
        public static final String NOTEID = "noteId";

        /**
         * The title field
         */
        public static final String TITLE = "title";

        /**
         * The content field
         */
        public static final String CONTENT = "content";

        /**
         * The created field
         */
        public static final String CREATED = "created";

        /**
         * The updated field
         */
        public static final String UPDATED = "updated";

        /**
         * The directory base-path
         */
        public static final String DIR_BASEPATH = "notes";

        /**
         * The items base-path
         */
        public static final String ITEM_BASEPATH = "notes/*";

        /**
         * The SQLite database command to create the table
         */
        public static final String CREATE_SQLITE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + _ID + " INTEGER PRIMARY KEY, "
                        + NOTEID + " TEXT UNIQUE NOT NULL, "
                        + TITLE + " TEXT NOT NULL DEFAULT '', "
                        + CONTENT + " TEXT NOT NULL DEFAULT '', "
                        + CREATED + " BIGINT NOT NULL DEFAULT 0, "
                        + UPDATED + " BIGINT NOT NULL DEFAULT 0)";

        /**
         * The content URI for this table
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(NotesContentContract.CONTENT_URI, TABLE_NAME);

        /**
         * The mime type of a directory of items
         */
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.example.lordi.restaurantcastlesuppliesmanager.notes";

        /**
         * The mime type of a single item
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.example.lordi.restaurantcastlesuppliesmanager.notes";

        /**
         * A projection of all columns in the items table
         */
        public static final String[] PROJECTION_ALL = {
                _ID,
                NOTEID,
                TITLE,
                CONTENT,
                CREATED,
                UPDATED
        };

        /**
         * The default sort order (SQLite syntax)
         */
        public static final String SORT_ORDER_DEFAULT = CREATED + " ASC";

        /**
         * Build a URI for the provided note
         * @param noteId the ID of the provided note
         * @return the URI of the provided note
         */
        public static Uri uriBuilder(String noteId) {
            return new Uri.Builder()
                    .scheme("content")
                    .authority(NotesContentContract.AUTHORITY)
                    .appendPath(Notes.DIR_BASEPATH)
                    .appendPath(noteId)
                    .build();
        }
    }
}

