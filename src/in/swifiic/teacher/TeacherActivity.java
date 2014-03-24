package in.swifiic.teacher;

import in.swifiic.examApp.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
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
				// TODO activity for importing test
				Intent intent = new Intent(TeacherActivity.this,
						ListFolder.class);
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
		getMenuInflater().inflate(R.menu.teacher, menu);
		return true;
	}
}
