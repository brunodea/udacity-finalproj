package br.brunodea.nevertoolate.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static br.brunodea.nevertoolate.db.NeverTooLateContract.FAVORITES;
import static br.brunodea.nevertoolate.db.NeverTooLateContract.FAVORITES_CONTENT_ITEM_TYPE;
import static br.brunodea.nevertoolate.db.NeverTooLateContract.FAVORITES_CONTENT_TYPE;
import static br.brunodea.nevertoolate.db.NeverTooLateContract.CONTENT_URI;
import static br.brunodea.nevertoolate.db.NeverTooLateContract.FAVORITES_ID;
import static br.brunodea.nevertoolate.db.NeverTooLateContract.NOTIFICATIONS;
import static br.brunodea.nevertoolate.db.NeverTooLateContract.NOTIFICATIONS_CONTENT_ITEM_TYPE;
import static br.brunodea.nevertoolate.db.NeverTooLateContract.NOTIFICATIONS_CONTENT_TYPE;
import static br.brunodea.nevertoolate.db.NeverTooLateContract.NOTIFICATIONS_ID;

public class NeverTooLaterProvider extends ContentProvider {

    NeverTooLateDBHelper mDB;

    @Override
    public boolean onCreate() {
        mDB = new NeverTooLateDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NeverTooLateDBHelper.Favorites.TABLE_NAME);

        switch (NeverTooLateContract.URI_MATCHER.match(uri)) {
            case FAVORITES: {
                // do nothing
            } break;
            case FAVORITES_ID: {
                qb.appendWhere(NeverTooLateDBHelper.Favorites._ID + "=" + uri.getPathSegments().get(1));
            } break;
            case NOTIFICATIONS: {
                // do nothing
            } break;
            case NOTIFICATIONS_ID: {
                qb.appendWhere(NeverTooLateDBHelper.Notifications._ID + "=" + uri.getPathSegments().get(1));
            }
            default: {
                // unreachable!
            } break;
        }
        Cursor c = qb.query(mDB.getReadableDatabase(), projection, selection, selectionArgs,
                null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (NeverTooLateContract.URI_MATCHER.match(uri)) {
            case FAVORITES:
                return FAVORITES_CONTENT_TYPE;
            case FAVORITES_ID:
                return FAVORITES_CONTENT_ITEM_TYPE;
            case NOTIFICATIONS:
                return NOTIFICATIONS_CONTENT_TYPE;
            case NOTIFICATIONS_ID:
                return NOTIFICATIONS_CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowID = 0;
        switch (NeverTooLateContract.URI_MATCHER.match(uri)) {
            case FAVORITES:
            case FAVORITES_ID:
                rowID = mDB.getWritableDatabase().insert(NeverTooLateDBHelper.Favorites.TABLE_NAME, "", values);
                break;
            case NOTIFICATIONS:
            case NOTIFICATIONS_ID:
                rowID = mDB.getWritableDatabase().insert(NeverTooLateDBHelper.Notifications.TABLE_NAME, "", values);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (NeverTooLateContract.URI_MATCHER.match(uri)) {
            case FAVORITES: {
                count = mDB.getWritableDatabase().delete(NeverTooLateDBHelper.Favorites.TABLE_NAME, selection, selectionArgs);
            } break;
            case FAVORITES_ID: {
                String id = uri.getPathSegments().get(1);
                count = mDB.getWritableDatabase().delete(NeverTooLateDBHelper.Favorites.TABLE_NAME,
                        NeverTooLateDBHelper.Favorites._ID + "=" + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                            selectionArgs
                        );
            } break;
            case NOTIFICATIONS: {
                count = mDB.getWritableDatabase().delete(NeverTooLateDBHelper.Notifications.TABLE_NAME, selection, selectionArgs);
            } break;
            case NOTIFICATIONS_ID: {
                String id = uri.getPathSegments().get(1);
                count = mDB.getWritableDatabase().delete(NeverTooLateDBHelper.Notifications.TABLE_NAME,
                        NeverTooLateDBHelper.Notifications._ID + "=" + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs
                );
            } break;
            default:
                throw new IllegalArgumentException("Unknown UIR: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}