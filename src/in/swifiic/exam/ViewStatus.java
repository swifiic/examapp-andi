package in.swifiic.exam;

import in.swifiic.examApp.R;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ViewStatus extends Activity {

	private boolean forReview[];
	private int ans[];
	private int totalQues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide the status bar and back/home buttons
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_view_status);

		Bundle vs = getIntent().getExtras();
		forReview = vs.getBooleanArray("rev");
		ans = vs.getIntArray("ans");
		totalQues = getIntent().getIntExtra("totalQues", 10);

		// XXX
		LinearLayout linear = (LinearLayout) findViewById(R.id.linLayout);

		LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);

		Button[] btn = new Button[totalQues];
		for (int i = 0; i < totalQues; i++) {
			btn[i] = new Button(getApplicationContext());
			btn[i].setText(Integer.toString(i + 1));
			btn[i].setTextColor(Color.parseColor("#000000"));
			// btn[i].setTextSize(20);
			if (ans[i] != 0) {
				btn[i].setBackgroundColor(0xFFFFCC00);
			}
			if (forReview[i]) {
				btn[i].setBackgroundColor(0xFF00FFFF);
			}
			if (ans[i] == -1) {
				btn[i].setBackgroundColor(0xFFBDA0CB);
			}
			btn[i].setHeight(70);
			btn[i].setWidth(100);
			btn[i].setLayoutParams(param);
			btn[i].setPadding(5, 15, 5, 15);
			linear.addView(btn[i], param);
			btn[i].setTag(i);

			btn[i].setOnClickListener(handleOnClick(btn[i]));

		}
	}

	View.OnClickListener handleOnClick(final Button button) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				int ob = (Integer) v.getTag();
				setResult(ob);
				finish();
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_status, menu);
		return true;
	}

}
