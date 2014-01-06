package mobile.app.dev;

import mobile.app.dev.ueb07.TodoReaderActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void uebung7(View view) {
		Intent intent = new Intent(this, TodoReaderActivity.class);
		startActivity(intent);
	}

}
