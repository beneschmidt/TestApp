package mobile.app.dev.ueb07;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mobile.app.dev.R;
import mobile.app.dev.ueb06.orm.DatabaseHelper;
import mobile.app.dev.ueb06.orm.Priority;
import mobile.app.dev.ueb06.orm.Todo;
import mobile.app.dev.ueb06.orm.TodoDBHelper;
import mobile.app.dev.ueb06.view.EmptyException;
import mobile.app.dev.ueb07.TodoContentProvider.Todos;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class TodoUpdaterActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private Todo todo;
	private long selectedTimeInMillis;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_updater);
		int selectedId = getIntent().getExtras().getInt(TodoReaderActivity.TODO_KEY);
		Cursor cursor = getTodo(selectedId);
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(TodoDBHelper.COL_ID));
			String title = cursor.getString(cursor.getColumnIndex(TodoDBHelper.COL_TITLE));
			String description = cursor.getString(cursor.getColumnIndex(TodoDBHelper.COL_DESCRIPTION));
			int priorityId = cursor.getInt(cursor.getColumnIndex(TodoDBHelper.COL_PRIORITY_ID));
			long datetime = cursor.getLong(cursor.getColumnIndex(TodoDBHelper.COL_DATE));
			todo = new Todo();
			todo.set_id(id);
			todo.setTitle(title);
			todo.setDescription(description);
			todo.setDatetime(datetime);
			todo.setPriority(new Priority(priorityId, null));
		}
		((EditText) findViewById(R.id.todoUpdaterTitle)).setText(todo.getTitle());
		((EditText) findViewById(R.id.todoUpdaterDescription)).setText(todo.getDescription());
		selectedTimeInMillis = todo.getDatetime();
		setFormattedDate();
	}

	private Cursor getTodo(int selectedId) {
		Uri uri = ContentUris.withAppendedId(Todos.CONTENT_URI, selectedId);
		Log.d("URI", "URI: " + uri);
		String[] projection = new String[] { TodoDBHelper.COL_ID, TodoDBHelper.COL_TITLE, TodoDBHelper.COL_DATE, TodoDBHelper.COL_DESCRIPTION,
				TodoDBHelper.COL_PRIORITY_ID };
		return getContentResolver().query(uri, projection, null, null, null);
	}

	private void setFormattedDate() {
		String formattedDate = sdf.format(new Date(selectedTimeInMillis));
		Log.d("TEST", formattedDate);
		((TextView) findViewById(R.id.todoChosenDateLabel)).setText(formattedDate);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public void save(View view) {
		try {
			String title = ((EditText) findViewById(R.id.todoUpdaterTitle)).getText().toString();
			if (title == null || title.isEmpty())
				throw new EmptyException(getString(R.string.EMPTY_TITLE_NOT_POSSIBLE));
			ContentValues values = new ContentValues();
			values.put(TodoDBHelper.COL_TITLE, title);
			values.put(TodoDBHelper.COL_DESCRIPTION, ((EditText) findViewById(R.id.todoUpdaterDescription)).getText().toString());

			Uri uri = ContentUris.withAppendedId(Todos.CONTENT_URI, todo.get_id());
			getContentResolver().update(uri, values, null, null);
			finish();
		} catch (EmptyException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public void delete(View view) {
		if (todo != null) {
			Log.d("DELETE_PRIORITY", "Loesche Todo " + todo);
			Uri uri = ContentUris.withAppendedId(Todos.CONTENT_URI, todo.get_id());
			getContentResolver().delete(uri, null, null);
			finish();
		} else {
			finish();
		}
	}
}
