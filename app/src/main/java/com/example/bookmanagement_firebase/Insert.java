/*
 * Copyright 2021 MRIDUL VIJ. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2021 MRIDUL VIJ. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bookmanagement_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.CaseMap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.content.ContentValues.TAG;


public class Insert extends AppCompatActivity {

    EditText tTitle, tAuthor;
    ImageView tBookImage;
    Button badd,bchoose;
    DatabaseReference ref;
    StorageReference sRef;
    private StorageTask uploadTask;
    public Uri img;
    Book book= new Book();
    long id=0,idn=0;
    String max="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        tTitle = (EditText) findViewById(R.id.tTitle);
        tAuthor = (EditText) findViewById(R.id.tAuthor);
        tBookImage = (ImageView) findViewById(R.id.tBookImage);
        badd= (Button) findViewById(R.id.badd);
        bchoose=(Button) findViewById(R.id.bchoose);

        sRef= FirebaseStorage.getInstance().getReference("BookImages");

        bchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosefile();
            }
        });


        ref= FirebaseDatabase.getInstance().getReference().child("Book");



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    idn=(snapshot.getChildrenCount());

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    book=userSnapshot.getValue(Book.class);
                    if(Integer.parseInt(book.getId()) > Integer.parseInt(max))
                        max=book.getId();

                    }
                id=Integer.parseInt(max);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Insert.this, " Id cancel", Toast.LENGTH_LONG).show();
            }
        });
        badd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(uploadTask!=null && uploadTask.isInProgress()) {
                    Toast.makeText(Insert.this, " Upload in Progress", Toast.LENGTH_LONG).show();
                }
                else{

                   uploadfile();
                    Toast.makeText(Insert.this,"Data inserted successfully",Toast.LENGTH_LONG).show();
                }
                //book.setBookImage(tBookImage.gettext().toString().trim());

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            img=data.getData();

            tBookImage.setImageURI(img);
        }
    }

    private String gerExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap map= MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadfile(){
        if (sRef != null) {
            String imgubook = System.currentTimeMillis() + "." + gerExtension(img);
            sRef=FirebaseStorage.getInstance().getReference();
            final StorageReference storageReference = sRef
                    .child("/BookImages" + imgubook);


            uploadTask = storageReference.putFile(img)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {




                                        sRef.child("/BookImages" + imgubook).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {

                                                book= new Book(tTitle.getText().toString().trim(),tAuthor.getText().toString().trim(),task.getResult().toString());
                                             //   book.setBookImage(task.getResult().toString());
                                           //     book.setTitle(tTitle.getText().toString().trim());
                                            //    book.setAuthor(tAuthor.getText().toString().trim());
                                                book.setId(String.valueOf(id+1));
                                                ref.child(String.valueOf(id+1)).setValue(book);
                                                Toast.makeText(Insert.this, "Image Upload successful"  , Toast.LENGTH_LONG).show();



                                            }
                                        });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Insert.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                       public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Insert.this, "On Progress", Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
           Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void choosefile(){
        Intent i= new Intent();
        i.setType("image/'");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,1);
    }
}