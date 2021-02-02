package nigeriandailies.com.ng.contentprovdemo;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import nigeriandailies.com.ng.contentprovdemo.data.NationContract.NationEntry;


public class NationListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter simpleCursorAdapter;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nations);

		getLoaderManager().initLoader(10, null, this);

		String[] from = { NationEntry.COLUMN_COUNTRY, NationEntry.COLUMN_CONTINENT };
		int[] to = { R.id.txvCountryName, R.id.txvContinentName };

		simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.nation_list_item, null, from, to, 0);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(simpleCursorAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cursor = (Cursor) parent.getItemAtPosition(position);

				int _id = cursor.getInt(cursor.getColumnIndex(NationEntry._ID));
				String country = cursor.getString(cursor.getColumnIndex(NationEntry.COLUMN_COUNTRY));
				String continent = cursor.getString(cursor.getColumnIndex(NationEntry.COLUMN_CONTINENT));

				Intent intent = new Intent(NationListActivity.this, NationEditActivity.class);
				intent.putExtra("_id", _id);
				intent.putExtra("country", country);
				intent.putExtra("continent", continent);
				startActivity(intent);
			}
		});


		FloatingActionButton fab =  findViewById(R.id.fabInsertData);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NationListActivity.this, NationEditActivity.class);
				intent.putExtra("_id", 0);
				startActivity(intent);
			}
		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		String[] projection = {
				NationEntry._ID,
				NationEntry.COLUMN_COUNTRY,
				NationEntry.COLUMN_CONTINENT
		};

		// returns a CursorLoader object that carries a Cursor Object.
		// The cursor Object contains all rows queried from database using ContentProvider.
		return new CursorLoader(this, NationEntry.CONTENT_URI, projection, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		simpleCursorAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		simpleCursorAdapter.swapCursor(null);
	}
}
