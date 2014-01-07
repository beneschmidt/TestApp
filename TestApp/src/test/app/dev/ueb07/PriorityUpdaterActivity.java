package test.app.dev.ueb07;

import java.sql.SQLException;

import test.app.dev.R;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PriorityUpdaterActivity extends Activity {

	private Priority priority;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_priority_updater);

		int selectedId = getIntent().getExtras().getInt(TodoReaderActivity.PRIORITY_KEY);
		Cursor cursor = getPriority(selectedId);
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(PriorityDBHelper.COL_ID));
			String name = cursor.getString(cursor.getColumnIndex(PriorityDBHelper.COL_NAME));
			priority = new Priority();
			priority.set_id(id);
			priority.setName(name);
		}
		((EditText) findViewById(R.id.priorityUpdaterName)).setText(priority.getName());
	}

	private Cursor getPriority(int selectedId) {
		Uri uri = ContentUris.withAppendedId(Priorities.CONTENT_URI, selectedId);
		Log.d("URI", "URI: " + uri);
		String[] projection = new String[] { PriorityDBHelper.COL_ID, PriorityDBHelper.COL_NAME };
		return getContentResolver().query(uri, projection, null, null, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public void save(View view) {
		try {
			String name = ((EditText) findViewById(R.id.priorityUpdaterName)).getText().toString();
			if (name == null || name.isEmpty())
				throw new EmptyException(getString(R.string.empty_title_not_possible));
			Uri uri = ContentUris.withAppendedId(Priorities.CONTENT_URI, priority.get_id());
			ContentValues contentValues = new ContentValues();
			contentValues.put(PriorityDBHelper.COL_NAME, name);
			getContentResolver().update(uri, contentValues, null, null);
			finish();
		} catch (EmptyException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public void delete(View view) {
		try {

			Log.d("DELETE_PRIORITY", "Versuche Prioritaet zu loeschen! " + priority);
			if (hasTodosWithPriority(priority.get_id()))
				throw new SQLException("Kann nicht loeschen, bestehende Referenzen!");
			Uri uri = ContentUris.withAppendedId(Priorities.CONTENT_URI, priority.get_id());
			getContentResolver().delete(uri, null, null);

			Log.d("DELETE_PRIORITY", "Loesche Priorität " + priority);
			finish();
		} catch (SQLException e) {
			Toast.makeText(this, R.string.priority_still_used, Toast.LENGTH_SHORT).show();
		}
	}

	private boolean hasTodosWithPriority(int priority_id) {
		Uri uri = Todos.CONTENT_URI;
		Log.d("URI", "URI: " + uri);
		String[] projection = new String[] { TodoDBHelper.COL_ID, TodoDBHelper.COL_TITLE, TodoDBHelper.COL_DATE, TodoDBHelper.COL_DESCRIPTION,
				TodoDBHelper.COL_PRIORITY_ID };
		String selection = TodoDBHelper.COL_PRIORITY_ID + "=?";
		String[] selectionArguments = new String[] { priority_id + "" };
		Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArguments, null);
		int i = 0;
		while (cursor.moveToNext()) {
			i++;
		}
		return i > 0;
	}
}
