package test.app.dev.ueb07;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Todos implements BaseColumns {
	public static final String AUTHORITY = "de.htwds.mada.todo";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY+"/todo");
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.de.htwds.mada.todo";
	public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.de.htwds.mada.todo";
}