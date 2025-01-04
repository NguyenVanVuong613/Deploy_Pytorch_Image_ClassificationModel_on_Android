package com.tckmpsi.objectdetectordemo.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.tckmpsi.objectdetectordemo.R;
import com.tckmpsi.objectdetectordemo.models.Disease;
import com.tckmpsi.objectdetectordemo.models.DiseaseDetail;
import com.tckmpsi.objectdetectordemo.network.NetworkClient;
import com.tckmpsi.objectdetectordemo.utils.ImageUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_PICK = 2;

    private ImageView imageView;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private Button classifyButton;
    private Spinner modelSpinner;
    private String currentModel;
    private String base64Image;

    // Define your models
    private final String[] models = {
            "inception_v3",
            "efficientnet_b0",
            "mobilenet_v2",
            "resnet50",
            "vgg16",
            "densenet121"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();
        // Setup model spinner
        setupModelSpinner();
        // Setup button click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        imageView = findViewById(R.id.image);
        resultTextView = findViewById(R.id.result_text);
        progressBar = findViewById(R.id.progressBar);
        classifyButton = findViewById(R.id.detect);
        modelSpinner = findViewById(R.id.model_spinner);

        // Initially disable the "Classify Image" button
        classifyButton.setEnabled(false);
    }

    private void setupModelSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                models
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(adapter);

        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentModel = models[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentModel = models[0]; // Default to first model
            }
        });

        // Set default model
        currentModel = models[0];
    }

    private void setupClickListeners() {
        Button cameraButton = findViewById(R.id.camera_button);
        Button galleryButton = findViewById(R.id.gallery_button);

        cameraButton.setOnClickListener(v -> openCamera());
        galleryButton.setOnClickListener(v -> openGallery());
        classifyButton.setOnClickListener(v -> classifyImage());
    }

    // Open camera to take picture
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // Handle camera image
                bitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == REQUEST_GALLERY_PICK && data != null) {
                // Handle gallery image
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // Set image to ImageView and process the image
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                base64Image = ImageUtils.convertBitmapToBase64(bitmap);
                classifyButton.setEnabled(true);
            }
        }
    }

    private void classifyImage() {
        progressBar.setVisibility(View.VISIBLE);
        sendImageDataToServer(base64Image, currentModel);
    }

    private void sendImageDataToServer(String base64Image, String modelName) {
        NetworkClient.sendImageData(base64Image, modelName, new NetworkClient.DiseaseCallback() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(Disease disease) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    StringBuilder resultText = new StringBuilder();
                    resultText.append("Model: ").append(currentModel)
                            .append("\nDisease: ").append(disease.getDisease())
                            .append("\nScore: ").append(String.format("%.2f", disease.getScore()));

                    if (disease.getDetail() != null) {
                        resultText.append("\n\nDetails:\n");
                        DiseaseDetail detail = disease.getDetail();

                        if (detail.getHac_to() > 0) {
                            resultText.append("Hac to: ").append(String.format("%.2f", detail.getHac_to())).append("\n");
                        }
                        if (detail.getVay() > 0) {
                            resultText.append("Vay: ").append(String.format("%.2f", detail.getVay())).append("\n");
                        }
                        if (detail.getDay() > 0) {
                            resultText.append("Day: ").append(String.format("%.2f", detail.getDay())).append("\n");
                        }
                        if (detail.getKhong_benh() > 0) {
                            resultText.append("Khong benh: ").append(String.format("%.2f", detail.getKhong_benh())).append("\n");
                        }
                        if (detail.getUng_thu() > 0) {
                            resultText.append("Ung thu: ").append(String.format("%.2f", detail.getUng_thu())).append("\n");
                        }
                        if (detail.getBenh_khac() > 0) {
                            resultText.append("Benh khac: ").append(String.format("%.2f", detail.getBenh_khac())).append("\n");
                        }
                    }

                    resultTextView.setText(resultText.toString());
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    resultTextView.setText("Error: " + errorMessage);
                });
            }
        });
    }
}