package in.swifiic.exam;

/**
 * @author aniket
 *
 */


import java.io.*;

import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import in.swifiic.examapp.R;
import in.swifiic.examapp.AddFolder;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

//TODO add functionality for displaying subjective questions

public class Questions extends Activity {

	// null namespace string for matching the tags of xml file
	private String ns = null;

	// constant for setting the total no. of questions in the test
	private int noOfQues;

	final static int noOfOpt = 4; // no. of options in MCQ questions

	private String path; // name of the test file location

	private String courseCode;

	private String idNo; // stores Id no. for creating zip file of that name

	private int timerMin; // stores total duration of test in minutes

	// string arrays for storing parsed questions data
	private String quesText[];
	private String optionText[][];
	private String img[];// stores name of the image file, if any
	private int ans[]; // stores chosen options
	private boolean forReview[];

	private int rand; // for storing random seed to shuffle order of options

	private int parsedQuesCnt = 0; // stores the count of the parsed questions
	private int displayCnt = 0; // stores the index of the currently displayed
								// question

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide the status bar and back/home buttons
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_questions);

		final Button vNext = (Button) findViewById(R.id.nextButton);
		final Button vPrev = (Button) findViewById(R.id.prevButton);
		final Button vclear = (Button) findViewById(R.id.clrButton);
		final RadioGroup optionGroup = (RadioGroup) findViewById(R.id.optionGroup);
		final CheckBox reviewCheck = (CheckBox) findViewById(R.id.reviewCheck);

		/*
		 * read xml file containing questions from sdcard typical
		 * location:\Exam\<test_filename>
		 */
		XmlPullParserFactory factory = null;
		try {
			factory = XmlPullParserFactory.newInstance();
		} catch (XmlPullParserException e1) {
			Toast.makeText(getApplicationContext(),
					"Unable to initialize parser", Toast.LENGTH_SHORT).show();
			e1.printStackTrace();
		}
		factory.setNamespaceAware(true);
		XmlPullParser parser = null;
		try {
			parser = factory.newPullParser();
		} catch (XmlPullParserException e1) {
			Toast.makeText(getApplicationContext(),
					"Error initializing XML parser", Toast.LENGTH_SHORT).show();
			e1.printStackTrace();
		}
		path = getIntent().getStringExtra("path");
		courseCode = getIntent().getStringExtra("crsCode");
		idNo = getIntent().getStringExtra("idNo");

		FileInputStream in = null;
		try {
			File file = new File(path + "/" + courseCode + "/questions.dat");
			in = new FileInputStream(file);
			File solDir = new File(path + courseCode + "/Solution");
			solDir.mkdir();
		} catch (FileNotFoundException e1) {
			Toast.makeText(getApplicationContext(),
					"Unable to open Questions.dat", Toast.LENGTH_SHORT).show();
			Intent loginUnsuccessful = new Intent(Questions.this,
					LoginActivity.class);
			startActivity(loginUnsuccessful);
			finish();
			// e1.printStackTrace();
		}
		try {
			parser.setInput(new InputStreamReader(in));
		} catch (XmlPullParserException e1) {
			Toast.makeText(getApplicationContext(),
					"Unable to parse test file using XML parser",
					Toast.LENGTH_SHORT).show();
			finish();
			e1.printStackTrace();
		}

		try {
			parse(in);
		} catch (XmlPullParserException e) {
			Toast.makeText(getApplicationContext(),
					"Unable to parse test file using XML parser",
					Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Unhandled IO exception",
					Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}

		// randomize the display array to randomize order of questions
		Random random = new Random();
		rand = random.nextInt(4);

		// set the inital display and start the timer
		vPrev.setEnabled(false);
		vNext.setEnabled(true);
		displayQues();
		startTimer();

		// onclick action for nextButton
		vNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (displayCnt < parsedQuesCnt - 1) {
					ans[displayCnt] = optionGroup.getCheckedRadioButtonId();

					if (reviewCheck.isChecked())
						forReview[displayCnt] = true;
					else
						forReview[displayCnt] = false;
					displayCnt++;
					displayQues();
				}
			}
		});

		// onclick action for prevButton
		vPrev.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (displayCnt > 0) {
					ans[displayCnt] = optionGroup.getCheckedRadioButtonId();

					if (reviewCheck.isChecked())
						forReview[displayCnt] = true;
					else
						forReview[displayCnt] = false;

					displayCnt--;
					displayQues();
				}
			}
		});

		// onclick action for clearButton - clears the currently selected answer
		vclear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				optionGroup.clearCheck();
				ans[displayCnt] = 0;
			}
		});

		optionGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (displayCnt > 0) {
					ans[displayCnt] = optionGroup.getCheckedRadioButtonId();
				}
			}
		});

		// XXX
		reviewCheck
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						forReview[displayCnt] = reviewCheck.isChecked();

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.questions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		// for submitting the answers
		case R.id.subButton:
			new AlertDialog.Builder(Questions.this)
					.setTitle("Confirm Submission")
					.setMessage("Are you sure you want to submit the answers?")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with submission
									submit();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Toast.makeText(getApplicationContext(),
											"Submission Cancelled",
											Toast.LENGTH_SHORT).show();
								}
							}).setIcon(android.R.drawable.ic_dialog_info)
					.show();
			return true;
			// XXX
		case R.id.statButton: {
			Intent vs = new Intent(Questions.this, ViewStatus.class);
			vs.putExtra("totalQues", noOfQues);
			Bundle b1 = new Bundle();
			b1.putBooleanArray("rev", forReview);
			b1.putIntArray("ans", ans);
			vs.putExtras(b1);
			startActivityForResult(vs, 2);

		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// prevents users from exiting the activity by pressing back button
	public void onBackPressed() {
		new AlertDialog.Builder(Questions.this)
				.setTitle("Test In Progress!")
				.setMessage(
						"You cannot go back while the test is in progress.\n"
								+ "If you want to submit the answers, press Submit.")
				.setPositiveButton(android.R.string.ok, null)
				.setIcon(android.R.drawable.ic_dialog_info).show();

	}

	private void parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			readFeed(parser);
		} finally {
			in.close();
		}
	}

	private void readFeed(XmlPullParser parser) throws XmlPullParserException,
			IOException {

		parser.require(XmlPullParser.START_TAG, ns, "test");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("timer")) {
				timerMin = (Integer.parseInt(readTag(parser, "timer")));
				// looking for the question tag
			} else if (name.equals("totalQuestions")) { // total no. of
														// questions
				noOfQues = (Integer.parseInt(readTag(parser, "totalQuestions")));
				quesText = new String[noOfQues];
				optionText = new String[noOfQues][noOfOpt];
				img = new String[noOfQues]; // stores name of the image
											// file, if any
				ans = new int[noOfQues]; // stores chosen options
				forReview = new boolean[noOfQues];
			} else if (name.equals("question")) { // looking for the question
													// tag
				readQuestion(parser);
			} else {
				skip(parser);
			}
		}
	}

	/*
	 * Parses the contents of an question. If it encounters a statement or an
	 * options tag, it calls the respective "read" methods for processing. Else
	 * skips the tag.
	 */

	private void readQuestion(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		int count = 0;
		parser.require(XmlPullParser.START_TAG, ns, "question");

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("statement")) {
				quesText[parsedQuesCnt] = readTag(parser, "statement");
			} else if (name.equals("image")) {
				img[parsedQuesCnt] = readTag(parser, "image");
			} else if (name.equals("option")) {
				switch (count) {
				case 0:
					optionText[parsedQuesCnt][count] = readTag(parser, "option");
					count++;
					break;
				case 1:
					optionText[parsedQuesCnt][count] = readTag(parser, "option");
					count++;
					break;
				case 2:
					optionText[parsedQuesCnt][count] = readTag(parser, "option");
					count++;
					break;
				case 3:
					optionText[parsedQuesCnt][count] = readTag(parser, "option");
					count = 0;
					// img[parsedQuesCnt] = null;
					parsedQuesCnt++;
					break;
				default:
					skip(parser);
				}
			} else if (name.equals("draw")) {
				ans[parsedQuesCnt] = -1;
				parsedQuesCnt++;
			} else
				skip(parser);
		}
	}

	// Processes the tags in the xml
	private String readTag(XmlPullParser parser, String tag)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String tagText = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return tagText;
	}

	// extracts the text between the tags
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	// helper method to skip unwanted tags
	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		/*
		 * depth counter keeping count of tags in case of nested tags ensures
		 * the method exits at the correct corresponding end tag
		 */
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	/*
	 * displays the question statements and options, along with image, if any.
	 * If draw tag is specified, then calls DrawActivity to provide a canvas for
	 * drawing
	 */
	private void displayQues() {
		// setting TextViews for labeling Questions and choices
		final TextView viewQues = (TextView) findViewById(R.id.quesText);
		final RadioButton vOption1 = (RadioButton) findViewById(R.id.option1);
		final RadioButton vOption2 = (RadioButton) findViewById(R.id.option2);
		final RadioButton vOption3 = (RadioButton) findViewById(R.id.option3);
		final RadioButton vOption4 = (RadioButton) findViewById(R.id.option4);
		final RadioGroup optionGroup = (RadioGroup) findViewById(R.id.optionGroup);
		final CheckBox reviewCheck = (CheckBox) findViewById(R.id.reviewCheck);
		final Button vNext = (Button) findViewById(R.id.nextButton);
		final Button vPrev = (Button) findViewById(R.id.prevButton);
		final ImageView qpic = (ImageView) findViewById(R.id.imageView);
		int rbIndex = 0; // for setting random option index

		if (displayCnt == parsedQuesCnt - 1) {
			vNext.setEnabled(false);
			vPrev.setEnabled(true);
		} else if (displayCnt == 0) {
			vPrev.setEnabled(false);
			vNext.setEnabled(true);
		} else {
			vPrev.setEnabled(true);
			vNext.setEnabled(true);
		}

		if (-1 == ans[displayCnt]) {
			Intent intent = new Intent(Questions.this, DrawActivity.class);
			intent.putExtra("quesText", quesText[displayCnt]);
			intent.putExtra("fullpath", path + courseCode + "/Solution/");
			startActivityForResult(intent, 1);
		} else {
			viewQues.setText((displayCnt + 1) + ") " + quesText[displayCnt]);

			if (!(img[displayCnt] == null)) {
				qpic.setVisibility(View.VISIBLE);
				Bitmap bmp = BitmapFactory.decodeFile(path + "/" + courseCode
						+ "/" + img[displayCnt]);
				qpic.setImageBitmap(bmp);
			} else {
				qpic.setVisibility(View.GONE);
			}
			rbIndex = rand;
			vOption1.setText(optionText[displayCnt][rbIndex]);
			rbIndex = randOpt(1);
			vOption2.setText(optionText[displayCnt][rbIndex]);
			rbIndex = randOpt(2);
			vOption3.setText(optionText[displayCnt][rbIndex]);
			rbIndex = randOpt(3);
			vOption4.setText(optionText[displayCnt][rbIndex]);
			optionGroup.check(ans[displayCnt]);
			if (forReview[displayCnt])
				reviewCheck.setChecked(true);
			else
				reviewCheck.setChecked(false);
		}
	}

	private int randOpt(int in) {
		if (in + rand <= 3)
			return in += rand;
		else
			return in += rand - 4;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == 1) { // back to objective ques
				displayCnt--;
				displayQues();
			} else if (resultCode == 2) { // User has submitted
				submit();
			}
		} else if (requestCode == 2) {
			displayCnt = resultCode;
			displayQues();
		}
	}

	/**
	 * Writes all answers to a text file in external memory, and compresses it
	 * to a password protected zip file. Name format is:
	 * <IdNo><course_code>.zip; Password is IdNo.
	 */
	private void submit() {
		String option, wStr;
		int i, opt; // opt for getting actual option selected
		if (isExternalStorageWritable()) {
			try {
				// create a directory 'Solution' which will have the solutions
				// files for packaging into a zip archive

				File solFile = new File(path + courseCode + "/Solution/"
						+ "soln.txt");
				solFile.createNewFile();
				FileOutputStream fOut = new FileOutputStream(solFile);
				OutputStreamWriter solWriter = new OutputStreamWriter(fOut);
				for (i = 0; i < parsedQuesCnt; i++) {
					try {
						option = getResources().getResourceEntryName(ans[i]);
					} catch (NotFoundException e) {
						option = "";
					}
					wStr = (i + 1) + " ";
					// get selected option and resolve it to original value
					if (option.equals("option1")) {
						opt = 0;
						opt = randOpt(opt);
					} else if (option.equals("option2")) {
						opt = 1;
						opt = randOpt(opt);
					} else if (option.equals("option3")) {
						opt = 2;
						opt = randOpt(opt);
					} else if (option.equals("option4")) {
						opt = 3;
						opt = randOpt(opt);
					} else {
						opt = -1;
					}

					switch (opt) {
					case 0:
						wStr = wStr + "a";
						break;
					case 1:
						wStr = wStr + "b";
						break;
					case 2:
						wStr = wStr + "c";
						break;
					case 3:
						wStr = wStr + "d";
						break;
					default:
						wStr = wStr + "-";
					}
					solWriter.append(wStr + "\n");

				}
				solWriter.close();
				fOut.close();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Unable to open file for writing Solution",
						Toast.LENGTH_SHORT).show();
			}
			@SuppressWarnings("unused")
			AddFolder zipSol = new AddFolder(path + courseCode + "/Solution",
					path + courseCode + "/", courseCode + idNo, idNo);
			
			//TODO convert created zip file into string and send it
			Intent sendSln = new Intent(Questions.this, SendSoln.class);
			sendSln.putExtra("path", path + courseCode + "/");
			sendSln.putExtra("fName", courseCode + idNo);
			startActivity(sendSln);
			finish();
		} else
			Toast.makeText(getApplicationContext(),
					"External Storage busy, cannot save file",
					Toast.LENGTH_SHORT).show();
	}

	// Checks if external storage is available for read and write
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	// implements the timer functionality
	private void startTimer() {
		final TextView viewTimer = (TextView) findViewById(R.id.timer);
		long totalTimeCountInSeconds = (int) timerMin * 60; // total count down
															// time
		// in seconds

		@SuppressWarnings("unused")
		CountDownTimer countDownTimer = new CountDownTimer(
				totalTimeCountInSeconds * 1000, 1000) {

			@Override
			public void onTick(long leftTimeInMilliseconds) {
				long seconds = leftTimeInMilliseconds / 1000;
				viewTimer.setText(String.format("%02d:%02d:%02d",
						seconds / 3600, (seconds % 3600) / 60, (seconds % 60)));
				// format the textview to show the easily readable format

			}

			// this function is called when the timecount is finished
			@Override
			public void onFinish() {
				viewTimer.setText("Time up!");
				viewTimer.setVisibility(View.VISIBLE);
				/*
				 * new AlertDialog.Builder(Questions.this) .setTitle("Time Up!")
				 * .setMessage("Press OK to submit answers")
				 * .setPositiveButton(android.R.string.ok, new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int which) {
				 */// continue with submission
				submit();
				/*
				 * } }).setIcon(android.R.drawable.ic_dialog_info) .show();
				 */}

		}.start();
	}
}
