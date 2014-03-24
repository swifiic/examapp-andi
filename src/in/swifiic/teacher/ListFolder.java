package in.swifiic.teacher;

import in.swifiic.examApp.R;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat;

import android.os.Bundle;
import android.os.Environment;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

public class ListFolder extends ListActivity {

	private File currentDir;
	private FileArrayAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentDir = new File(Environment.getExternalStorageDirectory() + "/");
		fill(currentDir);
	}

	private void fill(File f) {
		File[] dirs = f.listFiles();
		this.setTitle("Current Dir: " + f.getName());
		List<Albumb> dir = new ArrayList<Albumb>();
		List<Albumb> fls = new ArrayList<Albumb>();
		try {
			for (File ff : dirs) {
				String name = ff.getName();
				Date lastModDate = new Date(ff.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);

				File[] fbuf = ff.listFiles();
				int buf = 0;
				if (fbuf != null) {
					buf = fbuf.length;
				} else
					buf = 0;
				String num_item = String.valueOf(buf);
				if (buf == 0)
					num_item = num_item + " item";
				else
					num_item = num_item + " items";

				// String formated = lastModDate.toString();
				dir.add(new Albumb(ff.getName(), num_item, date_modify, ff
						.getAbsolutePath(), "directory_icon"));
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard"))
			dir.add(0, new Albumb("..", "Parent Directory", "", f.getParent(),
					"directory_up"));
		adapter = new FileArrayAdapter(ListFolder.this, R.layout.file_view, dir);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Albumb o = adapter.getItem(position);
		if (o.getImage().equalsIgnoreCase("directory_icon")
				|| o.getImage().equalsIgnoreCase("directory_up")) {
			currentDir = new File(o.getPath());
			fill(currentDir);
		}
	}

	public void onBackPressed() {
  		Albumb o = adapter.getItem(0);
		currentDir = new File(o.getPath());
		fill(currentDir);
	}
}