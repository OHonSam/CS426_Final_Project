package com.hfad.cs426_final_project.MainScreen.DoneSessionActivities;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.hfad.cs426_final_project.AppContext;
import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;
import com.hfad.cs426_final_project.CustomUIComponent.MyButton;
import com.hfad.cs426_final_project.DataStorage.Tag;
import com.hfad.cs426_final_project.MainScreen.Clock.Clock;
import com.hfad.cs426_final_project.MainScreen.Clock.ModePickerDialog;
import com.hfad.cs426_final_project.MainScreen.Clock.OnClockListener;
import com.hfad.cs426_final_project.MainScreen.Tag.TagAdapterSpinner;
import com.hfad.cs426_final_project.MainScreen.TimePickerDialog;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.StatisticScreen.StatisticScreenActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CongratulationScreenActivity extends AppCompatActivity {
    public static final String REWARDS = "REWARDS";
    private ClickableImageView btnBack;
    private ClickableImageView btnBreakSession;
    private ClickableImageView btnStatistics;
    private ClickableImageView btnShareSession;

    private MyButton btnClockModePicker;
    private ModePickerDialog modePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Spinner searchTagSpinner;
    private TextView tvSunDisplay;

    private AppContext appContext;
    private Clock clock;

    private ActivityResultLauncher<Intent> breakScreenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation_screen);
        appContext = AppContext.getInstance();
        clock = appContext.getCurrentClock();
        clock.disableDeepModeCount();
        setupUIReference();
        setupOnClickListener();
        setupSearchTag();
        setupTextDisplay();
        registerBreakScreenLauncher();
        showCongratulationDialog();
    }

    private void registerBreakScreenLauncher() {
        breakScreenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        focusAgain();
                    }
                    else if (result.getResultCode() == RESULT_CANCELED) {
                        finish();
                    }
                }
        );
    }

    private void setupTextDisplay() {
        tvSunDisplay.setText(String.valueOf(AppContext.getInstance().getCurrentUser().getSun()));
    }

    private void showCongratulationDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_congratulation, null);

        setupCongratulationDialogContent(dialogView);

        // Create a dialog using the AlertDialog.Builder
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Ensure the dialog is dismissible when tapping outside
        dialog.setCanceledOnTouchOutside(true);

        adjustDialogSizeOnShow(dialog);

        // Handle the button click to dismiss the dialog
        Button buttonOK = dialogView.findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    private void adjustDialogSizeOnShow(AlertDialog dialog) {
        // Resize the dialog programmatically if needed
        dialog.setOnShowListener(dialogInterface -> {
            // You can adjust the width and height as needed
            int dialogWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            Objects.requireNonNull(dialog.getWindow()).setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        });
    }

    private void setupCongratulationDialogContent(View dialogView) {
        int rewards = getIntent().getIntExtra(REWARDS, 0);
        TextView rewardsTextView = dialogView.findViewById(R.id.rewards);
        rewardsTextView.setText(String.valueOf(rewards));

        ImageView tree = dialogView.findViewById(R.id.tree);
        Glide.with(dialogView)
                .load(appContext.getCurrentUser().getUserSetting().getSelectedTree().getImgUri())
                .error(R.drawable.tree)
                .into(tree);
    }

    private void setupOnClickListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock.reset();
                finish();
            }
        });

        btnShareSession.setOnClickListener(new View.OnClickListener() {
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

        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CongratulationScreenActivity.this, StatisticScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnBreakSession.setOnClickListener(new View.OnClickListener() {
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
        btnBack = findViewById(R.id.back_button);
        btnBreakSession = findViewById(R.id.coffee);
        btnStatistics = findViewById(R.id.forest);
        btnShareSession = findViewById(R.id.share);
        timePickerDialog = new TimePickerDialog();
        btnClockModePicker = findViewById(R.id.clockMode);
        modePickerDialog = new ModePickerDialog();
        searchTagSpinner = findViewById(R.id.search_tag_spinner);
        tvSunDisplay = findViewById(R.id.tvSunDisplay);
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

    private void redirectToBreakScreenActivity(int breakTime, boolean isAutoStart) {
        Intent intent = new Intent(this,BreakScreenActivity.class);
        intent.putExtra(BreakScreenActivity.TIME_BREAK,breakTime);
        intent.putExtra(BreakScreenActivity.AUTO_START,isAutoStart);
        breakScreenLauncher.launch(intent);
    }

    private void focusAgain(){
        setResult(RESULT_OK);
        clock.reset();
        finish();
    }

    private void showDialogReassertCancelBreakSession() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_reassert_cancel_break_session, null);

        // Create a dialog using the AlertDialog.Builder
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Ensure the dialog is dismissible when tapping outside
        dialog.setCanceledOnTouchOutside(true);

        adjustDialogSizeOnShow(dialog);

        // Handle the button click to dismiss the dialog
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        // Handle the button click to focus again
        Button buttonFocus = dialogView.findViewById(R.id.buttonFocus);
        buttonFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock.disableBreakSessionCount();
                focusAgain();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void setupSearchTag() {
        List<Tag> tagList = AppContext.getInstance().getCurrentUser().getOwnTags();
        if (tagList.isEmpty()) {
            tagList.add(new Tag());
        }
        TagAdapterSpinner tagAdapterSpinner = new TagAdapterSpinner(this, R.layout.item_tag_selected, tagList);
        searchTagSpinner.setEnabled(false);
        searchTagSpinner.setClickable(false);
        searchTagSpinner.setAdapter(tagAdapterSpinner);
        updateTagDisplay();
    }

    private void updateTagDisplay() {
        TagAdapterSpinner tagAdapterSpinner = (TagAdapterSpinner) searchTagSpinner.getAdapter();
        int position = tagAdapterSpinner.getPosition(appContext.getCurrentUser().getFocusTag());
        searchTagSpinner.setSelection(position);
    }
}