package com.gopal.allbooks;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

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

public class AllBooksActivity extends AppCompatActivity {

    private String currentUserId;
    private DatabaseReference databaseReference;
    private List<AllBooksModel> allBooksModelsList;
    private RecyclerView recyclerViewAllBooks;
    private AllBooksAdapter allBooksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_all_books );

        Toolbar toolbar = findViewById( R.id.toolbarAllBook );
        setSupportActionBar( toolbar );
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle( "All Books" );
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                currentUserId = firebaseAuth.getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference( "Uploads" );

                recyclerViewAllBooks = findViewById( R.id.recyclerViewAllBooks );

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext() );
                linearLayoutManager.setOrientation( RecyclerView.VERTICAL );
                linearLayoutManager.setReverseLayout( true );
                linearLayoutManager.setStackFromEnd( true );
                recyclerViewAllBooks.setHasFixedSize( true );

                recyclerViewAllBooks.setLayoutManager( linearLayoutManager );

                allBooksModelsList = new ArrayList<>();
                allBooksAdapter = new AllBooksAdapter( allBooksModelsList );

                recyclerViewAllBooks.setAdapter( allBooksAdapter );

                getInfo();
            }
        }
    }

    public void getInfo() {

        Query query = databaseReference.orderByChild( "privacy" ).equalTo( "public" );

        query.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.exists()) {

                    String name = (String) snapshot.child( "bookName" ).getValue();
                    String authorName = (String) snapshot.child( "authorName" ).getValue();
                    String coverUrl = (String) snapshot.child( "coverUrl" ).getValue();
                    String date = (String) snapshot.child( "date" ).getValue();
                    String desc = (String) snapshot.child( "desc" ).getValue();
                    long pageNumber = (long) snapshot.child( "number" ).getValue();
                    String pdfUrl = (String) snapshot.child( "pdfUrl" ).getValue();
                    String type = (String) snapshot.child( "type" ).getValue();
                    String uploadId = (String) snapshot.child( "uploadId" ).getValue();
                    String key = snapshot.getKey();

                    AllBooksModel model = new AllBooksModel();
                    model.authorName = authorName;
                    model.bookName = name;
                    model.coverUrl = coverUrl;
                    model.desc = desc;
                    model.date = date;
                    model.uploadId = uploadId;
                    model.type = type;
                    model.pdfUrl = pdfUrl;
                    model.number = pageNumber;
                    model.key = key;
                    model.currentUserId = currentUserId;

                    allBooksModelsList.add( model );
                    allBooksAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.all_books_menu, menu );

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService( Context.SEARCH_SERVICE );
        SearchView searchView =
                (SearchView) menu.findItem( R.id.app_bar_search_all_Books ).getActionView();
        searchView.setQueryHint( "Search by author , book name , type" );
        if (searchManager != null)
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo( getComponentName() ) );

        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i( "GOPALL", newText + "" );
                search( newText );
                return true;
            }
        } );
        return true;
    }

    private void search(String newText) {

        ArrayList<AllBooksModel> arrayList = new ArrayList<>();
        for (AllBooksModel object : allBooksModelsList) {
            if (object.getAuthorName().toLowerCase().contains( newText.toLowerCase() ) ||
                    object.getType().toLowerCase().contains( newText.toLowerCase() ) ||
                    object.getBookName().toLowerCase().contains( newText.toLowerCase() )) {

                arrayList.add( object );
            }
        }

        AllBooksAdapter dishesAdapter1 = new AllBooksAdapter( arrayList );
        recyclerViewAllBooks.setAdapter( dishesAdapter1 );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.app_bar_search_all_Books) {
            Toast.makeText( this, "Search", Toast.LENGTH_SHORT ).show();
        }

        return super.onOptionsItemSelected( item );
    }

}