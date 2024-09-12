package com.hfad.cs426_final_project;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hfad.cs426_final_project.CustomUIComponent.ClickableImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FailScreenActivity extends AppCompatActivity {

    public static final String TAG_WHY_TREE_WITHERED = "WHY_TREE_WITHERED_MESSAGE";

    ClickableImageView backButton;
    TextView whyTreeWitheredTextView;
    ClickableImageView focusAgain;
    ClickableImageView forest;
    ClickableImageView shareSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail_screen);
        setupUIReference();
        setupOnClickListener();
    }

    private void setupOnClickListener() {
        whyTreeWitheredTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogWhyTreeWhithered();
            }
        });

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

        focusAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void setupUIReference() {
        whyTreeWitheredTextView = findViewById(R.id.why_tree_withered);
        backButton = findViewById(R.id.back_button);
        focusAgain = findViewById(R.id.clock);
        forest = findViewById(R.id.forest);
        shareSession = findViewById(R.id.share);

    }

    private void showDialogWhyTreeWhithered() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_why_tree_whithered, null);

        TextView whyTreeWithered = dialogView.findViewById(R.id.textViewDescription);
        String message = getIntent().getStringExtra(TAG_WHY_TREE_WITHERED);
        if (message != null && !message.isEmpty()) {
            whyTreeWithered.setText(message);
        }
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
        Button buttonClose = dialogView.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
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

    private Uri saveScreenshotToMediaStore(Bitmap bitmap) {
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "screenshot_" + System.currentTimeMillis() + ".png");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            OutputStream outputStream = resolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageUri;
    }


    private void shareFocusSession() {
        // Take a screenshot
        Bitmap screenshot = takeScreenshot();

        // Save the screenshot using MediaStore
        Uri screenshotUri = saveScreenshot(screenshot);

        // Share the screenshot
        shareScreenshot(screenshotUri);
    }
}