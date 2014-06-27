package in.swifiic.teacher;

import in.swifiic.examapp.R;
import in.swifiic.examapp.SettingsActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TeacherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacher);

		Button bImport = (Button) findViewById(R.id.importButton);
		Button bEval = (Button) findViewById(R.id.evalButton);

		bImport.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(TeacherActivity.this,SendTest.class);
				startActivity(intent);
			}
		});

		bEval.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO activity for viewing/evaluating test responses
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			Intent selectedSettings = new Intent(this, SettingsActivity.class);
			startActivity(selectedSettings);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
/*	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
	}
	*/
}
