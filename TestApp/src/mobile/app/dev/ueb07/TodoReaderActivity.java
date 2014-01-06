package mobile.app.dev.ueb07;

import java.util.LinkedList;
import java.util.List;

import mobile.app.dev.R;
import mobile.app.dev.ueb06.orm.Priority;
import mobile.app.dev.ueb06.orm.PriorityDBHelper;
import mobile.app.dev.ueb06.orm.Todo;
import mobile.app.dev.ueb06.orm.TodoDBHelper;
import mobile.app.dev.ueb07.TodoContentProvider.Priorities;
import mobile.app.dev.ueb07.TodoContentProvider.Todos;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;

public class TodoReaderActivity extends ListActivity implements OnCheckedChangeListener {

	public static final String TODO_KEY = "todo";
	public static final String PRIORITY_KEY = "priority";
	/** Zuständig für die Auswahl Todo/Priority zur Anzeige */
	private Switch todoSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_reader);
		todoSwitch = (Switch) findViewById(R.id.todoSwitch);
		todoSwitch.setOnCheckedChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadListWithEntries();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		loadListWithEntries();
	}

	/**
	 * Lädt die Liste neu, entsprechend dem Switch-Status
	 */
	private void loadListWithEntries() {
		boolean on = todoSwitch.isChecked();
		if (on) {
			loadTodos();
		} else {
			loadPriorites();
		}
	}

	private void loadTodos() {
		Cursor cursor = getTodos();

		List<Todo> list = new LinkedList<Todo>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(TodoDBHelper.COL_ID));
			String title = cursor.getString(cursor.getColumnIndex(TodoDBHelper.COL_TITLE));
			String description = cursor.getString(cursor.getColumnIndex(TodoDBHelper.COL_DESCRIPTION));
			int priorityId = cursor.getInt(cursor.getColumnIndex(TodoDBHelper.COL_PRIORITY_ID));
			Todo todo = new Todo();
			todo.set_id(id);
			todo.setTitle(title);
			todo.setDescription(description);
			todo.setPriority(new Priority(priorityId, null));
			list.add(todo);
		}

		ArrayAdapter<Todo> adapter = new ArrayAdapter<Todo>(this, android.R.layout.simple_list_item_1, list);
		setListAdapter(adapter);
	}

	private Cursor getTodos() {
		Uri uri = Todos.CONTENT_URI;
		Log.d("URI", "URI: " + uri);
		String[] projection = new String[] { TodoDBHelper.COL_ID, TodoDBHelper.COL_TITLE, TodoDBHelper.COL_DATE, TodoDBHelper.COL_DESCRIPTION,
				TodoDBHelper.COL_PRIORITY_ID };
		return getContentResolver().query(uri, projection, null, null, null);
	}

	private void loadPriorites() {
		Cursor cursor = getPriorites();

		List<Priority> list = new LinkedList<Priority>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(PriorityDBHelper.COL_ID));
			String name = cursor.getString(cursor.getColumnIndex(PriorityDBHelper.COL_NAME));
			Priority priority = new Priority();
			priority.set_id(id);
			priority.setName(name);
			list.add(priority);
		}

		ArrayAdapter<Priority> adapter = new ArrayAdapter<Priority>(this, android.R.layout.simple_list_item_1, list);
		setListAdapter(adapter);
	}

	private Cursor getPriorites() {
		Uri uri = Priorities.CONTENT_URI;
		Log.d("URI", "URI: " + uri);
		String[] projection = new String[] { PriorityDBHelper.COL_ID, PriorityDBHelper.COL_NAME };

		return getContentResolver().query(uri, projection, null, null, null);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		if (todoSwitch.isChecked()) {
			Intent intent = new Intent(this, TodoUpdaterActivity.class);
			Log.d("ON_CLICK_TODO", "Position: " + position);
			intent.putExtra(TODO_KEY, ((Todo) listView.getItemAtPosition(position)).get_id());
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, PriorityUpdaterActivity.class);
			Log.d("ON_CLICK_PRIORITY", "Position: " + position);
			intent.putExtra(PRIORITY_KEY, ((Priority) listView.getItemAtPosition(position)).get_id());
			startActivity(intent);
		}
	}

}
