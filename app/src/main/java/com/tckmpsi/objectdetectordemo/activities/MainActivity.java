package com.tckmpsi.objectdetectordemo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.tckmpsi.objectdetectordemo.R;
import com.tckmpsi.objectdetectordemo.network.NetworkClient;
import com.tckmpsi.objectdetectordemo.utils.ImageUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private Button classifyButton;
    private String base64Image;
    private String modelName = "inception_v3"; // You can make this dynamic based on user input if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
        resultTextView = findViewById(R.id.result_text);
        progressBar = findViewById(R.id.progressBar);
        classifyButton = findViewById(R.id.detect); // This is the "Classify Image" button

        // Initially disable the "Classify Image" button
        classifyButton.setEnabled(false);

        // Open the camera when the "Take Picture" button is clicked
        Button cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> openCamera());

        // Handle "Classify Image" button click
        classifyButton.setOnClickListener(v -> classifyImage());
    }

    // Open camera to take picture
    private void openCamera() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);  // Display the image on ImageView

            // Convert the Bitmap to Base64
            base64Image = ImageUtils.convertBitmapToBase64(bitmap);

            // Enable the "Classify Image" button after the image is captured and processed
            classifyButton.setEnabled(true);
        }
    }

    // Method to handle "Classify Image" button click
    private void classifyImage() {
        // Show progress bar while sending data to the server
        progressBar.setVisibility(View.VISIBLE);

        // Send the Base64 image data to the server
        sendImageDataToServer(base64Image, modelName);
    }

    // Method to send image data to server using NetworkClient
    private void sendImageDataToServer(String base64Image, String modelName) {
        // Using the NetworkClient class to make the Retrofit call
        NetworkClient.sendImageData(base64Image, modelName, new NetworkClient.ResponseCallback() {
            @Override
            public void onSuccess() {
                // Hide progress bar after receiving the response
                progressBar.setVisibility(View.GONE);
                resultTextView.setText("Image classified successfully!");
            }

            @Override
            public void onFailure(String errorMessage) {
                // Hide progress bar after receiving the response
                progressBar.setVisibility(View.GONE);
                resultTextView.setText("Error: " + errorMessage);
            }
        });
    }
}