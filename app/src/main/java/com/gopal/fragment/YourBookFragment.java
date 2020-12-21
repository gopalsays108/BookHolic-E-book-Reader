package com.gopal.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gopal.AboutBooksActivity;
import com.gopal.ebookapp.R;
import com.gopal.readpdf.ReadPdfActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class YourBookFragment extends Fragment {

    private View view;
    private RecyclerView myBookRecyclerView;
    private DatabaseReference databaseReference;
    private String currentUserId;
    private DatabaseReference rootRef;

    private LinearLayout noFound;

    public YourBookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate( R.layout.fragment_your_book, container, false );

        myBookRecyclerView = view.findViewById( R.id.myBookRecyclerView );
        noFound = view.findViewById( R.id.linearLayoutYourBook );
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();

            databaseReference = FirebaseDatabase.getInstance().getReference( "Uploads" );
            rootRef = FirebaseDatabase.getInstance().getReference().child( "Library" ).child( currentUserId );
            databaseReference.keepSynced( true );

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getContext() );
            linearLayoutManager.setReverseLayout( true );
            linearLayoutManager.setStackFromEnd( true );

            myBookRecyclerView.setHasFixedSize( true );
            myBookRecyclerView.setLayoutManager( linearLayoutManager );
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = databaseReference.orderByChild( "uploadId" ).equalTo( currentUserId );

        conversationQuery.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    noFound.setVisibility( View.VISIBLE );
                } else {
                    noFound.setVisibility( View.GONE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

        FirebaseRecyclerAdapter<MyBookModel, MyBookViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MyBookModel, MyBookViewHolder>(

                        MyBookModel.class,
                        R.layout.my_book_layout,
                        MyBookViewHolder.class,
                        conversationQuery

                ) {
                    @Override
                    protected void populateViewHolder(final MyBookViewHolder myBookViewHolder, final MyBookModel myBookModel, final int i) {

                        final String bookKey = getRef( i ).getKey();

                        myBookViewHolder.setBookName( myBookModel.getBookName() );
                        myBookViewHolder.setAuthorName( myBookModel.getAuthorName() );
                        myBookViewHolder.setImageCover( myBookModel.getCoverUrl() );
                        myBookViewHolder.setTotalPage( myBookModel.getNumber() );
                        myBookViewHolder.setAddedDate( myBookModel.getDate() );
                        myBookViewHolder.setPrivacy( myBookModel.getPrivacy() );

                        myBookViewHolder.readNowBtn.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent readNowIntent = new Intent( getContext(), ReadPdfActivity.class );
                                readNowIntent.putExtra( "pdfUrl", myBookModel.getPdfUrl() );
                                readNowIntent.putExtra( "bookKey", bookKey );
//                                readNowIntent.putExtra( "pdfName", myBookModel.getPdfName() ); // todo : attention not required
                                readNowIntent.putExtra( "uploadId", currentUserId );
                                startActivity( readNowIntent );
                            }
                        } );

                        myBookViewHolder.menu.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                PopupMenu popupMenu = new PopupMenu( view.getContext(), myBookViewHolder.menu );
                                popupMenu.inflate( R.menu.my_book_menu );

                                popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {

                                        if (item.getItemId() == R.id.addToLibraryMenu) {
                                            if (bookKey != null) {
                                                Calendar calendar = Calendar.getInstance();
                                                final String date = DateFormat.getDateInstance().format( calendar.getTime() );

                                                rootRef.addListenerForSingleValueEvent( new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (!snapshot.hasChild( bookKey )) {
                                                            Map<String, Object> libraryData = new HashMap<>();
                                                            libraryData.put( "date", date );

                                                            rootRef.child( bookKey ).setValue( libraryData ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText( view.getContext(), "Added to Library", Toast.LENGTH_SHORT ).show();
                                                                }
                                                            } );
                                                        } else {
                                                            Toast.makeText( view.getContext(), "Already Added", Toast.LENGTH_SHORT ).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                } );

                                            }
                                        } else if (item.getItemId() == R.id.aboutBookMenu) {

                                            Intent aboutBookIntent = new Intent( getContext(), AboutBooksActivity.class );
                                            aboutBookIntent.putExtra( "bookName", myBookModel.getBookName() );
                                            aboutBookIntent.putExtra( "coverUrl", myBookModel.getCoverUrl() );
                                            aboutBookIntent.putExtra( "pageNumber", myBookModel.getNumber() );
                                            aboutBookIntent.putExtra( "authorName", myBookModel.getAuthorName() );
                                            aboutBookIntent.putExtra( "desc", myBookModel.getDesc() );
                                            aboutBookIntent.putExtra( "type", myBookModel.getType() );
                                            aboutBookIntent.putExtra( "uploadUserId", myBookModel.getUploadId() );
                                            view.getContext().startActivity( aboutBookIntent );

                                        } else if (item.getItemId() == R.id.deleteBookMenu) {
                                            myBookViewHolder.deleteBook( bookKey );
                                        }

                                        return true;
                                    }
                                } );
                                popupMenu.show();
                            }
                        } );

                        myBookViewHolder.itemView.setOnLongClickListener( new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                final String privacyType = myBookModel.getPrivacy();

                                if (privacyType.equals( "public" )) {
                                    CharSequence[] option = new CharSequence[]{"Change privacy to private", "Delete book"};
                                    AlertDialog.Builder builder = new AlertDialog.Builder( view.getContext() );
                                    builder.setTitle( "Select an option" );
                                    builder.setItems( option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                if (bookKey != null)
                                                    databaseReference.child( bookKey ).child( "privacy" ).setValue( "private" );
                                            } else if (which == 1) {
                                                myBookViewHolder.deleteBook( bookKey );
                                            }
                                        }
                                    } );
                                    builder.show();

                                } else if (privacyType.equals( "private" )) {
                                    CharSequence[] option = new CharSequence[]{"Change privacy to public", "Delete book"};
                                    final AlertDialog.Builder builder = new AlertDialog.Builder( view.getContext() );
                                    builder.setTitle( "Select an option" );
                                    builder.setItems( option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                if (bookKey != null)
                                                    databaseReference.child( bookKey ).child( "privacy" ).setValue( "public" );
                                            } else if (which == 1) {
                                                myBookViewHolder.deleteBook( bookKey );
                                            }

                                        }
                                    } );
                                    builder.show();

                                }
                                return true;
                            }
                        } );

                        myBookViewHolder.name.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText( view.getContext(), "" + myBookModel.getBookName(), Toast.LENGTH_SHORT ).show();
                            }
                        } );

