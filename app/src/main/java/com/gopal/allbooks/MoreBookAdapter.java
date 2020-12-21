package com.gopal.allbooks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gopal.ebookapp.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class MoreBookAdapter extends RecyclerView.Adapter<MoreBookAdapter.ViewHolder> {

    List<MoreBookModel> moreBookModelList;

    public MoreBookAdapter(List<MoreBookModel> moreBookModelList) {
        this.moreBookModelList = moreBookModelList;
    }


    @NonNull
    @Override
    public MoreBookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.my_book_layout, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull MoreBookAdapter.ViewHolder holder, int position) {

        holder.setData( moreBookModelList.get( position ).getCoverUrl(), moreBookModelList.get( position ).getBookName(),
                moreBookModelList.get( position ).getAuthorName(), moreBookModelList.get( position ).getDate(), moreBookModelList.get( position ).getNumber(),
                moreBookModelList.get( position ).getKey() );
    }

    @Override
    public int getItemCount() {
        return moreBookModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView bookName;
        TextView authorName;
        TextView pagesNumber;
        TextView date;
        Button btn;
        TextView menu;
        FirebaseUser firebaseAuth;
        String currentUserId;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseAuth != null)
                currentUserId = firebaseAuth.getUid();
            cover = itemView.findViewById( R.id.bookCover );
            bookName = itemView.findViewById( R.id.bookNameRecentRead );
            authorName = itemView.findViewById( R.id.authorNameRecentRead );
            pagesNumber = itemView.findViewById( R.id.totalPageNumber );
            date = itemView.findViewById( R.id.dateAdded );
            btn = itemView.findViewById( R.id.readNowMyBook );
            menu = itemView.findViewById( R.id.menuMyBook );
            menu.setVisibility( View.INVISIBLE );
            btn.setText( R.string.add_to_library );

        }

        private void setData(final String image, final String bookName, String authorName, String date, long pages, final String key) {

            Glide.with( itemView.getContext() ).load( image ).placeholder( R.drawable.loading )
                    .into( cover );
            this.bookName.setText( bookName );
            this.authorName.setText( authorName );
            this.pagesNumber.setText( String.valueOf( pages ) );
            this.date.setText( date );

            this.bookName.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText( itemView.getContext(), "" + bookName, Toast.LENGTH_SHORT ).show();
                }
            } );

            btn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference( "Library" ).child( currentUserId );

                    Calendar calendar = Calendar.getInstance();
                    String date = DateFormat.getDateInstance().format( calendar.getTime() );

                    rootRef.child( key ).child( "date" ).setValue( date );
                }
            } );
        }
    }
}