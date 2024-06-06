package com.example.exmap;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.exmap.databinding.ActivityTransactionPageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class TransactionPage extends AppCompatActivity {

    String[] cameraPermissions;
    private static final int CAMERA_REQUEST_CODE = 100;
    ActivityTransactionPageBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference dr;
    TransactionModel transactionModel;
    Uri imgUri = null;
    String receiptUrl;
    String idTrans;
    TextRecognizer textRecognizer;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        idTrans = firebaseUser.getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("receipt/" + imgUri);
        transactionModel = (TransactionModel) getIntent().getSerializableExtra("transaction");
        idTrans = getIntent().getStringExtra("id");

        cameraPermissions = new String[] {android.Manifest.permission.CAMERA};
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        /* Menu Navigation */
        binding.homeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(TransactionPage.this, HomePage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                }
            }
        });

        binding.historyMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(TransactionPage.this, HistoryPage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                }
            }
        });

        binding.settingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(TransactionPage.this, SettingPage.class));
                    overridePendingTransition(0, 0);
                } catch (Exception e) {
                }
            }
        });

        /* Transaction Form */
        binding.expenseCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Expense";
                binding.expenseCb.setChecked(true);
                binding.earnCb.setChecked(false);
            }
        });

        binding.earnCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Earning";
                binding.earnCb.setChecked(true);
                binding.expenseCb.setChecked(false);
            }
        });

        binding.cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputImageDialog();
            }
        });

        binding.receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        binding.textCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUri == null){
                    Toast.makeText(TransactionPage.this, "Pick Image First", Toast.LENGTH_SHORT).show();
                } else {
                    recognizeText();
                }
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountTrans = binding.amount.getText().toString().trim();
                String category = binding.categList.getText().toString().trim();
                String note = binding.notes.getText().toString().trim();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/dd");
                Calendar calendar = Calendar.getInstance();
                String currentDate = dateFormat.format(calendar.getTime());

                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentTime = timestamp.format(date);

                if (amountTrans.length() <= 0) {
                    return;
                }
                if (type.length() <= 0) {
                    Toast.makeText(TransactionPage.this, "Choose expense or earning box", Toast.LENGTH_SHORT).show();
                }

                toStorageFirebase();

                String id = UUID.randomUUID().toString();

                Map<String, Object> trans = new HashMap<>();
                trans.put("id", id);
                trans.put("amountTrans", amountTrans);
                trans.put("category", category);
                trans.put("note", note);
                trans.put("type", type);
                trans.put("date", currentDate);
                trans.put("receipt", imgUri);
                trans.put("time", currentTime);

                firestore.collection("record").document(firebaseAuth.getUid())
                        .collection("transaction")
                        .add(trans)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(TransactionPage.this, "Add successfully", Toast.LENGTH_SHORT).show();
                                binding.amount.setText("");
                                startActivity(new Intent(TransactionPage.this, HistoryPage.class));
                                overridePendingTransition(0, 0);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TransactionPage.this, "Try again, the data not added", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    /* Hide Status Bar & Navigation Bar */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void recognizeText() {
        try {
            InputImage inputImage = InputImage.fromFilePath(TransactionPage.this, imgUri);
            Task<Text> result = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            String recognizeText = text.getText();
                            binding.textShow.setText(recognizeText);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TransactionPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showInputImageDialog() {
        PopupMenu popupMenu = new PopupMenu(this, binding.cam);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "CAMERA");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "GALLERY");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id==1){
                    if (checkCameraPermission()) {
                        pickImageCamera();
                    } else {
                        requestCamera();
                    }
                } else if (id == 2) {
                    pickImage();
                }
                return false;
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.launch(intent);
    }

    ActivityResultLauncher<Intent> gallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imgUri = data.getData();
                        binding.receipt.setImageURI(imgUri);
                    } else {
                        Toast.makeText(TransactionPage.this, "Cancelled....", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void pickImageCamera(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title1");
        imgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
//        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        camera.launch(intent);
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CAMERA_REQUEST_CODE) {
//            Bitmap imgUri = (Bitmap) data.getExtras().get("data");
//            binding.receipt.setImageBitmap(imgUri);
//        }
//    }

    ActivityResultLauncher<Intent> camera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        binding.receipt.setImageURI(imgUri);
                    } else {
                        Toast.makeText(TransactionPage.this, "Cancelled....", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    boolean checkCameraPermission(){
        boolean camresult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return camresult;
    }

    void toStorageFirebase(){
        if (imgUri != null) {
            storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            receiptUrl = uri.toString();
                            Toast.makeText(TransactionPage.this, "Success Upload", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void requestCamera(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0 ){
                    boolean camAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(camAccept){
                        pickImageCamera();
                    } else {
                        Toast.makeText(this, "Camera are required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            } break;
        }
    }
}