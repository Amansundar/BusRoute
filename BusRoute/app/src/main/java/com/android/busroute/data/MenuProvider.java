package com.android.busroute.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.busroute.util.SelectionBuilder;


public class MenuProvider extends ContentProvider {
    DbHelper dbHelper;

    private static final String AUTHORITY = DbContract.CONTENT_AUTHORITY;
    public static final String BASE_DATA_NAME = "route";
    public static final Uri SEARCH_SUGGEST_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_DATA_NAME + "/" + SearchManager.SUGGEST_URI_PATH_QUERY);


    public static final int ROUTE_TIMING_ENTRIES = 1;

    public static final int ROUTE_ENTRIES_ID = 2;

    public static final int ROUTE_ALL_ENTRIES = 3;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "entries", ROUTE_TIMING_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "routes", ROUTE_ALL_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "entries/*", ROUTE_ENTRIES_ID);
    }


    @Override
    public boolean onCreate()
    {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_ENTRIES_ID:
                // Return a single entry, by ID.
                String id = uri.getLastPathSegment();
                builder.where(DbContract.MenuEntry._ID + "=?", id);
            case ROUTE_ALL_ENTRIES:
                // Return all known entries.
                builder.table(DbContract.MenuEntry.ROUTE_NAME)
                        .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            case ROUTE_TIMING_ENTRIES:
                // Return all known entries.
                builder.table(DbContract.MenuEntry.ROUTE_TIMING_NAME)
                        .where(selection, selectionArgs);
                 c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                 ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }




    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_TIMING_ENTRIES:
                return DbContract.MenuEntry.CONTENT_TYPE;
            case ROUTE_ENTRIES_ID:
                return DbContract.MenuEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case ROUTE_TIMING_ENTRIES:
                long id = db.insertOrThrow(DbContract.MenuEntry.ROUTE_TIMING_NAME, null, contentValues);
                result = Uri.parse(DbContract.MenuEntry.CONTENT_URI + "/" + id);
                break;
            case ROUTE_ENTRIES_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_TIMING_ENTRIES:
                count = builder.table(DbContract.MenuEntry.ROUTE_TIMING_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(DbContract.MenuEntry.ROUTE_TIMING_NAME)
                        .where(DbContract.MenuEntry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_TIMING_ENTRIES:
                count = builder.table(DbContract.MenuEntry.ROUTE_TIMING_NAME)
                        .where(selection, selectionArgs)
                        .update(db, contentValues);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(DbContract.MenuEntry.ROUTE_TIMING_NAME)
                        .where(DbContract.MenuEntry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, contentValues);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }
}
