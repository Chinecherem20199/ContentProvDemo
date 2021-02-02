package nigeriandailies.com.ng.contentprovdemo.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import nigeriandailies.com.ng.contentprovdemo.data.NationContract.NationEntry;

import static nigeriandailies.com.ng.contentprovdemo.data.NationContract.CONTENT_AUTHORITY;
import static nigeriandailies.com.ng.contentprovdemo.data.NationContract.NationEntry.TABLE_NAME;
import static nigeriandailies.com.ng.contentprovdemo.data.NationContract.PATH_COUNTRIES;


public class NationProvider extends ContentProvider {

	private static final String TAG = NationProvider.class.getSimpleName();

	private NationDbHelper databaseHelper;

	//constants for the operation
	private static final int COUNTRIES = 1;				// For whole table
	private static final int COUNTRIES_ID = 2;			// For a specific row in a table identified by _ID
	private static final int COUNTRIES_COUNTRY_NAME = 3;// For a specific row in a table identified by COUNTRY NAME

	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		uriMatcher.addURI(CONTENT_AUTHORITY, PATH_COUNTRIES, COUNTRIES);
		uriMatcher.addURI(CONTENT_AUTHORITY, PATH_COUNTRIES + "/#", COUNTRIES_ID);
		uriMatcher.addURI(CONTENT_AUTHORITY, PATH_COUNTRIES + "/*", COUNTRIES_COUNTRY_NAME);
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new NationDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteDatabase database = databaseHelper.getReadableDatabase();
		Cursor cursor;

		switch (uriMatcher.match(uri)) {

			case COUNTRIES:
				cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
				break;

			case COUNTRIES_ID:
				selection = NationEntry._ID + " = ?";
				selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
				cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
				break;

			default:
				throw new IllegalArgumentException(TAG + "Unknown URI: " + uri);
		}
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		switch (uriMatcher.match(uri)) {

			case COUNTRIES:
				return insertRecord(uri, values, TABLE_NAME);
			default:
				throw new IllegalArgumentException(TAG + "Unknown URI: " + uri);
		}
	}

	private Uri insertRecord(Uri uri, ContentValues values, String tableName) {

		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		long rowId = database.insert(tableName, null, values);

		if (rowId == -1) {
			Log.e(TAG, "Insert error for URI " + uri);
			return null;
		}else {
			getContext().getContentResolver().notifyChange(uri, null);

		}

		return ContentUris.withAppendedId(uri, rowId);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		switch (uriMatcher.match(uri)) {
			case COUNTRIES:			// To delete whole table
				return deleteRecord(uri, null, null, NationEntry.TABLE_NAME);

			case COUNTRIES_ID:		// To delete a row by ID
				selection = NationEntry._ID + " = ?";
				selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
				return deleteRecord(uri, selection, selectionArgs, NationEntry.TABLE_NAME);
				
			case COUNTRIES_COUNTRY_NAME:	// To Delete a row by COUNTRY NAME
				selection = NationEntry.COLUMN_COUNTRY + " = ? ";
				selectionArgs = new String[] { String.valueOf(uri.getLastPathSegment()) };
				return deleteRecord(uri, selection, selectionArgs, NationEntry.TABLE_NAME);
			default:
				throw new IllegalArgumentException(TAG + "Unknown URI: " + uri);
		}
	}

	private int deleteRecord(Uri uri, String selection, String[] selectionArgs, String tableName) {

		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int rowsDeleted = database.delete(tableName, selection, selectionArgs);

		if (rowsDeleted != 0){
			getContext().getContentResolver().notifyChange(uri, null);

		}
		return rowsDeleted;
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		switch (uriMatcher.match(uri)) {

			case COUNTRIES: 	// For Countries Table
				return updateRecord(uri, values, selection, selectionArgs, NationEntry.TABLE_NAME);
			default:
				throw new IllegalArgumentException(TAG + "Unknown URI: " + uri);
		}
	}

	private int updateRecord(Uri uri, ContentValues values, String selection, String[] selectionArgs, String tableName) {

		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int rowsUpdated = database.update(tableName, values, selection, selectionArgs);
		if (rowsUpdated != 0){
			getContext().getContentResolver().notifyChange(uri, null);

		}
		return rowsUpdated;
	}
}
