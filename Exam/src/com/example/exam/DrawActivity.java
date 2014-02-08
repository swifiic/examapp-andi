package com.example.exam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * adapted from code available at mobile.tutsplus.com
 */
public class DrawActivity extends Activity implements OnClickListener {

	// custom drawing view
	private DrawingView drawView;
	// buttons
	private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
	// sizes
	private float smallBrush, mediumBrush, largeBrush;
	private String fullpath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide the status bar
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_draw);

		// get drawing view
		drawView = (DrawingView) findViewById(R.id.drawing);

		// get the question statement passed alongside intent
		TextView vQues = (TextView) findViewById(R.id.quesView);

		String ques = getIntent().getStringExtra("quesText");
		fullpath = getIntent().getStringExtra("fullpath");
		vQues.setText(ques);

		// get the palette and first color button
		LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
		currPaint = (ImageButton) paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(
				R.drawable.paint_pressed));

		// sizes from dimensions
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);

		// draw button
		drawBtn = (ImageButton) findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);

		// set initial size
		drawView.setBrushSize(mediumBrush);

		// erase button
		eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
		eraseBtn.setOnClickListener(this);

		// new button
		newBtn = (ImageButton) findViewById(R.id.new_btn);
		newBtn.setOnClickListener(this);

		// save button
		saveBtn = (ImageButton) findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.draw_screen, menu);
		return true;
	}

	// user clicked paint
	public void paintClicked(View view) {
		// use chosen color

		// set erase false
		drawView.setErase(false);
		drawView.setBrushSize(drawView.getLastBrushSize());

		if (view != currPaint) {
			ImageButton imgView = (ImageButton) view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			// update ui
			imgView.setImageDrawable(getResources().getDrawable(
					R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(
					R.drawable.paint));
			currPaint = (ImageButton) view;
		}
	}

	@Override
	public void onClick(View view) {

		if (view.getId() == R.id.draw_btn) {
			// draw button clicked
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Brush size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			// listen for clicks on size buttons
			ImageButton smallBtn = (ImageButton) brushDialog
					.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setErase(false);
					drawView.setBrushSize(smallBrush);
					drawView.setLastBrushSize(smallBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton mediumBtn = (ImageButton) brushDialog
					.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setErase(false);
					drawView.setBrushSize(mediumBrush);
					drawView.setLastBrushSize(mediumBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton largeBtn = (ImageButton) brushDialog
					.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setErase(false);
					drawView.setBrushSize(largeBrush);
					drawView.setLastBrushSize(largeBrush);
					brushDialog.dismiss();
				}
			});
			// show and wait for user interaction
			brushDialog.show();
		} else if (view.getId() == R.id.erase_btn) {
			// switch to erase - choose size
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Eraser size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			// size buttons
			ImageButton smallBtn = (ImageButton) brushDialog
					.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(smallBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton mediumBtn = (ImageButton) brushDialog
					.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(mediumBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton largeBtn = (ImageButton) brushDialog
					.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(largeBrush);
					brushDialog.dismiss();
				}
			});
			brushDialog.show();
		} else if (view.getId() == R.id.new_btn) {
			// new button
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("New drawing");
			newDialog
					.setMessage("Start new drawing (you will lose the current drawing)?");
			newDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							drawView.startNew();
							dialog.dismiss();
						}
					});
			newDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			newDialog.show();
		} else if (view.getId() == R.id.save_btn) {
			// save drawing
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save drawing?");
			saveDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// save drawing
							saveImg();
						}
					});
			saveDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			saveDialog.show();
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		// submit and save drawing, return to MCQ Questions
		case R.id.doneBtn:
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Confirm Exit");
			saveDialog
					.setMessage("Save drawing to device Gallery before Exit?");
			saveDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// XXX save drawing
							saveImg();
							setResult(1, null);
							finish();
						}
					});
			saveDialog.setNeutralButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Toast savedToast = Toast.makeText(
									getApplicationContext(),
									"Drawing not saved to Gallery",
									Toast.LENGTH_SHORT);
							savedToast.show();
							setResult(1, null);
							finish();
						}
					}

			);
			saveDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			saveDialog.show();
			return true;
			// Submit all the answers
		case R.id.subButton:
			new AlertDialog.Builder(DrawActivity.this)
					.setTitle("Confirm Submission")
					.setMessage("Are you sure you want to submit the answers?")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with submission
									saveImg();
									setResult(2, null);
									finish();
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

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onBackPressed() {
		new AlertDialog.Builder(DrawActivity.this)
				.setTitle("Exit")
				.setMessage(
						"Exit drawing question without saving?\n(Press Done to save and exit)")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// go back to MCQ questions section
								setResult(1, null);
								finish();
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
						}).setIcon(android.R.drawable.ic_dialog_info).show();
	}

	private void saveImg() {
		// enable drawing cache
		drawView.setDrawingCacheEnabled(true);
		// Grab a bitmap of the drawing on drawView
		Bitmap b = drawView.getDrawingCache();
		// copy the contents of the Bitmap into a Picture
		Picture pictureToSave = new Picture();
		// Canvas to copy the Bitmap into the Picture
		Canvas c = pictureToSave.beginRecording(b.getWidth(), b.getHeight());
		c.drawBitmap(b, 0, 0, new Paint());
		pictureToSave.endRecording();

		// Create a File object to write the Picture to
		File file = new File(fullpath + "draw_sol.png");
		try {
			file.createNewFile();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		// Write the contents of the Picture object to disk
		try {
			OutputStream os = new FileOutputStream(file);
			b.compress(Bitmap.CompressFormat.PNG, 80, os);
			os.flush();
			os.close();
			Toast.makeText(getApplicationContext(), "Successfully Submitted",
					Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException fnfe) {
			Toast.makeText(getApplicationContext(), "Could not save file",
					Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Could not create file for saving",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		drawView.destroyDrawingCache();
	}
}
