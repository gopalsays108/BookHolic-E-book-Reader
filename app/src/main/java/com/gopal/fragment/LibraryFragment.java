package com.gopal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gopal.AboutBooksActivity;
import com.gopal.ebookapp.R;
import com.gopal.readpdf.ReadPdfActivity;

public class LibraryFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private DatabaseReference rootRef;
    private String currentUserId;
    private String bookName;
    private String authorName;
    private String coverUrl;
    private long number;


    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // I    nflate the layout for this fragment
        view = inflater.inflate( R.layout.fragment_library, container, false );
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
            recyclerView = view.findViewById( R.id.libraryRecyclerView );
            databaseReference = FirebaseDatabase.getInstance().getReference( "Library" ).child( currentUserId );
            rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.keepSynced( true );
            databaseReference.keepSynced( true );

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( view.getContext() );
            linearLayoutManager.setReverseLayout( true );
            linearLayoutManager.setStackFromEnd( true );

            recyclerView.setHasFixedSize( true );
            recyclerView.setLayoutManager( linearLayoutManager );
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        rootRef.child( "Library" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild( currentUserId )) {
                    LinearLayout linearLayout = view.findViewById( R.id.linearLayoutLibrary );
                    linearLayout.setVisibility( View.VISIBLE );
                } else {
                    LinearLayout linearLayout = view.findViewById( R.id.linearLayoutLibrary );
                    linearLayout.setVisibility( View.GONE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

        FirebaseRecyclerAdapter<LibraryModel, LibraryViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<LibraryModel, LibraryViewHolder>(
                        LibraryModel.class,
                        R.layout.my_book_layout,
                        LibraryViewHolder.class,
                        databaseReference
                ) {
                    @Override
                    protected void populateViewHolder(final LibraryViewHolder libraryViewHolder, LibraryModel libraryModel, int i) {

                        final String bookKey = getRef( i ).getKey();
                        libraryViewHolder.setDate( libraryModel.getDate() );

                        if (bookKey != null)
                            rootRef.child( "Uploads" ).addListenerForSingleValueEvent( new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.hasChild( bookKey )) {
                                        rootRef.child( "Uploads" ).child( bookKey ).addListenerForSingleValueEvent( new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                bookName = (String) snapshot.child( "bookName" ).getValue();
                                                authorName = (String) snapshot.child( "authorName" ).getValue();
                                                coverUrl = (String) snapshot.child( "coverUrl" ).getValue();
                                                number = (long) snapshot.child( "number" ).getValue();

                                                libraryViewHolder.setName( bookName );
                                                libraryViewHolder.setAuthor( authorName );
                                                libraryViewHolder.setCover( coverUrl );
                                                libraryViewHolder.setPages( number );
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        } );
                                    } else {
                                        databaseReference.child( bookKey ).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            } );

                        libraryViewHolder.menu.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PopupMenu popupMenu = new PopupMenu( view.getContext(), libraryViewHolder.menu );
                                popupMenu.inflate( R.menu.library_menu );

                                popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {

                                        if (item.getItemId() == R.id.removeFromLibrary) {
                                            if (bookKey != null)
                                                databaseReference.child( bookKey ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText( view.getContext(), "Removed", Toast.LENGTH_SHORT ).show();
                                                    }
                                                } );
                                        } else if (item.getItemId() == R.id.aboutBook) {

                                            if (bookKey != null)
                                                rootRef.child( "Uploads" ).child( bookKey ).addListenerForSingleValueEvent( new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {

                                                            long totalNumbers = (long) snapshot.child( "number" ).getValue();
                                                            String bookNames = (String) snapshot.child( "bookName" ).getValue();
                                                            String authorNames = (String) snapshot.child( "authorName" ).getValue();
                                                            String coverUrls = (String) snapshot.child( "coverUrl" ).getValue();
                                                            String descs = (String) snapshot.child( "desc" ).getValue();
                                                            String types = (String) snapshot.child( "type" ).getValue();
                                                            String uploadUserId = (String) snapshot.child( "uploadId" ).getValue();

                                                            Intent aboutBookIntent = new Intent( getContext(), AboutBooksActivity.class );
                                                            aboutBookIntent.putExtra( "bookName", bookNames );
                                                            aboutBookIntent.putExtra( "coverUrl", coverUrls );
                                                            aboutBookIntent.putExtra( "pageNumber", (int) totalNumbers );
                                                            aboutBookIntent.putExtra( "authorName", authorNames );
                                                            aboutBookIntent.putExtra( "desc", descs );
                                                            aboutBookIntent.putExtra( "type", types );
                                                            aboutBookIntent.putExtra( "uploadUserId", uploadUserId );
                                                            view.getContext().startActivity( aboutBookIntent );
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                } );
                                        }

                                        return true;
                                    }
                                } );
                                popupMenu.show();
                            }
                        } );

                        if (bookKey != null)
                            libraryViewHolder.readNowBtn.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    rootRef.child( "Uploads" ).addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild( bookKey )) {
                                                rootRef.child( "Uploads" ).child( bookKey ).addListenerForSingleValueEvent( new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        String pdfUrls = (String) snapshot.child( "pdfUrl" ).getValue();
                                                        String uploadIds = (String) snapshot.child( "uploadId" ).getValue();

                                                        Intent readNowIntent = new Intent( view.getContext(), ReadPdfActivity.class );
                                                        readNowIntent.putExtra( "pdfUrl", pdfUrls );
                                                        readNowIntent.putExtra( "bookKey", bookKey );
                                                        readNowIntent.putExtra( "uploadId", uploadIds );
                                                        startActivity( readNowIntent );


                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                } );
                                            } else {
                                                databaseReference.child( bookKey ).removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    } );
                                }
                            } );
                    }
                };
        recyclerView.setAdapter( firebaseRecyclerAdapter );
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {

        View view;
        Button readNowBtn;
        TextView bookName;
        TextView menu;

        public LibraryViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            readNowBtn = view.findViewById( R.id.readNowMyBook );
            bookName = view.findViewById( R.id.bookNameRecentRead );
            menu = view.findViewById( R.id.menuMyBook );
        }

        public void setDate(String date) {
            TextView addedDate = view.findViewById( R.id.dateAdded );
            addedDate.setText( date );
        }

        public void setName(final String name) {
            bookName.setText( name );


            bookName.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText( itemView.getContext(), "" + name, Toast.LENGTH_SHORT ).show();
                }
            } );
        }

        public void setAuthor(String author) {
            TextView authorName = view.findViewById( R.id.authorNameRecentRead );
            authorName.setText( author );
        }

        public void setCover(String cover) {
            ImageView coverImage = view.findViewById( R.id.bookCover );
            if (cover.equals( "default" )){
                coverImage.setImageResource( R.drawable.appiconimage );
            }else {
                Glide.with( view.getContext() ).load( cover ).placeholder( R.drawable.loading )
                        .into( coverImage );
            }
        }

        public void setPages(long pages) {
            TextView pageNumber = view.findViewById( R.id.totalPageNumber );
            pageNumber.setText( String.valueOf( pages ) );
        }
    }
}