package com.gopal.ebookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gopal.AboutBooksActivity;
import com.gopal.readpdf.ReadPdfActivity;

import java.text.DateFormat;
import java.util.Calendar;

public class LikedBookActivity extends AppCompatActivity {

    private DatabaseReference firebaseDatabase;
    private DatabaseReference rootRef;
    private String currentUserId;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_liked_book );

        Toolbar toolbar = findViewById( R.id.toolbarLiked );
        setSupportActionBar( toolbar );
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle( R.string.likedbook );
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                currentUserId = firebaseUser.getUid();

                recyclerView = findViewById( R.id.readRecyclerViewLiked );
                firebaseDatabase = FirebaseDatabase.getInstance().getReference( "Liked" ).child( currentUserId );
                rootRef = FirebaseDatabase.getInstance().getReference();
                rootRef.keepSynced( true );
                firebaseDatabase.keepSynced( true );
                recyclerView.setHasFixedSize( true );

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager( this );
                linearLayoutManager.setStackFromEnd( true );
                linearLayoutManager.setReverseLayout( true );
                recyclerView.setLayoutManager( linearLayoutManager );
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    LinearLayout linearLayout = findViewById( R.id.linearLayoutLibrary );
                    linearLayout.setVisibility( View.VISIBLE );
                } else {
                    LinearLayout linearLayout = findViewById( R.id.linearLayoutLibrary );
                    linearLayout.setVisibility( View.GONE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

        FirebaseRecyclerAdapter<LikedBookModel, LikedViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<LikedBookModel, LikedViewHolder>(
                        LikedBookModel.class,
                        R.layout.my_book_layout,
                        LikedViewHolder.class,
                        firebaseDatabase

                ) {
                    @Override
                    protected void populateViewHolder(final LikedViewHolder likedViewHolder, final LikedBookModel likedBookModel, int i) {

                        final String bookKey = getRef( i ).getKey();
                        if (bookKey != null) {

                            rootRef.child( "Uploads" ).addListenerForSingleValueEvent( new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild( bookKey )) {
                                        rootRef.child( "Uploads" ).child( bookKey ).addListenerForSingleValueEvent( new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                String bookName = (String) snapshot.child( "bookName" ).getValue();
                                                String authorName = (String) snapshot.child( "authorName" ).getValue();
                                                String coverUrl = (String) snapshot.child( "coverUrl" ).getValue();
                                                long number = (long) snapshot.child( "number" ).getValue();

                                                likedViewHolder.setImage( coverUrl );
                                                likedViewHolder.setAuthor( authorName );
                                                likedViewHolder.setName( bookName );
                                                likedViewHolder.setDate( likedBookModel.getDate() );
                                                likedViewHolder.setPages( number );
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        } );
                                    } else {
                                        firebaseDatabase.child( bookKey ).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            } );

                            likedViewHolder.readBookBtn.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rootRef.child( "Uploads" ).child( bookKey ).addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String pdfUrl = (String) snapshot.child( "pdfUrl" ).getValue();
                                                String uploadId = (String) snapshot.child( "uploadId" ).getValue();

                                                Intent readIntent = new Intent( getApplicationContext(), ReadPdfActivity.class );
                                                readIntent.putExtra( "pdfUrl", pdfUrl );
                                                readIntent.putExtra( "bookKey", bookKey );
                                                readIntent.putExtra( "uploadId", uploadId );
                                                readIntent.putExtra( "defaultValue", 0 );
                                                readIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                                getApplicationContext().startActivity( readIntent );
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    } );
                                }
                            } );

                            likedViewHolder.liked.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    firebaseDatabase.child( bookKey ).removeValue();
                                }
                            } );

                            likedViewHolder.menu.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PopupMenu popupMenu = new PopupMenu( getApplicationContext(), likedViewHolder.menu );
                                    popupMenu.inflate( R.menu.my_book_menu );

                                    popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            if (item.getItemId() == R.id.addToLibraryMenu) {
                                                rootRef.child( "Library" ).child( currentUserId ).addListenerForSingleValueEvent( new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        if (!snapshot.hasChild( bookKey )) {
                                                            Calendar calendar = Calendar.getInstance();
                                                            String date = DateFormat.getDateInstance().format( calendar.getTime() );
                                                            rootRef.child( "Library" ).child( currentUserId ).child( bookKey )
                                                                    .child( "date" ).setValue( date );
                                                            Toast.makeText( getApplicationContext(), "Added to Library", Toast.LENGTH_SHORT ).show();

                                                        } else {
                                                            Toast.makeText( LikedBookActivity.this, "Already Added", Toast.LENGTH_SHORT ).show();
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                } );
                                            } else if (item.getItemId() == R.id.aboutBookMenu) {
                                                rootRef.child( "Uploads" ).child( bookKey ).addValueEventListener( new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {

                                                            String bookName = (String) snapshot.child( "bookName" ).getValue();
                                                            String authorName = (String) snapshot.child( "authorName" ).getValue();
                                                            String coverUrl = (String) snapshot.child( "coverUrl" ).getValue();
                                                            long number = (long) snapshot.child( "number" ).getValue();
                                                            String desc = (String) snapshot.child( "desc" ).getValue();
                                                            String type = (String) snapshot.child( "type" ).getValue();
                                                            String uploadUserId = (String) snapshot.child( "uploadId" ).getValue();

                                                            Intent aboutBookIntent = new Intent( getApplicationContext(), AboutBooksActivity.class );
                                                            aboutBookIntent.putExtra( "coverUrl", coverUrl );
                                                            aboutBookIntent.putExtra( "bookName", bookName );
                                                            aboutBookIntent.putExtra( "pageNumber", (int) number );
                                                            aboutBookIntent.putExtra( "authorName", authorName );
                                                            aboutBookIntent.putExtra( "desc", desc );
                                                            aboutBookIntent.putExtra( "type", type );
                                                            aboutBookIntent.putExtra( "uploadUserId", uploadUserId );
                                                            startActivity( aboutBookIntent );
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                } );
                                            } else if (item.getItemId() == R.id.deleteBookMenu) {
                                                firebaseDatabase.child( bookKey ).removeValue();
                                            }
                                            return true;
                                        }
                                    } );
                                    popupMenu.show();
                                }
                            } );

                        }
                    }
                };
        recyclerView.setAdapter( firebaseRecyclerAdapter );
    }

    public static class LikedViewHolder extends RecyclerView.ViewHolder {

        View view;
        Button readBookBtn;
        ImageView liked;
        TextView menu;

        public LikedViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            menu = view.findViewById( R.id.menuMyBook );
            readBookBtn = view.findViewById( R.id.readNowMyBook );
            liked = view.findViewById( R.id.likedBook );
            liked.setVisibility( View.VISIBLE );
        }

        public void setImage(String image) {
            ImageView coverImage = view.findViewById( R.id.bookCover );

            if (image.equals( "default" )){
                coverImage.setImageResource( R.drawable.appiconimage );
            }else{
                Glide.with( view.getContext() ).load( image ).placeholder( R.drawable.loading )
                        .into( coverImage );
            }
        }

        public void setDate(String date) {
            TextView addedDate = view.findViewById( R.id.dateAdded );
            addedDate.setText( date );
        }

        public void setName(String name) {
            TextView bookName = view.findViewById( R.id.bookNameRecentRead );
            bookName.setText( name );
        }

        public void setAuthor(String author) {
            TextView authorName = view.findViewById( R.id.authorNameRecentRead );
            authorName.setText( author );
        }

        public void setPages(long pages) {
            TextView pageNumber = view.findViewById( R.id.totalPageNumber );
            pageNumber.setText( String.valueOf( pages ) );
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected( item );
    }
}