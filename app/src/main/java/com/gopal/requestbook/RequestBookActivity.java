package com.gopal.requestbook;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.gopal.ebookapp.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RequestBookActivity extends AppCompatActivity {

    private Dialog requestBookDialogue;
    private EditText bookRequestName;
    private EditText authorRequestName;
    private ProgressBar progressBar;
    private DatabaseReference rootRef;
    private RecyclerView recyclerViewRequest;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_request_book );

        Toolbar toolbar = findViewById( R.id.toolBarRequest );
        setSupportActionBar( toolbar );
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle( "Requests" );
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {

                rootRef = FirebaseDatabase.getInstance().getReference();
                databaseReference = FirebaseDatabase.getInstance().getReference( "Request" );

                setRequestDialogue();

                recyclerViewRequest = findViewById( R.id.recyclerViewRequest );
                recyclerViewRequest.setHasFixedSize( true );

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager( this );
                linearLayoutManager.setStackFromEnd( true );
                linearLayoutManager.setReverseLayout( true );
                recyclerViewRequest.setLayoutManager( linearLayoutManager );
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = databaseReference.orderByChild( "timestamp" );

        FirebaseRecyclerAdapter<RequestModel, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RequestModel, RequestViewHolder>(
                RequestModel.class,
                R.layout.request_book,
                RequestViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(RequestViewHolder requestViewHolder, RequestModel requestModel, int i) {
                requestViewHolder.setBookName( requestModel.getBookName() );
                requestViewHolder.setAuthor( requestModel.getAuthorName() );
                requestViewHolder.setDate( requestModel.getDate() );
            }
        };
        recyclerViewRequest.setAdapter( firebaseRecyclerAdapter );
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View view;

        public RequestViewHolder(@NonNull View itemView) {
            super( itemView );
            view = itemView;
        }

        private void setBookName(String bookName) {
            TextView tvBookName = view.findViewById( R.id.bookNameR );
            tvBookName.setText( bookName );
        }

        private void setAuthor(String author) {
            TextView authorName = view.findViewById( R.id.authorNameR );
            authorName.setText( author );
        }

        private void setDate(String date) {
            TextView requestedDate = view.findViewById( R.id.requestedDate );
            requestedDate.setText( String.format( "%s %s", view.getContext().getResources()
                    .getString( R.string.asked_on ), date ) );
        }
    }


    private void setRequestDialogue() {

        requestBookDialogue = new Dialog( this );
        requestBookDialogue.setContentView( R.layout.request_book_dialogue );
        if (requestBookDialogue.getWindow() != null) {
            requestBookDialogue.getWindow().setBackgroundDrawable( ContextCompat.getDrawable( this,R.drawable.rounded_corner ) );
            requestBookDialogue.getWindow().setLayout( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            requestBookDialogue.setCancelable( true );

            bookRequestName = requestBookDialogue.findViewById( R.id.bookNameRequest );
            authorRequestName = requestBookDialogue.findViewById( R.id.authorNameRequest );
            final Button requestBtn = requestBookDialogue.findViewById( R.id.requestBookBtn );
            progressBar = requestBookDialogue.findViewById( R.id.progressBarRequestDialogue );

            requestBtn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bookName = bookRequestName.getText().toString();
                    String authorName = authorRequestName.getText().toString();
                    if (bookName.isEmpty()) {
                        bookRequestName.setError( "Enter book name" );
                        bookRequestName.requestFocus();
                    } else if (authorName.isEmpty()) {
                        authorRequestName.setError( "Enter author name" );
                        authorRequestName.requestFocus();
                    } else {
                        requestBtn.setEnabled( false );
                        progressBar.setVisibility( View.VISIBLE );
                        startRequest();
                    }
                }

                private void startRequest() {

                    Calendar calendar = Calendar.getInstance();
                    String date = DateFormat.getDateInstance().format( calendar.getTime() );

                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put( "bookName", bookRequestName.getText().toString() );
                    requestMap.put( "authorName", authorRequestName.getText().toString() );
                    requestMap.put( "timestamp", ServerValue.TIMESTAMP );
                    requestMap.put( "date" , date );

                    rootRef.child( "Request" ).push().setValue( requestMap ).addOnSuccessListener( aVoid -> {
                        progressBar.setVisibility( View.INVISIBLE );
                        bookRequestName.setText( "" );
                        authorRequestName.setText( "" );
                        requestBtn.setEnabled( true );
                        requestBookDialogue.dismiss();
                    } );
                }
            } );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.request_menu, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.requestBookMenu) {
            requestBookDialogue.show();
        }
        return super.onOptionsItemSelected( item );
    }
}