package com.gopal.allbooks;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.gopal.ebookapp.R;

import java.util.ArrayList;
import java.util.List;

public class MoreBookActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private String currentUserId;
    private String uploadUserId;
    private List<MoreBookModel> moreBookModelList;
    private MoreBookAdapter moreBookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_more_book );

        String user = getIntent().getStringExtra( "user" );
        uploadUserId = getIntent().getStringExtra( "uploadUserId" );

        Toolbar toolbar = findViewById( R.id.toolbarMoreBook );
        setSupportActionBar( toolbar );
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle( "Books by " + user );
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                currentUserId = firebaseUser.getUid();

                recyclerView = findViewById( R.id.MoreBookReadRecyclerView );
                databaseReference = FirebaseDatabase.getInstance().getReference( "Uploads" );

                recyclerView.setHasFixedSize( true );

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager( this );
                linearLayoutManager.setStackFromEnd( true );
                linearLayoutManager.setReverseLayout( true );
                recyclerView.setLayoutManager( linearLayoutManager );

                moreBookModelList = new ArrayList<>();
                moreBookAdapter = new MoreBookAdapter( moreBookModelList );

                recyclerView.setAdapter( moreBookAdapter );
                getInfo();

            }
        }
    }

    private void getInfo() {

        Query query = databaseReference.orderByChild( "uploadId" ).equalTo( uploadUserId );

        query.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.exists()) {

                    /*     holder.setData( moreBookModelList.get( position ).getCoverUrl(), moreBookModelList.get( position ).getBookName(),
                moreBookModelList.get( position ).getAuthorName(), moreBookModelList.get( position ).getDate(), moreBookModelList.get( position ).getNumber(),
                moreBookModelList.get( position ).getKey() );*/

                    String name = (String) snapshot.child( "bookName" ).getValue();
                    String authorName = (String) snapshot.child( "authorName" ).getValue();
                    String coverUrl = (String) snapshot.child( "coverUrl" ).getValue();
                    String date = (String) snapshot.child( "date" ).getValue();
                    long pageNumber = (long) snapshot.child( "number" ).getValue();
                    String pdfUrl = (String) snapshot.child( "pdfUrl" ).getValue();
                    String key = snapshot.getKey();
                    String privacy = (String) snapshot.child( "privacy" ).getValue();

                    moreBookModelList.add( new MoreBookModel( authorName, name, coverUrl, date, key,
                            pdfUrl, pageNumber, privacy ) );

                    search();

                    moreBookAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void search() {

        ArrayList<MoreBookModel> arrayList = new ArrayList<>();
        for (MoreBookModel object : moreBookModelList) {
            if (object.getPrivacy().toLowerCase().contains( "public".toLowerCase() )) {

                arrayList.add( object );
            }
        }

        MoreBookAdapter moreBookAdapter = new MoreBookAdapter( arrayList );
        recyclerView.setAdapter( moreBookAdapter );
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        Query query = databaseReference.orderByChild( "uploadId" ).equalTo( uploadUserId );
//
//        FirebaseRecyclerAdapter<MoreBookModel, MoreBookViewHolder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<MoreBookModel, MoreBookViewHolder>(
//                        MoreBookModel.class,
//                        R.layout.my_book_layout,
//                        MoreBookViewHolder.class,
//                        query
//                ) {
//
//                    @Override
//                    protected void populateViewHolder(MoreBookViewHolder moreBookViewHolder, MoreBookModel moreBookModel, int i) {
//
//                        final String bookKey = getRef( i ).getKey();
//
//                        moreBookViewHolder.setImage( moreBookModel.getCoverUrl() );
//                        moreBookViewHolder.setName( moreBookModel.getBookName() );
//                        moreBookViewHolder.setAuthor( moreBookModel.getAuthorName() );
//                        moreBookViewHolder.setDateAdded( moreBookModel.getDate() );
//                        moreBookViewHolder.setPages( moreBookModel.getNumber() );
//
//                        if (bookKey != null)
//                            moreBookViewHolder.readNowBtn.setOnClickListener( new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference( "Library" ).child( currentUserId );
//
//                                    Calendar calendar = Calendar.getInstance();
//                                    String date = DateFormat.getDateInstance().format( calendar.getTime() );
//
//                                    rootRef.child( bookKey ).child( "date" ).setValue( date );
//                                }
//                            } );
//                    }
//                };
//        recyclerView.setAdapter( firebaseRecyclerAdapter );
//    }

//    public static class MoreBookViewHolder extends RecyclerView.ViewHolder {
//
//        View view;
//        Button readNowBtn;
//
//        public MoreBookViewHolder(@NonNull View itemView) {
//            super( itemView );
//            view = itemView;
//            TextView textView = view.findViewById( R.id.menuMyBook );
//            textView.setVisibility( View.INVISIBLE );
//            readNowBtn = view.findViewById( R.id.readNowMyBook );
//            readNowBtn.setText( R.string.add_to_library );
//        }
//
//        private void setImage(String image) {
//            ImageView cover = view.findViewById( R.id.bookCover );
//            Glide.with( view.getContext() ).load( image ).placeholder( R.drawable.trailphoto )
//                    .into( cover );
//        }
//
//        private void setName(String name) {
//            TextView bookName = view.findViewById( R.id.bookNameRecentRead );
//            bookName.setText( name );
//        }
//
//        private void setAuthor(String author) {
//            TextView authorName = view.findViewById( R.id.authorNameRecentRead );
//            authorName.setText( author );
//        }
//
//        private void setPages(long pages) {
//            TextView pagesNumber = view.findViewById( R.id.totalPageNumber );
//            pagesNumber.setText( String.valueOf( pages ) );
//        }
//
//        private void setDateAdded(String dateAdded) {
//            TextView date = view.findViewById( R.id.dateAdded );
//            date.setText( dateAdded );
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected( item );
    }
}