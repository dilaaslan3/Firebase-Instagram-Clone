package com.dilaaslan.firebaseinstagram;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    EditText commentText;
    ImageView imageView4;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorageRef;
    Uri selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        commentText = (EditText) findViewById(R.id.commentText);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void chooseImage(View view) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    @Override //Kullanıcıdan erişim iznim var ise
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            //Eğer bir cevap verildi ise
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) { //Kullanıcı hem izni vermiş hem fotoğrafı seçmiş ve fotoğrafı yüklemeye hazır ise
            //Seçilen fotoğrafın datasını alıyorum
            selected = data.getData();
            //bitmap ile datayı image a çeviriyorum
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selected);
                imageView4.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void upload(View view) {
        //kaydedilen her bir fotoğrafın kendine özel bir ismi olması gerekiyor
        //rastgele stringler olacak uniqe id oluşturuyorum
        UUID uuidImage = UUID.randomUUID();
        String imageName = "images/" + uuidImage + ".jpg";
        StorageReference storageReference = mStorageRef.child(imageName);
        StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = storageReference.putFile(selected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {  //upload işlemi başarılı ise
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();


                //resmi kim upload edecek onu göstereceğimiz için tekrardan Authentication ile işlemler yapıyorum
                FirebaseUser user = mAuth.getCurrentUser();
                String userEmail = user.getEmail().toString();
                String userComment = commentText.getText().toString();

                UUID uuıd = UUID.randomUUID();
                String uuidString = uuıd.toString();


                //database e postları kaydediyorum
                // post içerisinde postu paylaşan lişinin emaili, yorumu ve paylaştığı resmin url bilgileri olacak
                myRef.child("Posts").child(uuidString).child("useremail").setValue(userEmail);
                myRef.child("Posts").child(uuidString).child("comment").setValue(userComment);
                myRef.child("Posts").child(uuidString).child("downloadurl").setValue(downloadUrl);

                Toast.makeText(getApplicationContext(), "Your post shared", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);


            }
        }).addOnFailureListener(new OnFailureListener() { //upload işlemi başarılı değilse
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });


    }
}
