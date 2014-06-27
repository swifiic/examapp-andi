package in.swifiic.teacher;

import in.swifiic.examapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of DatePickerDialog and return it
		TimePickerDialog dialog = new TimePickerDialog(getActivity(),
				(OnTimeSetListener) this, hour, minute, false);
		return dialog;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		String am_pm = "";
		int hour;

		Calendar calTime = Calendar.getInstance();
		calTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calTime.set(Calendar.MINUTE, minute);

		if (calTime.get(Calendar.AM_PM) == Calendar.AM)
			am_pm = "AM";
		else
			am_pm = "PM";

		// convert hours from 24-hr format to 12-hr format
		if (calTime.get(Calendar.HOUR) == 0)
			hour = 12;
		else
			hour = calTime.get(Calendar.HOUR);

		Calendar c = Calendar.getInstance();
		c.set(hour, minute);
		
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
		String time = sdf.format(c.getTime());
		
		((Button) getActivity().findViewById(R.id.selectTestTime)).setText(time + " " + am_pm);
	}
}
