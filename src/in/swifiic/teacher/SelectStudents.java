package in.swifiic.teacher;

import in.swifiic.examapp.R;

import java.util.ArrayList;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SelectStudents extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	    ArrayAdapter<String> adapter;
	 
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_select_students);
	 
	        final ListView listView = (ListView) findViewById(R.id.list);
	        Button button = (Button) findViewById(R.id.selectButton);
	 
	        
	        //TODO Apr 21 '14: Final version should get student list from apphub
	        String[] students = {"aniket", "abhishek", "aniket2", "student4", "student5", "student6"};
	        adapter = new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_multiple_choice, students);
	        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        listView.setAdapter(adapter);
	 
	        //button = (Button) findViewById(R.id.selectButton);
	        button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					SparseBooleanArray checked = listView.getCheckedItemPositions();
			        ArrayList<String> selectedItems = new ArrayList<String>();
			        for (int i = 0; i < checked.size(); i++) {
			            // Item position in adapter
			            int position = checked.keyAt(i);
			            // Add sport if it is checked i.e.) == TRUE!
			            if (checked.valueAt(i))
			                selectedItems.add(adapter.getItem(position));
			        }
			 
			        String outputStrArr  = "";
			 
			        for (int i = 0; i < selectedItems.size(); i++) {
			        	if (outputStrArr.equals(""))
			            outputStrArr = selectedItems.get(i);
			        	else 
			        		outputStrArr = outputStrArr + "|" + selectedItems.get(i);
			        }
			 
			        Intent stdList = new Intent();
			        
			        // Create a bundle object
			        stdList.putExtra("selectedStudents", outputStrArr);
			        
			        setResult(RESULT_OK, stdList);
			        
			        finish();
			 
				}

			});
		}
	 
	}
