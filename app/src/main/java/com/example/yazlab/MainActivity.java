    package com.example.yazlab;

    import android.content.ContentValues;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.widget.Button;

    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.ml.vision.FirebaseVision;
    import com.google.firebase.ml.vision.common.FirebaseVisionImage;
    import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
    import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
    import com.google.firebase.storage.FileDownloadTask;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import com.google.firebase.storage.UploadTask;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.Toast;
    import java.io.File;
    import java.io.IOException;
    import java.util.List;

    public class MainActivity extends AppCompatActivity {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref=storage.getReference();
        int tut=0;  Bitmap bitmap;
        Uri imauri;     ImageView mImg;     Intent tutint=null;      Uri tuturi; String tuts="";
        File locfi=File.createTempFile("images","jpg");
        public MainActivity() throws IOException {
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            mImg=findViewById(R.id.image_view);
            Bitmap asd;
            Button but1 = findViewById(R.id.but1);
            but1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(Intent.ACTION_PICK);
                    i.setType("image/*");
                    startActivityForResult(i,1);
                }
            });

            Button but2 = findViewById(R.id.but2);
            but2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values=new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE,"New Pic");
                    values.put(MediaStore.Images.Media.DESCRIPTION,"From Cam");
                    imauri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                    Intent tak = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                    tak.putExtra(MediaStore.EXTRA_OUTPUT,imauri);
                    if (tak.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(tak,2);
                    }
                }
            });

            Button but3 = findViewById(R.id.but3);
            but3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tuturi != null) {
                        EditText edt=findViewById(R.id.editText2);
                        String yol=edt.getText().toString();
                        int asd=Integer.parseInt(yol); tuts=yol;
                        if(asd<1||asd>99)
                            Toast.makeText(MainActivity.this,"1 ile 100 arasında bir sıkıştırma değeri gir",Toast.LENGTH_LONG).show();
                        else{
                            String[] de=tuturi.getLastPathSegment().split("/");
                            String dee=de[de.length-1];
                            yol=yol+"-"+dee;
                        StorageReference refe=ref.child(yol);
                        refe.putFile(tuturi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(MainActivity.this,"Resim sıkıştırıldı",Toast.LENGTH_LONG).show();
                            }
                        });
                        }
                    }
                }
            });

            Button but4 = findViewById(R.id.but4);
            but4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StorageReference refin=ref.child("thumb_"+tuts);
                    refin.getFile(locfi).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap= BitmapFactory.decodeFile(locfi.getAbsolutePath());
                            mImg.setImageBitmap(bitmap);
                            Toast.makeText(MainActivity.this,"Resim yüklendi",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            Button but5 = findViewById(R.id.but5);
            but5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tut==2)
                    try {
                        bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imauri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FirebaseVisionImage image =FirebaseVisionImage.fromBitmap(bitmap);
                    //FirebaseVisionImage image =FirebaseVisionImage.fromFilePath(,imauri);
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                            .getCloudImageLabeler();
                    labeler.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionImageLabel> labels) { int a=0;
                                    for (FirebaseVisionImageLabel label: labels) { if(a>3) break;
                                        String text = label.getText();
                                        String entityId = label.getEntityId();
                                        float confidence = label.getConfidence();
                                        Toast.makeText(MainActivity.this,text,Toast.LENGTH_LONG).show();
                                        a++;
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
            });

        }

        protected void onActivityResult(int requestCode,int resultCode,Intent data){
            super.onActivityResult(requestCode,resultCode,data);
            if(requestCode==1 && resultCode==RESULT_OK){ tut=1;
                Uri urid=data.getData();
                mImg.setImageURI(urid);
                tuturi=urid;
                bitmap= BitmapFactory.decodeFile(urid.getLastPathSegment());

            }
            else if (requestCode == 2 && resultCode==RESULT_OK) { tut=2;
                mImg.setImageURI(imauri);
                Uri urid = imauri;
                tuturi=urid;
            }
        }

    }

