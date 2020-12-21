package com.gopal.allbooks;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gopal.AboutBooksActivity;
import com.gopal.ebookapp.R;
import com.gopal.readpdf.ReadPdfActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllBooksAdapter extends RecyclerView.Adapter<AllBooksAdapter.ViewHolder> {

    List<AllBooksModel> allBooksModels;

    public AllBooksAdapter(List<AllBooksModel> allBooksModels) {
        this.allBooksModels = allBooksModels;
    }

    @NonNull
    @Override
    public AllBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.my_book_layout, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull AllBooksAdapter.ViewHolder holder, int position) {

        holder.setData( allBooksModels.get( position ).getBookName(), allBooksModels.get( position ).getAuthorName(),
                allBooksModels.get( position ).getCoverUrl(), allBooksModels.get( position ).getDesc(), allBooksModels.get( position ).getDate(),
                allBooksModels.get( position ).getUploadId(), allBooksModels.get( position ).getType(), allBooksModels.get( position ).getPdfUrl(),
                allBooksModels.get( position ).getNumber(), allBooksModels.get( position ).getKey(), allBooksModels.get( position ).getCurrentUserId() );
    }

    @Override
    public int getItemCount() {
        return allBooksModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView bookName;
        private TextView authorName;
        private ImageView bookCover;
        private TextView dateAdded;
        private TextView pageNumber;
        private Button readNow;
        private ImageView like;
        private DatabaseReference rootRef;
        private boolean isLiked = false;
        private TextView menu;
        private String dateToday;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            view = itemView;

            bookName = view.findViewById( R.id.bookNameRecentRead );
            authorName = view.findViewById( R.id.authorNameRecentRead );
            bookCover = view.findViewById( R.id.bookCover );
            dateAdded = view.findViewById( R.id.dateAdded );
            pageNumber = view.findViewById( R.id.totalPageNumber );
            readNow = view.findViewById( R.id.readNowMyBook );
            like = view.findViewById( R.id.likedBook );
            rootRef = FirebaseDatabase.getInstance().getReference();
            menu = view.findViewById( R.id.menuMyBook );

            Calendar calendar = Calendar.getInstance();
            dateToday = DateFormat.getDateInstance().format( calendar.getTime() );

            like.setImageResource( R.drawable.noliked );
            like.setVisibility( View.VISIBLE );
        }

        private void setData(final String bookName, final String authorName, final String coverUrl, final String desc,
                             final String date, final String UploadId, final String type, final String pdfUrl,
                             final long number, final String key, final String currentUserId) {

            if (coverUrl.equals( "default" )){
                bookCover.setImageResource( R.drawable.appiconimage );
            }else{
                Glide.with( view.getContext() ).load( coverUrl ).placeholder( R.drawable.loading )
                        .into( bookCover );
            }

            this.bookName.setText( bookName );
            this.authorName.setText( authorName );
            this.dateAdded.setText( date );
            this.pageNumber.setText( String.valueOf( number ) );

            this.bookName.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText( view.getContext(), "" + bookName, Toast.LENGTH_SHORT ).show();
                }
            } );


            menu.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu( view.getContext(), menu );
                    popupMenu.inflate( R.menu.all_books_menus );

                    popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.addToLibraryMenu) {

                                rootRef.child( "Library" ).child( currentUserId ).addListenerForSingleValueEvent( new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.hasChild( key )) {
                                            Toast.makeText( view.getContext(), "Added to Library", Toast.LENGTH_SHORT ).show();
                                            rootRef.child( "Library" ).child( currentUserId ).child( key ).child( "date" ).setValue( dateToday );
                                        } else {
                                            Toast.makeText( view.getContext(), "Already Added", Toast.LENGTH_SHORT ).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                } );
                            } else if (item.getItemId() == R.id.aboutBookMenu) {
                                Intent aboutBookIntent = new Intent( view.getContext(), AboutBooksActivity.class );
                                aboutBookIntent.putExtra( "bookName", bookName );
                                aboutBookIntent.putExtra( "coverUrl", coverUrl );
                                aboutBookIntent.putExtra( "pageNumber", (int) number );
                                aboutBookIntent.putExtra( "authorName", authorName );
                                aboutBookIntent.putExtra( "desc", desc );
                                aboutBookIntent.putExtra( "type", type );
                                aboutBookIntent.putExtra( "uploadUserId", UploadId );
                                view.getContext().startActivity( aboutBookIntent );
                            }
                            return true;
                        }
                    } );
                    popupMenu.show();
                }
            } );

            like.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isLiked = true;
                    if (key != null)
                        rootRef.child( "Liked" ).child( currentUserId ).addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (isLiked) {
                                    if (!snapshot.hasChild( key )) {

                                        Map<String, Object> likedMap = new HashMap<>();
                                        likedMap.put( "bookKey", key );
                                        likedMap.put( "date", dateToday );
                                        rootRef.child( "Liked" ).child( currentUserId ).child( key ).setValue( likedMap );
                                        like.setImageResource( R.drawable.liked );

                                        isLiked = false;
                                    } else {
                                        rootRef.child( "Liked" ).child( currentUserId ).child( key ).removeValue();
                                        like.setImageResource( R.drawable.noliked );
                                        isLiked = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        } );
                }
            } );

            readNow.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent recentReadIntent = new Intent( view.getContext(), ReadPdfActivity.class );
                    recentReadIntent.putExtra( "bookKey", key );
                    recentReadIntent.putExtra( "pdfUrl", pdfUrl );
                    recentReadIntent.putExtra( "defaultValue", 0 );
                    recentReadIntent.putExtra( "uploadId", UploadId );
                    view.getContext().startActivity( recentReadIntent );
                }
            } );

            rootRef.child( "Liked" ).addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild( currentUserId )) {
                        rootRef.child( "Liked" ).child( currentUserId ).addListenerForSingleValueEvent( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild( key )) {
                                    like.setImageResource( R.drawable.liked );
                                    like.setVisibility( View.VISIBLE );
                                } else {
                                    like.setImageResource( R.drawable.noliked );
                                    like.setVisibility( View.VISIBLE );
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        } );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            } );

        }
    }
}
