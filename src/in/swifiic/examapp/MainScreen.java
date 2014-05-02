package in.swifiic.examapp;

/**
 * @author aniket
 *
 */

import java.io.File;


import in.swifiic.android.app.lib.ui.SwifiicActivity;
import in.swifiic.examapp.R;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainScreen extends SwifiicActivity {
	final static String TAG = "MainScreen";
	boolean serviceStarted = false;
	//Button srvcControl;

	public MainScreen() {
		super();
		// This is a must for all applications - hook to get notification from GenericService
    	mDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("notification")) {
                	String payload= intent.getStringExtra("notification");
                	
                    Log.d(TAG, "Doing Nothing with incoming message: " + payload);
                    //Notification notif = Helper.parseNotification(payload);
                } else {
                    Log.d(TAG, "Broadcast Receiver ignoring message - no notification found");
                }
            }
        }; 

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// This is a must for all applications - hook to get notification from
		// GenericService

		
		setContentView(R.layout.activity_main_screen);

		File folderPath = new File(Environment.getExternalStorageDirectory() + "/Exam/");
		if(!folderPath.exists()) folderPath.mkdir();
		
		Button exam = (Button) findViewById(R.id.examButton);
		Button teacher = (Button) findViewById(R.id.teacherButton);

		exam.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this,in.swifiic.exam.LoginActivity.class);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			Intent selectedSettings = new Intent(this, SettingsActivity.class);
			startActivity(selectedSettings);
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
    }
}