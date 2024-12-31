package com.tckmpsi.objectdetectordemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView resultText;
    private Button cameraButton;
    private Button galleryButton;
    private Button detectButton;
    private Bitmap selectedImage;
    private Module module;

    // Normalization parameters
    private static final float[] MEAN = {0.485f, 0.456f, 0.406f};
    private static final float[] STD = {0.229f, 0.224f, 0.225f};

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    handleImageSelection(imageBitmap);
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        handleImageSelection(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        imageView = findViewById(R.id.image);
        resultText = findViewById(R.id.result_text);
        cameraButton = findViewById(R.id.camera_button);
        galleryButton = findViewById(R.id.button);
        detectButton = findViewById(R.id.detect);

        // Load PyTorch model
        try {
            module = Module.load(assetFilePath("inception_traced.pt"));
        } catch (IOException e) {
            resultText.setText("Error loading model: " + e.getMessage());
            return;
        }

        // Set click listeners
        cameraButton.setOnClickListener(v -> checkCameraPermission());
        galleryButton.setOnClickListener(v -> openGallery());
        detectButton.setOnClickListener(v -> classifyImage());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void handleImageSelection(Bitmap bitmap) {
        selectedImage = bitmap;
        imageView.setImageBitmap(bitmap);
        detectButton.setEnabled(true);
        resultText.setText("");
    }

    private void classifyImage() {
        if (selectedImage != null) {
            try {
                // Prepare input tensor
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(selectedImage, 300, 300, false);
                Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                        resizedBitmap,
                        MEAN,
                        STD
                );

                // Run inference
                Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

                // Print the raw output tensor (logits or probabilities)
                float[] scores = outputTensor.getDataAsFloatArray();
                Log.d("Model Output", "Raw model output: " + Arrays.toString(scores));

                // Apply softmax to logits (if necessary)
                float sumExp = 0;
                for (int i = 0; i < scores.length; i++) {
                    scores[i] = (float) Math.exp(scores[i]);
                    sumExp += scores[i];
                }
                for (int i = 0; i < scores.length; i++) {
                    scores[i] /= sumExp; // Normalize to get probabilities
                }

                // Get the prediction
                float maxScore = Float.NEGATIVE_INFINITY;
                int maxScoreIdx = -1;
                for (int i = 0; i < scores.length; i++) {
                    if (scores[i] > maxScore) {
                        maxScore = scores[i];
                        maxScoreIdx = i;
                    }
                }

                // Map prediction to class label
                List<String> classes = loadClasses();
                String className = maxScoreIdx < classes.size() ? classes.get(maxScoreIdx) : "Unknown";
                float confidence = maxScore * 100; // Multiply by 100 for percentage

                // Ensure confidence is between 0 and 100
                if (confidence > 100) {
                    confidence = 100;
                }

                resultText.setText(String.format("%s\n%.2f%%", className, confidence));
            } catch (Exception e) {
                resultText.setText("Error during classification: " + e.getMessage());
            }
        }
    }


    private String assetFilePath(String assetName) throws IOException {
        File file = new File(getApplicationContext().getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream input = getApplicationContext().getAssets().open(assetName)) {
            try (FileOutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            }
        }
        return file.getAbsolutePath();
    }

    private List<String> loadClasses() {
        List<String> classes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open("classes.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                classes.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }
}