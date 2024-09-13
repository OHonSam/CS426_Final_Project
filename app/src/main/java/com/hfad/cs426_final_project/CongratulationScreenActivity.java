package com.hfad.cs426_final_project;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.transition.Visibility;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.MainScreen.Clock.ModePickerDialog;
import com.hfad.cs426_final_project.MainScreen.TimePickerDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;

public class CongratulationScreenActivity extends AppCompatActivity {
    ClickableImageView backButton;
    ClickableImageView breakSession;
    ClickableImageView forest;
    ClickableImageView shareSession;

    private MyButton btnClockModePicker;
    private ModePickerDialog modePickerDialog;
    private TimePickerDialog timePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation_screen);
        setupUIReference();
        setupOnClickListener();
    }

    private void setupOnClickListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shareSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use a Handler to delay the screenshot by 200ms
                v.setPressed(false); // Reset pressed state immediately
                v.invalidate(); // Redraw the button

                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareFocusSession();
                    }
                }, 200); // 200ms delay
            }
        });

//        focusAgain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setResult(RESULT_OK);
//                finish();
//            }
//        });

        breakSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        timePickerDialog.setOnDismissListener(new TimePickerDialog.onDismissListener() {
            @Override
            public void onDismiss(TimePickerDialog timePickerDialog, int breakTime, boolean isBreak) {

            }
        });

        btnClockModePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModePickerDialog();
            }
        });
    }

    private void setupUIReference() {
        backButton = findViewById(R.id.back_button);
        breakSession = findViewById(R.id.coffee);
        forest = findViewById(R.id.forest);
        shareSession = findViewById(R.id.share);
        timePickerDialog = new TimePickerDialog();
        btnClockModePicker = findViewById(R.id.clockMode);
        modePickerDialog = new ModePickerDialog();
    }

    private void shareFocusSession() {
        // Take a screenshot
        Bitmap screenshot = takeScreenshot();

        // Save the screenshot using MediaStore
        Uri screenshotUri = saveScreenshot(screenshot);

        // Share the screenshot
        shareScreenshot(screenshotUri);
    }

    private Bitmap takeScreenshot() {
        // Get the root view (decor view) including the background
        View rootView = getWindow().getDecorView();
        rootView.setDrawingCacheEnabled(true);

        // Capture the entire window content including the background
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        return bitmap;
    }

    private Uri saveScreenshot(Bitmap bitmap) {
        File imagePath = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the Uri for the saved image
        return FileProvider.getUriForFile(this, "com.yourpackage.fileprovider", imagePath);
    }

    private void shareScreenshot(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Screenshot"));
    }

    private void showTimePickerDialog() {
        if (getSupportFragmentManager().findFragmentByTag(TimePickerDialog.TAG) == null)
            timePickerDialog.show(getSupportFragmentManager(), TimePickerDialog.TAG);
    }

    private void showModePickerDialog() {
        if (getSupportFragmentManager().findFragmentByTag(ModePickerDialog.TAG) == null)
            modePickerDialog.show(getSupportFragmentManager(), ModePickerDialog.TAG);
    }
}