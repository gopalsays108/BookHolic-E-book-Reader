package com.gopal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gopal.allbooks.MoreBookActivity;
import com.gopal.ebookapp.R;

public class AboutBooksActivity extends AppCompatActivity {

    private Button moreBooks;
    private String uploadUserId;
    private String currentUserId;
    private String userName;
    private DatabaseReference databaseReference;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_about_books );
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();

            databaseReference = FirebaseDatabase.getInstance().getReference( "Users" );

            String bookName = getIntent().getStringExtra( "bookName" );
            String coverUrl = getIntent().getStringExtra( "coverUrl" );
            int number = getIntent().getIntExtra( "pageNumber", 0 );
            String authorName = getIntent().getStringExtra( "authorName" );
            final String desc = getIntent().getStringExtra( "desc" );
            String bookType = getIntent().getStringExtra( "type" );
            uploadUserId = getIntent().getStringExtra( "uploadUserId" );
            ScrollView scrollView1 = findViewById( R.id.scrollView1 );

            TextView tvBookName = findViewById( R.id.bookNameAbout );
            TextView tvAuthorName = findViewById( R.id.authorNameAbout );
            final TextView tvDesc = findViewById( R.id.descAbout );
            ImageView cover = findViewById( R.id.bookPhotoAboutBook );
            TextView totalPage = findViewById( R.id.pageNoAbout );
            TextView type = findViewById( R.id.typeAbout );
            moreBooks = findViewById( R.id.moreBook );

            tvDesc.setMovementMethod( new ScrollingMovementMethod() );

            Log.i( "INTENT", "=> " + uploadUserId );

            tvBookName.setText( bookName );
            tvAuthorName.setText( authorName );
            tvDesc.setText( desc );
            totalPage.setText( String.format( "%s %s",
                    getApplicationContext().getResources().getString( R.string.no_of_pages ), number ) );
            type.setText( bookType );

            if (coverUrl != null)
            if (coverUrl.equals( "default" )){
                cover.setImageResource( R.drawable.appiconimage );
            }else{
                Glide.with( getApplicationContext() ).load( coverUrl ).placeholder( R.drawable.loading ).into( cover );
                checkClickedData();
            }

            moreBooks.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( getApplicationContext(), MoreBookActivity.class );
                    intent.putExtra( "user", userName );
                    intent.putExtra( "uploadUserId", uploadUserId );
                    startActivity( intent );
                }
            } );

            scrollView1.setOnTouchListener( new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    tvDesc.getParent().requestDisallowInterceptTouchEvent( false );


                    return false;
                }
            } );

            tvDesc.setOnTouchListener( new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    tvDesc.getParent().requestDisallowInterceptTouchEvent( true );
                    return false;
                }
            } );
        }
    }

    private void checkClickedData() {

        if (currentUserId.equals( uploadUserId )) {
            moreBooks.setVisibility( View.GONE );
        } else {
            setButton();
        }
    }

    private void setButton() {

        databaseReference.child( uploadUserId ).addListenerForSingleValueEvent( new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    userName = (String) snapshot.child( "name" ).getValue();
                    moreBooks.setVisibility( View.VISIBLE );
                    moreBooks.setText( String.format( "%s %s", getApplicationContext()
                            .getResources().getString( R.string.more_book_from ), userName ) );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}