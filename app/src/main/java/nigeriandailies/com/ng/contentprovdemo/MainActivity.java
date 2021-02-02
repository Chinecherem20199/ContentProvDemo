package nigeriandailies.com.ng.contentprovdemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import nigeriandailies.com.ng.contentprovdemo.data.NationContract;
import nigeriandailies.com.ng.contentprovdemo.data.NationDbHelper;


/**
 * 	Module4: Implementing ContentProvider with It’s Insert and Query Operations
 *	Author: Sriyank Siddhartha
 *
 *		"AFTER" project
 * */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private EditText etCountry, etContinent, etWhereToUpdate, etNewContinent, etWhereToDelete, etQueryRowById;
	private Button btnInsert, btnUpdate, btnDelete, btnQueryRowById, btnDisplayAll;

	private static final String TAG = MainActivity.class.getSimpleName();

//	private SQLiteDatabase database;
//	private NationDbHelper databaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etCountry 		= (EditText) findViewById(R.id.etCountry);
		etContinent 	= (EditText) findViewById(R.id.etContinent);
		etWhereToUpdate = (EditText) findViewById(R.id.etWhereToUpdate);
		etNewContinent 	= (EditText) findViewById(R.id.etUpdateContinent);
		etQueryRowById 	= (EditText) findViewById(R.id.etQueryByRowId);
		etWhereToDelete = (EditText) findViewById(R.id.etWhereToDelete);

		btnInsert 		= (Button) findViewById(R.id.btnInsert);
		btnUpdate 		= (Button) findViewById(R.id.btnUpdate);
		btnDelete 		= (Button) findViewById(R.id.btnDelete);
		btnQueryRowById = (Button) findViewById(R.id.btnQueryByID);
		btnDisplayAll 	= (Button) findViewById(R.id.btnDisplayAll);

		btnInsert.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnQueryRowById.setOnClickListener(this);
		btnDisplayAll.setOnClickListener(this);

//		databaseHelper = new NationDbHelper(this);
//		database = databaseHelper.getWritableDatabase();		// READ/WRITE
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.btnInsert:
				insert();
				break;

			case R.id.btnUpdate:
				update();
				break;

			case R.id.btnDelete:
				delete();
				break;

			case R.id.btnQueryByID:
				queryRowById();
				break;

			case R.id.btnDisplayAll:
				queryAndDisplayAll();
				break;
		}
	}

	private void insert() {

		String countryName 	= etCountry.getText().toString();
		String continentName= etContinent.getText().toString();

		ContentValues contentValues = new ContentValues();
		contentValues.put(NationContract.NationEntry.COLUMN_COUNTRY, countryName);
		contentValues.put(NationContract.NationEntry.COLUMN_CONTINENT, continentName);

		Uri uri = NationContract.NationEntry.CONTENT_URI;
		Uri uriRowInserted = getContentResolver().insert(uri, contentValues);
		Log.i(TAG, "Items inserted at: " + uriRowInserted);
	}

	private void update() {

		String whereCountry = etWhereToUpdate.getText().toString();
		String newContinent = etNewContinent.getText().toString();

		String selection = NationContract.NationEntry.COLUMN_COUNTRY + " = ?";
		String[] selectionArgs = { whereCountry };			// WHERE country = ? = Japan

		ContentValues contentValues = new ContentValues();
		contentValues.put(NationContract.NationEntry.COLUMN_CONTINENT, newContinent);


		Uri uri = NationContract.NationEntry.CONTENT_URI;			Log.i(TAG, ""+uri);
		int rowsUpdated = getContentResolver().update(uri, contentValues, selection, selectionArgs);
		Log.i(TAG, "Number of rows updated: " + rowsUpdated);
	}

	private void delete() {

		String countryName = etWhereToDelete.getText().toString();

		String selection = NationContract.NationEntry.COLUMN_COUNTRY + " = ? ";
		String[] selectionArgs = { countryName };		// WHERE country = "Japan"

		Uri uri = Uri.withAppendedPath(NationContract.NationEntry.CONTENT_URI, countryName);		Log.i(TAG, ""+uri);
		int rowsDeleted = getContentResolver().delete(uri, selection, selectionArgs);
		Log.i(TAG, "Number of rows deleted: " + rowsDeleted);
	}

	private void queryRowById() {

		String rowId = etQueryRowById.getText().toString();

		String[] projection = {
				NationContract.NationEntry._ID,
				NationContract.NationEntry.COLUMN_COUNTRY,
				NationContract.NationEntry.COLUMN_CONTINENT
		};

		// Filter results. Make these null if you want to query all rows
		String selection = NationContract.NationEntry._ID + " = ? ";	// _id = ?
		String[] selectionArgs = { rowId };				// Replace '?' by rowId in runtime

		String sortOrder = null;	// Ascending or Descending ...

		Uri uri = Uri.withAppendedPath(NationContract.NationEntry.CONTENT_URI, rowId);		Log.i(TAG, ""+uri);
		Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

		if (cursor != null && cursor.moveToNext()) {

			String str = "";

			String[] columns = cursor.getColumnNames();
			for (String column : columns) {
				str += "\t" + cursor.getString(cursor.getColumnIndex(column));
			}
			str += "\n";

			cursor.close();
			Log.i(TAG, str);
		}
	}

	private void queryAndDisplayAll() {
		Intent intent = new Intent(MainActivity.this, NationListActivity.class);
		startActivity(intent);

		//Code snipet to query and display items in logCat

		String[] projection = {
				NationContract.NationEntry._ID,
				NationContract.NationEntry.COLUMN_COUNTRY,
				NationContract.NationEntry.COLUMN_CONTINENT
		};

		// Filter results. Make these null if you want to query all rows
		String selection = null;
		String[] selectionArgs = null;

		String sortOrder = null;	// Ascending or Descending ...

		Uri uri = NationContract.NationEntry.CONTENT_URI;			Log.i(TAG, ""+uri);
		Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

		if (cursor != null) {

			String str = "";
			while (cursor.moveToNext()) {	// Cursor iterates through all rows

				String[] columns = cursor.getColumnNames();
				for (String column : columns) {
					str += "\t" + cursor.getString(cursor.getColumnIndex(column));
				}
				str += "\n";
			}

			cursor.close();
			Log.i(TAG, str);
		}
	}

//	@Override
//	protected void onDestroy() {
//		database.close();	// Close Database Connection
//		super.onDestroy();
//	}
}
