package com.example.tareafirebaseml;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";
    TextView lblResult;
    Button btnElegirImg;
    private static final int STORAGE_PERMISSION_CODE=113;

    ActivityResultLauncher<Intent> intentActivityResultLauncher;

    InputImage inputImage;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblResult=findViewById(R.id.lblResult);
        btnElegirImg=findViewById(R.id.btnElegirImg);

        textRecognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        intentActivityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data=result.getData();
                        Uri imageUri=data.getData();
                        
                        convertImageToText(imageUri);
                    }
                }
        );


        btnElegirImg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intentActivityResultLauncher.launch(intent);

            }
        });
    }

    private void convertImageToText(Uri imageUri) {
        //Prepare the input image
        try{
            inputImage=InputImage.fromFilePath(getApplicationContext(),imageUri);
            //obtener el texto de la imagen de entrada
            Task<Text> result=textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(@NonNull Text text) {
                            lblResult.setText(text.getText());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            lblResult.setText("Error: "+e.getMessage());
                            Log.d(TAG, "Error: "+e.getMessage());
                        }
                    });
        }catch (Exception e){
            Log.d(TAG, "convertImageToText: Error: "+e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
    }

    public void checkPermission(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(MainActivity.this, permission)== PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
//        else {
//            Toast.makeText(MainActivity.this, "Permission already Granted", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"Storage permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this,"Storage permission Granted", Toast.LENGTH_SHORT).show();
            }
        }

    }
}