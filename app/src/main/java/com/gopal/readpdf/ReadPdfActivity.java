package com.gopal.readpdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.gopal.ebookapp.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReadPdfActivity extends AppCompatActivity {

    private PDFView pdfView;
    private String pdfUrl;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentUserId;
    private Uri pdfUri;
    private ProgressBar progressBar;
    private String pdfName;
    private String bookKey;
    private String uploadId;
    private int lastPage;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_read_pdf );

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();

            pdfView = findViewById( R.id.pdfViewRead );
            progressBar = findViewById( R.id.progressBarActivityRead );

            pdfUrl = getIntent().getStringExtra( "pdfUrl" );
            bookKey = getIntent().getStringExtra( "bookKey" );
            //pdfName = getIntent().getStringExtra( "pdfName" );
            uploadId = getIntent().getStringExtra( "uploadId" ); // userId
             int defaultValue = getIntent().getIntExtra( "defaultValue", 0 );

             defaultValue = defaultValue - 1;

            Calendar calendar = Calendar.getInstance();
            final String date = DateFormat.getDateInstance().format( calendar.getTime() );

            final Context mContext = this;
            progressBar.setVisibility( View.VISIBLE );
            Toast.makeText( mContext, "Please wait loading might take time", Toast.LENGTH_LONG ).show();

            SharedPreferences settings = getSharedPreferences("settings", 0);
            final boolean isChecked = settings.getBoolean("checkbox", false);

            final int finalDefaultValue = defaultValue;
            AsyncTask.execute( new Runnable() {
                @Override
                public void run() {
                    try {
                        final InputStream input = new URL( pdfUrl ).openStream();

                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {

                                pdfView.fromStream( input )
                                        .defaultPage( finalDefaultValue )
                                        .enableSwipe( true )
                                        .swipeHorizontal( true )
                                        .pageSnap( true )
                                        .autoSpacing( true )
                                        .nightMode( isChecked )
                                        .pageFling( true )
                                        .enableAnnotationRendering( true )
                                        .scrollHandle( new DefaultScrollHandle( mContext ) )
                                        .load();
                                progressBar.setVisibility( View.INVISIBLE );
                            }
                        } );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } );

            databaseReference = FirebaseDatabase.getInstance().getReference( "RecentRead" ).child( currentUserId )
                    .child( bookKey );

            databaseReference.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        databaseReference.child( "lastDate" ).setValue( date );
                    } else {
                        Map<String, Object> dateMap = new HashMap<>();
                        dateMap.put( "date", date );
                        dateMap.put( "lastDate", date );

                        databaseReference.setValue( dateMap );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            } );
//            try {
//                readData();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
//    private void readData() throws IOException {
//        progressBar.setVisibility( View.VISIBLE );
//
//        final File outputFile = File.createTempFile( "file", "pdf" );
//
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().
//                child( "Users" ).child( uploadId ).child( "Pdf" )
//                .child( pdfName );
//
//        storageReference.getFile( outputFile ).addOnSuccessListener( new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                Toast.makeText( ReadPdfActivity.this, "File Downloaded", Toast.LENGTH_SHORT ).show();
//                recentRead( outputFile );
//            }
//        } ).addOnProgressListener( new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
//                // Add Progress
//            }
//        } );
//    }

//    private void recentRead(final File outputFile) {
//
//        databaseReference.child( "bookKey" ).setValue( bookKey ).addOnCompleteListener( new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//                if(task.isComplete())
//                if (task.isSuccessful()) {
//                    displayPdf( outputFile );
//                }
//            }
//        } );
//    }

    @Override
    protected void onStop() {
        super.onStop();
        lastPage = pdfView.getCurrentPage();
        lastPage += 1;
        databaseReference.child( "lastPage" ).setValue( lastPage );

    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//    }
//    private void displayPdf(File file) {
//
//        if (pdfView != null) {
//            progressBar.setVisibility( View.INVISIBLE );
//            pdfView.fromFile( file )
//                    .enableSwipe( true )
//                    .swipeHorizontal( true )
//                    .pageSnap( true )
//                    .autoSpacing( true )
//                    .pageFling( true )
//                    .enableAnnotationRendering( true )
//                    .scrollHandle( new DefaultScrollHandle( this ) )
//                    .load();
//            //  .defaultPage( 20 )
//        }else{
//            Toast.makeText( this, "Null", Toast.LENGTH_SHORT ).show();
//        }
//
//
//    }

}