//                        myBookViewHolder.spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                              //String item = parent.getItemAtPosition( position ).toString();
//                              if (position == 0){
//                                  Toast.makeText( view.getContext(), "Hey", Toast.LENGTH_SHORT ).show();
//                              }else if (position == 1){
//                                  Toast.makeText( view.getContext(), "Hello", Toast.LENGTH_SHORT ).show();
//                              }
//                              //Toast.makeText( view.getContext(), "Position =>" + String.valueOf(  position) + "VALue => " + item, Toast.LENGTH_SHORT ).show();
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        } );

                    }
                };

        myBookRecyclerView.setAdapter( firebaseRecyclerAdapter );
    }

    public static class MyBookViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView menu;
        Button readNowBtn;
        DatabaseReference databaseReference1;
        TextView name;

        public MyBookViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            menu = view.findViewById( R.id.menuMyBook );
            readNowBtn = view.findViewById( R.id.readNowMyBook );
            name = view.findViewById( R.id.bookNameRecentRead );
            databaseReference1 = FirebaseDatabase.getInstance().getReference( "Uploads" );
//            spinner = view.findViewById( R.id.spinnerMyBook );
//            String[] more = {"More", "Delete"};
//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( view.getContext(), android.R.layout.simple_spinner_item, more );
//
//            dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
//
//            spinner.setAdapter( dataAdapter );

        }

        //------ Make Methods--------
        public void setBookName(String bookName) {
            name.setText( bookName );
        }

        public void setAuthorName(String authorName) {
            TextView writerName = view.findViewById( R.id.authorNameRecentRead );
            writerName.setText( authorName );
        }

        public void setImageCover(String imageCover) {
            ImageView bookImage = view.findViewById( R.id.bookCover );
            if (imageCover.equals( "default" )){
                bookImage.setImageResource( R.drawable.appiconimage );
            }else{
                Glide.with( view.getContext() ).load( imageCover ).placeholder( R.drawable.loading )
                        .into( bookImage );
            }
        }

        public void setTotalPage(int total) {
            TextView totalPage = view.findViewById( R.id.totalPageNumber );
            totalPage.setText( String.valueOf( total ) );
        }

        public void setAddedDate(String date) {
            TextView dateAdded = view.findViewById( R.id.dateAdded );
            dateAdded.setText( date );
        }

        public void setPrivacy(String privacy) {
            TextView privacyStatus = view.findViewById( R.id.privacyStatus );
            if (privacy.equals( "public" )) {
                privacyStatus.setVisibility( View.INVISIBLE );
            } else if (privacy.equals( "private" )) {
                privacyStatus.setVisibility( View.VISIBLE );
            }
        }

        public void deleteBook(final String bookKey) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder( view.getContext() );
            builder1.setTitle( "Delete" );
            builder1.setMessage( "Are you sure you want to delete?" );
            builder1.setPositiveButton( "  Yes  ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (bookKey != null)
                        databaseReference1.child( bookKey ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText( view.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT ).show();
                            }
                        } );
                }
            } ).setNegativeButton( "  No  ", null ).setIcon( android.R.drawable.ic_dialog_alert );
            builder1.show();
        }
    }
}