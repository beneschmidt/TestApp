package mobile.app.dev.ueb07;

import java.io.Serializable;

import mobile.app.dev.ueb06.orm.AbstractDBHelper;
import mobile.app.dev.ueb06.orm.DatabaseHelper;
import mobile.app.dev.ueb06.orm.PriorityDBHelper;
import mobile.app.dev.ueb06.orm.TodoDBHelper;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class TodoContentProvider extends ContentProvider {

	private static final int PRIORITY_ID = 111;
	private static final int PRIORITIES = 110;
	private static final int TODO_ID = 101;
	private static final int TODOS = 100;
	public static final String SCHEME = "content://";
	/** Paketstruktur scheint Pflicht für die Abgabe zu sein, muss aber nur nach aussen hin so aussehen */
	public static final String AUTHORITY = "de.htwds.mada.todo";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		// Todo-URIs
		URI_MATCHER.addURI(AUTHORITY, Todos.PATH, TODOS);
		URI_MATCHER.addURI(AUTHORITY, Todos.PATH + "/#", TODO_ID);

		// Priority-URIs
		URI_MATCHER.addURI(AUTHORITY, Priorities.PATH, PRIORITIES);
		URI_MATCHER.addURI(AUTHORITY, Priorities.PATH + "/#", PRIORITY_ID);
	}

	private TodoDBHelper todoDBHelper;
	private PriorityDBHelper priorityDBHelper;

	@Override
	public boolean onCreate() {
		todoDBHelper = new TodoDBHelper();
		priorityDBHelper = new PriorityDBHelper();
		return false;
	}

	@Override
	public String getType(Uri uri) {
		int uriCode = URI_MATCHER.match(uri);
		switch (uriCode) {
			case TODOS:
				return Todos.CONTENT_TYPE;
			case TODO_ID:
				return Todos.CONTENT_TYPE_ITEM;
			case PRIORITIES:
				return Priorities.CONTENT_TYPE;
			case PRIORITY_ID:
				return Priorities.CONTENT_TYPE_ITEM;
			default:
				return null;
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriCode = URI_MATCHER.match(uri);
		switch (uriCode) {
			case TODOS: {
				SelectionArguments args = new SelectionArguments(DatabaseHelper.TODO_TABLE)
						.addSelection(selection)
						.setSelectionArguments(selectionArgs);
				return delete(todoDBHelper, args);
			}
			case TODO_ID: {
				String id = uri.getLastPathSegment();
				SelectionArguments args = new SelectionArguments(DatabaseHelper.TODO_TABLE)
						.addSelection(TodoDBHelper.COL_ID + "=" + id)
						.addSelection(selection)
						.setSelectionArguments(selectionArgs);
				return delete(todoDBHelper, args);
			}
			case PRIORITIES: {
				SelectionArguments args = new SelectionArguments(DatabaseHelper.PRIORITY_TABLE)
						.addSelection(selection)
						.setSelectionArguments(selectionArgs);
				return delete(priorityDBHelper, args);
			}
			case PRIORITY_ID: {
				String id = uri.getLastPathSegment();
				SelectionArguments args = new SelectionArguments(DatabaseHelper.PRIORITY_TABLE)
						.addSelection(PriorityDBHelper.COL_ID + "=" + id)
						.addSelection(selection)
						.setSelectionArguments(selectionArgs);
				return delete(priorityDBHelper, args);
			}
			default: {
				return 0;
			}
		}
	}

	private <T extends Serializable> int delete(AbstractDBHelper<T> dbHelper, SelectionArguments args) {
		SQLiteDatabase sqlDB = dbHelper.getHelper(getContext()).getWritableDatabase();
		int rowsDeleted = sqlDB.delete(args.getTableName(), args.getSelection(), args.getSelectionArguments());
		return rowsDeleted;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriCode = URI_MATCHER.match(uri);
		switch (uriCode) {
			case TODOS:
				long newTodoId = insertData(values, todoDBHelper, DatabaseHelper.TODO_TABLE);
				// URI für neues Todo erstellen und zurückgeben 
				return ContentUris.appendId(uri.buildUpon(), newTodoId).build();
			case PRIORITIES:
				long newPriorityId = insertData(values, priorityDBHelper, DatabaseHelper.PRIORITY_TABLE);
				// URI für neues Todo erstellen und zurückgeben 
				return ContentUris.appendId(uri.buildUpon(), newPriorityId).build();
			default:
				return null;
		}
	}

	/**
	 * Fügt einen Datensatz hinzu und gibt die eingefügte ID zurück.
	 * @param values
	 * @param dbHelper
	 * @return die eingefügte ID
	 */
	private <T extends Serializable> long insertData(ContentValues values, AbstractDBHelper<T> dbHelper, String table) {
		SQLiteDatabase sqlDB = dbHelper.getHelper(getContext()).getWritableDatabase();
		long id = sqlDB.insert(table, null, values);
		return id;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int uriCode = URI_MATCHER.match(uri);
		switch (uriCode) {
			case TODOS: {
				SelectionArguments arguments = new SelectionArguments(DatabaseHelper.TODO_TABLE)
						.setProjection(projection).addSelection(selection).setSelectionArguments(selectionArgs).setSortOrder(sortOrder);
				return getAll(arguments, todoDBHelper);
			}
			case TODO_ID: {
				long todoId = ContentUris.parseId(uri);
				String idSelection = TodoDBHelper.COL_ID + "=" + todoId;
				SelectionArguments arguments = new SelectionArguments(DatabaseHelper.TODO_TABLE)
						.setProjection(projection)
						.addSelection(selection).addSelection(idSelection)
						.setSelectionArguments(selectionArgs)
						.setSortOrder(sortOrder);
				return getAll(arguments, todoDBHelper);
			}
			case PRIORITIES: {
				SelectionArguments arguments = new SelectionArguments(DatabaseHelper.PRIORITY_TABLE)
						.setProjection(projection).addSelection(selection).setSelectionArguments(selectionArgs).setSortOrder(sortOrder);
				return getAll(arguments, priorityDBHelper);
			}
			case PRIORITY_ID: {
				long priorityId = ContentUris.parseId(uri);
				String idSelection = PriorityDBHelper.COL_ID + "=" + priorityId;
				SelectionArguments arguments = new SelectionArguments(DatabaseHelper.PRIORITY_TABLE)
						.setProjection(projection)
						.addSelection(selection).addSelection(idSelection)
						.setSelectionArguments(selectionArgs)
						.setSortOrder(sortOrder);
				return getAll(arguments, priorityDBHelper);
			}
			default: {
				return null;
			}
		}
	}

	private <T extends Serializable> Cursor getAll(SelectionArguments arguments, AbstractDBHelper<T> dbHelper) {
		try {
			SQLiteDatabase database = dbHelper.getHelper(getContext()).getReadableDatabase();
			return database.query(arguments.getTableName(), arguments.getProjection(), arguments.getSelection(), arguments.getSelectionArguments(), null, null,
					arguments.getSortOrder());
		} catch (Exception e) {
			Log.e("CONTENT_PROVIDER", "error querying database", e);
			return null;
		} 
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriCode = URI_MATCHER.match(uri);
		switch (uriCode) {
			case TODOS: {
				SelectionArguments arguments = new SelectionArguments(DatabaseHelper.TODO_TABLE)
						.setValues(values)
						.addSelection(selection)
						.setSelectionArguments(selectionArgs);
				return updateData(todoDBHelper, arguments);
			}
			case TODO_ID: {
				String id = uri.getLastPathSegment();
				SelectionArguments arguments = new SelectionArguments(DatabaseHelper.TODO_TABLE)
						.addSelection(TodoDBHelper.COL_ID + "=" + id)
						.addSelection(selection)
						.setSelectionArguments(selectionArgs)
						.setValues(values);
				return updateData(todoDBHelper, arguments);
			}
			case PRIORITIES: {
				SelectionArguments arguments = new SelectionArguments(DatabaseHelper.PRIORITY_TABLE)
						.setValues(values)
						.addSelection(selection)
						.setSelectionArguments(selectionArgs);
				return updateData(priorityDBHelper, arguments);
			}
			case PRIORITY_ID: {
				String id = uri.getLastPathSegment();
				SelectionArguments arguments = new SelectionArguments(DatabaseHelper.PRIORITY_TABLE)
						.addSelection(TodoDBHelper.COL_ID + "=" + id)
						.addSelection(selection)
						.setSelectionArguments(selectionArgs)
						.setValues(values);
				return updateData(priorityDBHelper, arguments);
			}
			default: {
				return 0;
			}
		}
	}

	private <T extends Serializable> int updateData(AbstractDBHelper<T> dbHelper, SelectionArguments arguments) {
		SQLiteDatabase sqlDB = dbHelper.getHelper(getContext()).getWritableDatabase();
		int rowsUpdated = sqlDB.update(arguments.getTableName(),
				arguments.getValues(),
				arguments.getSelection(),
				arguments.getSelectionArguments());
		return rowsUpdated;
	}

	public static final class Todos implements BaseColumns {
		public static final String PATH = "todo";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(TodoContentProvider.CONTENT_URI, PATH);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.de.htwds.mada.todo";
		public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.de.htwds.mada.todo";
	}

	public static final class Priorities implements BaseColumns {
		public static final String PATH = "priority";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(TodoContentProvider.CONTENT_URI, PATH);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.de.htwds.mada.priority";
		public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.de.htwds.mada.priority";
	}

}
