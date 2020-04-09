package com.example.lecture7march;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UploadImage extends AppCompatActivity {
    private ImageView imageToUploadIV;
    private EditText imageNameET;

    private Button imageUploadingBtn;
    private static final int REQUEST_CODE=124;

    private Uri imageDataInUriForm;
    private StorageReference objectStorageReference;
    private String currentDate;

    private FirebaseFirestore objectFirebaseFirestore;
    private boolean isImageSelected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        objectStorageReference = FirebaseStorage.getInstance().getReference("Gallery");
        setContentView(R.layout.activity_upload_image);
        connectXMLToJava();


        Calendar calendar=Calendar.getInstance();
        currentDate= DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        imageNameET.setText(currentDate);
    }


    private void connectXMLToJava() {
        try {
            imageToUploadIV = findViewById(R.id.imageToUploadIV);
            imageNameET = findViewById(R.id.imageNameET);

            imageUploadingBtn = findViewById(R.id.imageUploadingBtn);
            imageToUploadIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGallery();
                }
            });
            imageUploadingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadOurImage();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "connectXMLToJava:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        try {
            Intent objectIntent = new Intent();
            objectIntent.setAction(Intent.ACTION_GET_CONTENT);

            objectIntent.setType("image/*");
            startActivityForResult(objectIntent,REQUEST_CODE);

        } catch (Exception e) {
            Toast.makeText(this, "openGallery:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null)
            {
                imageDataInUriForm=data.getData();
                Bitmap objectBitmap;

                objectBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageDataInUriForm);
                imageToUploadIV.setImageBitmap(objectBitmap);

                isImageSelected=true;

            }
            else if(requestCode!=REQUEST_CODE)
            {
                Toast.makeText(this, "Request code doesn't match", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode!=RESULT_OK)
            {
                Toast.makeText(this, "Fails to get image", Toast.LENGTH_SHORT).show();
            }
            else if(data==null)
            {
                Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
            }


        }
        catch (Exception e) {
            Toast.makeText(this, "onActivityResult:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadOurImage()
    {
        try
        {
            if (imageDataInUriForm != null && !imageNameET.getText().toString().isEmpty()
                    && isImageSelected) {
            //    bar.setVisibility(View.VISIBLE);
                //yourName.jpeg


                String imageName = imageNameET.getText().toString()+  "." + getExtension(imageDataInUriForm);

                //FirebaseStorage/BSCSAImagesFolder/yourName.jpeg
                final StorageReference actualImageRef = objectStorageReference.child(imageName);

                UploadTask uploadTask = actualImageRef.putFile(imageDataInUriForm);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                       //     bar.setVisibility(View.INVISIBLE);
                            throw task.getException();
                        }
                        return actualImageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String url = task.getResult().toString();
                            Map<String, Object> objectMap = new HashMap<>();

                            objectMap.put("URL", url);
                            objectFirebaseFirestore.collection("Gallery")
                                    .document(imageNameET.getText().toString())
                                    .set(objectMap)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(UploadImage.this, "Fails To Upload Image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            imageNameET.setText("");
                                            imageToUploadIV.setVisibility(View.INVISIBLE);
                                       //     bar.setVisibility(View.INVISIBLE);
                                         //   gallery.setVisibility(View.VISIBLE);
                                            Toast.makeText(UploadImage.this, "Image Successfully Uploaded: ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                   //     bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadImage.this, "Fails To Upload Image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (imageDataInUriForm == null) {
                Toast.makeText(this, "No Image Is Selected", Toast.LENGTH_SHORT).show();
            } else if (imageNameET.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please First You Need To Enter An Image Name", Toast.LENGTH_SHORT).show();
                imageNameET.requestFocus();
            } else if (!isImageSelected) {
                Toast.makeText(this, "Please Select An Image", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "uploadOurImage:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri imageDataInUriForm)
    {
        try
        {
            ContentResolver objectContentResolver=getContentResolver();
            MimeTypeMap objectMimeTypeMap=MimeTypeMap.getSingleton();

            String extension=objectMimeTypeMap.getExtensionFromMimeType(objectContentResolver.getType(imageDataInUriForm));
            return extension;
        }
        catch (Exception e)
        {
            Toast.makeText(this, "getExtension:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return "";
    }

    public  void BackBtn(View v)
    {
        try {
            startActivity(new Intent(this, MainPage.class));
            finish();
        }
        catch (Exception e)
        {
            Toast.makeText(UploadImage.this,"Back Button"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
