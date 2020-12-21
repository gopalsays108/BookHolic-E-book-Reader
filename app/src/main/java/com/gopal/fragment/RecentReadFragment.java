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
import android.widget.ProgressBar;
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
import com.gopal.allbooks.AllBooksActivity;
import com.gopal.ebookapp.R;
import com.gopal.readpdf.ReadPdfActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RecentReadFragment extends Fragment {

    private View view;
    private RecyclerView recentBookRecyclerView;
    private String currentUserId;
    private DatabaseReference databaseReference;
    private DatabaseReference rootRef;
    private long totalNumber;
    private String bookName;
    private String authorName;
    private String coverUrl;
    private boolean liked = false;

    public RecentReadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate( R.layout.fragment_recent_read, container, false );

        recentBookRecyclerView = view.findViewById( R.id.recentReadRecyclerView );

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference( "RecentRead" ).child( currentUserId );
            rootRef = FirebaseDatabase.getInstance().getReference();

            databaseReference.keepSynced( true );

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getContext() );
            linearLayoutManager.setReverseLayout( true );
            linearLayoutManager.setStackFromEnd( true );

            recentBookRecyclerView.setHasFixedSize( true );
            recentBookRecyclerView.setLayoutManager( linearLayoutManager );
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    LinearLayout linearLayout = view.findViewById( R.id.linearLayoutRecent );
                    linearLayout.setVisibility( View.VISIBLE );
                    Button button = view.findViewById( R.id.findBooks );
                    button.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity( new Intent( view.getContext(), AllBooksActivity.class ) );
                        }
                    } );
                } else {
                    LinearLayout linearLayout = view.findViewById( R.id.linearLayoutRecent );
                    linearLayout.setVisibility( View.GONE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

        FirebaseRecyclerAdapter<RecentReadModel, RecentReadViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<RecentReadModel, RecentReadViewHolder>(
                        RecentReadModel.class,
                        R.layout.single_read_book_layout,
                        RecentReadViewHolder.class,
                        databaseReference
                ) {
                    @Override
                    protected void populateViewHolder(final RecentReadViewHolder recentReadViewHolder
                            , final RecentReadModel recentReadModel, int i) {

                        final String bookKey = getRef( i ).getKey();

                        recentReadViewHolder.setCreated( recentReadModel.getDate() );
                        recentReadViewHolder.setLastReadDate( recentReadModel.getLastDate() );

                        if (bookKey != null)
                            rootRef.child( "Uploads" ).addListenerForSingleValueEvent( new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.hasChild( bookKey )) {
                                        rootRef.child( "Uploads" ).child( bookKey ).addListenerForSingleValueEvent( new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                totalNumber = (long) snapshot.child( "number" ).getValue();
                                                bookName = (String) snapshot.child( "bookName" ).getValue();
                                                authorName = (String) snapshot.child( "authorName" ).getValue();
                                                coverUrl = (String) snapshot.child( "coverUrl" ).getValue();

                                                recentReadViewHolder.setProgress( recentReadModel.getLastPage(), totalNumber );
                                                recentReadViewHolder.setName( bookName );
                                                recentReadViewHolder.setAuthor( authorName );
                                                recentReadViewHolder.setImage( coverUrl );
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

                        recentReadViewHolder.continueReading.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final long lastPageRead = recentReadModel.getLastPage();

                                if (bookKey != null)
                                    rootRef.child( "Uploads" ).child( bookKey ).addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            long totalPagee = (long) snapshot.child( "number" ).getValue();
                                            final String pdfUrl = (String) snapshot.child( "pdfUrl" ).getValue();
                                            final String uploadId = (String) snapshot.child( "UploadId" ).getValue();
                                            int progress = (int) (((float) lastPageRead / (float) totalPagee) * 100);

                                            if (progress < 100) {
                                                recentReadViewHolder.setIntent( pdfUrl, bookKey, uploadId, (int) lastPageRead );
                                            } else {

                                                CharSequence[] charSequences = new CharSequence[]{"Read Again", "Remove from recent Read"};

                                                AlertDialog.Builder builder = new AlertDialog.Builder( view.getContext() );
                                                builder.setTitle( "Choose an option" );
                                                builder.setItems( charSequences, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        if (which == 0) {
                                                            recentReadViewHolder.setIntent( pdfUrl, bookKey, uploadId, 0 );
                                                        } else if (which == 1) {
                                                            databaseReference.child( bookKey ).removeValue();
                                                        }

                                                    }
                                                } );
                                                builder.show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    } );
                            }
                        } );

                        recentReadViewHolder.menuRecent.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PopupMenu popupMenu = new PopupMenu( view.getContext(), recentReadViewHolder.menuRecent );
                                popupMenu.inflate( R.menu.my_book_menu );

                                popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {

                                        if (item.getItemId() == R.id.addToLibraryMenu) {
                                            if (bookKey != null) {
                                                Calendar calendar = Calendar.getInstance();
                                                final String date = DateFormat.getDateInstance().format( calendar.getTime() );

                                                rootRef.child( "Library" ).child( currentUserId ).addListenerForSingleValueEvent( new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (!snapshot.hasChild( bookKey )) {
                                                            Map<String, Object> libraryData = new HashMap<>();
                                                            libraryData.put( "date", date );

                                                            rootRef.child( "Library" ).child( currentUserId ).child( bookKey ).setValue( libraryData ).addOnSuccessListener( new OnSuccessListener<Void>() {
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

                                                            int pageNo = (int) totalNumbers;

                                                            Intent aboutBookIntent = new Intent( getContext(), AboutBooksActivity.class );
                                                            aboutBookIntent.putExtra( "bookName", bookNames );
                                                            aboutBookIntent.putExtra( "coverUrl", coverUrls );
                                                            aboutBookIntent.putExtra( "pageNumber", pageNo );
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

                                        } else if (item.getItemId() == R.id.deleteBookMenu) {
                                            recentReadViewHolder.deleteBook( bookKey, databaseReference );
                                        }

                                        return true;
                                    }
                                } );
                                popupMenu.show();

                            }
                        } );


                        recentReadViewHolder.liked.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                liked = true;
                                if (bookKey != null)
                                    rootRef.child( "Liked" ).child( currentUserId ).addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (liked) {
                                                if (!snapshot.hasChild( bookKey )) {

                                                    Calendar calendar = Calendar.getInstance();
                                                    String date = DateFormat.getDateInstance().format( calendar.getTime() );

                                                    Map<String, Object> likedMap = new HashMap<>();
                                                    likedMap.put( "bookKey", bookKey );
                                                    likedMap.put( "date", date );

                                                    rootRef.child( "Liked" ).child( currentUserId ).child( bookKey ).setValue( likedMap );
                                                    recentReadViewHolder.liked.setImageResource( R.drawable.liked );
                                                } else {
                                                    rootRef.child( "Liked" ).child( currentUserId ).child( bookKey ).removeValue();
                                                    recentReadViewHolder.liked.setImageResource( R.drawable.noliked );
                                                }
                                                liked = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    } );
                            }
                        } );
                        recentReadViewHolder.setLike( rootRef, currentUserId, bookKey );

                    }
                };

        recentBookRecyclerView.setAdapter( firebaseRecyclerAdapter );
    }

    public static class RecentReadViewHolder extends RecyclerView.ViewHolder {

        View view;
        private TextView continueReading;
        TextView menuRecent;
        ImageView liked;

        public RecentReadViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
            continueReading = view.findViewById( R.id.continueReading );
            menuRecent = view.findViewById( R.id.menuRecent );
            liked = view.findViewById( R.id.notLiked );
        }

        public void setIntent(String pdfUrl, String bookKey, String uploadId, int lastNumber) {
            Intent readIntent = new Intent( view.getContext(), ReadPdfActivity.class );
            readIntent.putExtra( "pdfUrl", pdfUrl );
            readIntent.putExtra( "bookKey", bookKey );
            readIntent.putExtra( "uploadId", uploadId );
            readIntent.putExtra( "defaultValue", lastNumber );
            view.getContext().startActivity( readIntent );

        }

        public void setLike(DatabaseReference rootRef, String currentUserId, final String bookKey) {

            rootRef.child( "Liked" ).child( currentUserId ).addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild( bookKey )) {
                        liked.setImageResource( R.drawable.liked );
                    } else {
                        liked.setImageResource( R.drawable.noliked );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            } );
        }

        public void deleteBook(final String bookKey, final DatabaseReference databaseReference) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder( view.getContext() );
            builder1.setTitle( "Delete" );
            builder1.setMessage( "Are you sure you want to delete?" );
            builder1.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (bookKey != null)
                        databaseReference.child( bookKey ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText( view.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT ).show();
                            }
                        } );
                }
            } ).setNegativeButton( "No", null ).setIcon( android.R.drawable.ic_dialog_alert );
            builder1.show();
        }

        public void setImage(String image) {
            ImageView imageView = view.findViewById( R.id.recentReadBookCover );
            if (image.equals( "default" )){
                imageView.setImageResource( R.drawable.appiconimage );
            }else{
                Glide.with( view.getContext() ).load( image ).placeholder( R.drawable.loading )
                        .into( imageView );
            }
        }

        public void setName(final String name) {
            TextView bookName = view.findViewById( R.id.bookNameRecentRead );
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

        public void setCreated(String created) {
            TextView createdDate = view.findViewById( R.id.createdRecentBooks );
            createdDate.setText( created );
        }

        public void setLastReadDate(String lastReadDate) {
            TextView lastRead = view.findViewById( R.id.lastDate );
            lastRead.setText( lastReadDate );
        }

        public void setProgress(long lastPage, long totalPage) {
            TextView pageNo = view.findViewById( R.id.progressRecent );
            TextView progressBar = view.findViewById( R.id.progressBarPercentage );
            ProgressBar progressBar1 = view.findViewById( R.id.progressBarRecent );
            pageNo.setText( String.format( "%s %s %s", lastPage, view.getContext().getResources()
                    .getString( R.string.out_of ), totalPage ) );

            int progress = (int) (((float) lastPage / (float) totalPage) * 100);
            progressBar1.setProgress( progress );
            progressBar.setText( String.format( "%s %s", progress, view.getContext()
                    .getResources().getString( R.string.progressPercentage ) ) );

            if (progress < 100) {
                continueReading.setText( R.string.continue_reading );
            } else {
                continueReading.setText( R.string.finished );
            }
        }
    }
}