package com.hfad.cs426_final_project;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.MainScreen.Clock.ModePickerDialog;
import com.hfad.cs426_final_project.MainScreen.Clock.OnClockListener;
import com.hfad.cs426_final_project.MainScreen.TimePickerDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CongratulationScreenActivity extends AppCompatActivity implements OnClockListener {
    ClickableImageView backButton;
    ClickableImageView breakSession;
    ClickableImageView forest;
    ClickableImageView shareSession;

    private MyButton btnClockModePicker;
    private ModePickerDialog modePickerDialog;
    private TimePickerDialog timePickerDialog;

    private ActivityResultLauncher<Intent> breakScreenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation_screen);
        setupUIReference();
        setupOnClickListener();
        breakScreenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Start the clock for a new session
                        Log.d("CongratulationScreenActivity","focusAgain triggered");
                        focusAgain();
                    }
                    else if (result.getResultCode() == RESULT_CANCELED) {
                        // Start the clock for a new session
                        Log.d("CongratulationScreenActivity","focusAgain triggered");
                        finish();
                    }
                }
        );
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
            public void onDismiss(TimePickerDialog timePickerDialog, int breakTime, boolean isBreak, boolean autoStartSession) {
                if(!isBreak){
                    showDialogReassertCancelBreakSession();
                }
                else {
                    redirectToBreakScreenActivity(breakTime,autoStartSession);
                    Log.d("CongratulationScreenActivity","redirectToBreakScreenActivity triggered");
                }
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

    @Override
    public void redirectToFailScreenActivity(String message) {
        return;
    }

    @Override
    public void redirectToCongratulationScreenActivity() {
        return;
    }

    private void redirectToBreakScreenActivity(int breakTime, boolean isAutoStart) {
        Intent intent = new Intent(this,BreakScreenActivity.class);
        intent.putExtra(BreakScreenActivity.TIME_BREAK,breakTime);
        intent.putExtra(BreakScreenActivity.AUTO_START,isAutoStart);
        breakScreenLauncher.launch(intent);
    }

    private void focusAgain(){
        setResult(RESULT_OK);
        finish();
    }

    private void showDialogReassertCancelBreakSession() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_reassert_cancel_break_session, null);

        // Create a dialog using the AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Create and show the dialog
        final AlertDialog dialog = builder.create();

        // Ensure the dialog is dismissible when tapping outside
        dialog.setCanceledOnTouchOutside(true);

        // Resize the dialog programmatically if needed
        dialog.setOnShowListener(dialogInterface -> {
            // You can adjust the width and height as needed
            int dialogWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            dialog.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        });

        // Handle the button click to dismiss the dialog
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        // Handle the button click to focus again
        Button buttonFocus = dialogView.findViewById(R.id.buttonFocus);
        buttonFocus.setOnClickListener(v -> focusAgain());

        // Show the dialog
        dialog.show();
    }
}