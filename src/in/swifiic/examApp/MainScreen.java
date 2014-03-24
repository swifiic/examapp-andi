package in.swifiic.examApp;

/**
 * @author aniket
 *
 */

import in.swifiic.examApp.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.WindowManager;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide the status bar
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main_screen);

		Button exam = (Button) findViewById(R.id.examButton);
		Button teacher = (Button) findViewById(R.id.teacherButton);

		exam.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this,
						in.swifiic.exam.LoginActivity.class);
				startActivity(intent);
			}
		});

		teacher.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this,
						in.swifiic.teacher.TeacherActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;

	}
}