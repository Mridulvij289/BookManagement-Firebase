package com.example.bookmanagement_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BookView extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference ref;

    ArrayList<Book> bookList;
    private RecyclerAdapter recyclerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);


        recyclerView=findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        ref= FirebaseDatabase.getInstance().getReference();

        bookList = new ArrayList<>();

        ClearAll();

        GetDataFromFirebase();


    }

    private void GetDataFromFirebase(){

        Query query= ref.child("Book");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                ClearAll();
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    Book book = new Book();
                    //book= snapshot.getValue(Book.class);
                    book.setBookImage(snapshot.child("bookImage").getValue().toString());
                    book.setTitle(snapshot.child("title").getValue().toString());
                    book.setAuthor(snapshot.child("author").getValue().toString());

                    bookList.add(book);
                }

                recyclerAdapter = new RecyclerAdapter(getApplicationContext(), bookList);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ClearAll(){
        if(bookList!=null){
            bookList.clear();

            if(recyclerAdapter!=null){
                recyclerAdapter.notifyDataSetChanged();
            }
        }

        bookList = new ArrayList<>();
    }

}