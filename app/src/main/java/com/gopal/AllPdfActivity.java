package com.gopal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gopal.ebookapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class AllPdfActivity extends AppCompatActivity {

    private ListView pdf;
    public static ArrayList<File> filesList = new ArrayList<>();
    PdfAdapter objAdapter;
    private final int REQUEST_PERMISSION = 1;
    boolean aBooleanPermission;
    private EditText fromStorage;
    File dir;
    private final String TAG = "MYTAg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_all_pdf );

        Intent intent=getIntent();
        fromStorage = findViewById( R.id.fromStorage );
        pdf = findViewById( R.id.listViewPdf );

        if(intent!=null) {

            String action = intent.getAction();

            String type = intent.getType();

            String filepath = "";
            if (type != null)
            if (Intent.ACTION_VIEW.equals( action ) && type.endsWith( "pdf" )) {

                // Get the file from the intent object

                Uri file_uri = intent.getData();

                if (file_uri != null) {
                    filepath = file_uri.getPath();
                    Toast.makeText( this, "yo" + filepath, Toast.LENGTH_SHORT ).show();
                }
                else {
                    filepath = "No file";
                    Toast.makeText( this, "yo" + filepath, Toast.LENGTH_SHORT ).show();
                }
            } else if (Intent.ACTION_SEND.equals( action ) && type.endsWith( "pdf" )) {

                Uri uri = intent.getParcelableExtra( Intent.EXTRA_STREAM );

                filepath = uri.getLastPathSegment();

                if (filepath != null) {

                    final String finalFilepath = filepath;
                    final String finalFilepath1 = filepath;
                    new Handler(  ).postDelayed( new Runnable() {
                        @Override
                        public void run() {
                            search( finalFilepath.toLowerCase() );
                            fromStorage.setText( finalFilepath1.toLowerCase() );
                            File file = new File( finalFilepath );
                        }
                    } , 800);

//
//                    File file1 = file.getAbsoluteFile();
//
////                    File file2 = file.getAbsoluteFile();
//                    Toast.makeText( this, "yo" + filepath + "\n" + file1 + "\n" + file2 , Toast.LENGTH_SHORT ).show();
//                    Log.i( "LOG" , "yo" + filepath + "\n" + file1 + "\n" + file2 );
                }
            }
        }else{
            Toast.makeText( this, "Empty" + intent, Toast.LENGTH_SHORT ).show();
        }

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle( "Choose the file" );
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        }

        fromStorage.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    search( s );
                }else{
                    getFile( dir );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

       // dir = new File( Environment.getExternalStorageDirectory().toString() );
        dir = new File( Environment.getExternalStorageDirectory().toString() );
        permissionFn();
    }


//    private void uploadPdfFromStorage() {
//        checkPermissions();
//    }
//
//    private void checkPermissions() {
//        if (ActivityCompat.checkSelfPermission( AllPdfActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission( AllPdfActivity.this , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
//            selectFile();
//        } else {
//            ActivityCompat.requestPermissions( AllPdfActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102 );
//        }
//    }

//    private void selectFile() {
//        //String path = String.valueOf( dir );
//        Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
//       // intent.setDataAndType(uri  , "application/pdf" );
//        intent.setType( "application/pdf" );
//        intent.addCategory( Intent.CATEGORY_OPENABLE );
//        startActivityForResult( Intent.createChooser( intent, "Select file" ), 222 );
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult( requestCode, resultCode, data );
//
//        if (requestCode == 222) {
//            if (resultCode == RESULT_OK) {
//                if (data != null && data.getData() != null) {
////
////                  String filePaths = data.getData().getLastPathSegment();
//////
//////                    File file = new File( data.getData().getPath() );
//////                    File abs = file.getAbsoluteFile();
////
////                    Uri uri = getIntent().getData();
////                    File file = new File( uri.getPath() );
////                    File filePath = file.getAbsoluteFile();
////                    Log.i( "Tags" , filePath + " \n " + file + "\n "  );
////
////                    if (filePath != null)
////                    if (filePaths.endsWith( ".pdf" )) {
////                        try {
////                            getCountAndUpload(filePath);
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////                    } else {
////                        Toast.makeText( getApplicationContext(), "Select From Internal Storage", Toast.LENGTH_SHORT ).show();
////                    }
//
////                    Uri uri = data.getData();
////                    Log.i( "URI" , "gopall " + uri );
////                    String type = data.getType();
////                    Log.i("TAG","Pick completed: "+ uri + " "+type);
////
////                    if (uri != null)
////                    {
////                        String path = uri.toString();
////                        Toast.makeText( this, "pah " + path, Toast.LENGTH_SHORT ).show();
////                        Log.i( "PATH : " , path );
////                        if (path.toLowerCase().startsWith("file://"))
////                        {
////                            // Selected file/directory path is below
////                            File files;
////                            path = (new File( URI.create(path))).getAbsolutePath();
////                            Log.i("TAG","\nPick completed: "+ uri + " "+type+ " \npth"+path);
////                            Toast.makeText( this, "yoho", Toast.LENGTH_SHORT ).show();
////                          //  getCountAndUpload( path );
////                        }else{
////                            Toast.makeText( this, "1", Toast.LENGTH_SHORT ).show();
////                        }
////
////                    }else{
////                        Toast.makeText( this, "2", Toast.LENGTH_SHORT ).show();
////
////                    }
//               Uri filePath = data.getData();
//               File cache = null;
//                    cache = new File(
//                            android.os.Environment.getExternalStorageDirectory().toString());
//               String fileName = String.valueOf( filePath.hashCode() );
//                    File f = null;
//                    try {
//                        f = File.createTempFile( fileName , ".pdf" , cache );
//                        getCountAndUpload( f.getAbsoluteFile() );
//                        Log.i( "TAG", "onActivityResult: " + f.getAbsolutePath()  );
//                        //     Toast.makeText( this, "File : " + f.getName(), Toast.LENGTH_SHORT ).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Toast.makeText( this, "File :  Filed " + e.getLocalizedMessage() , Toast.LENGTH_SHORT ).show();
//                        Log.i( "TAG", "onActivityResult: " + e.getLocalizedMessage()   + "  " + "\n" + f.getAbsolutePath());
//                        e.printStackTrace();
//                    }
                  //  Toast.makeText( this, "Gopal : "  + filePath, Toast.LENGTH_SHORT ).show();

//               File file1 = new File( String.valueOf( f ) );
//
//               File absolute = new File( String.valueOf( file1.getAbsoluteFile() ) );
//
//               Log.i( "India " ,  "path: " + file1 + "  \nabs " + absolute );
//                    try {
//                        getCountAndUpload( file1.getAbsoluteFile() );
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    private void getCountAndUpload(File filePath) throws IOException {
//        int totalPages = 0;
//        try {
//            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open( filePath, ParcelFileDescriptor.MODE_READ_ONLY );
//            PdfRenderer pdfRenderer = null;
//            pdfRenderer = new PdfRenderer( parcelFileDescriptor );
//            totalPages = pdfRenderer.getPageCount();
//            Toast.makeText( this, "Success", Toast.LENGTH_SHORT ).show();
//            Log.i( "INFOs ", "NUber = " + totalPages + " path " + filePath );
//        } catch (FileNotFoundException e) {
//            Toast.makeText( getApplicationContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
//        }
//    }

    private void permissionFn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( AllPdfActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION );

            } else {
                if (ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText( this, "Permission Denied", Toast.LENGTH_SHORT ).show();
                } else {
                    aBooleanPermission = true;
                    getFile( dir );
                    objAdapter = new PdfAdapter( getApplicationContext(), filesList );
                    pdf.setAdapter( objAdapter );
                }
            }
        } else {
            // ---------- No runtime permission required below marshmallow-----------
            aBooleanPermission = true;
            getFile( dir );
            objAdapter = new PdfAdapter( getApplicationContext(), filesList );
            pdf.setAdapter( objAdapter );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                aBooleanPermission = true;
                getFile( dir );
                objAdapter = new PdfAdapter( getApplicationContext(), filesList );
                pdf.setAdapter( objAdapter );
            } else {
                Toast.makeText( this, "Allow the Permission", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    private void search(CharSequence s) {

        ArrayList<File> arrayList =new ArrayList<>(  );

        for (File ignored : filesList){

            for (int i = 0 ; i < filesList.size() ; i++){
                if (filesList.get( i ).getName().toLowerCase().contains( s )){
                    if(!arrayList.contains( filesList.get( i ) )){
                        arrayList.add( filesList.get( i ) );
                    }
                }
            }
            ImageView imageView = findViewById( R.id.noSearchBooks );
            TextView textView = findViewById( R.id.noResult );
            if (arrayList.isEmpty()){
                imageView.setVisibility( View.VISIBLE );
                textView.setVisibility( View.VISIBLE );
            }else{
                textView.setVisibility( View.INVISIBLE );
                imageView.setVisibility( View.INVISIBLE );
            }
            PdfAdapter pdfAdapter ;
            pdfAdapter = new PdfAdapter( getApplicationContext(), arrayList );
            pdf.setAdapter( pdfAdapter );
        }
        Collections.sort( arrayList );
    }

    private void getFile(File dir) {

        File[] listFile = dir.listFiles();

        if (listFile != null && listFile.length > 0) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    getFile( file );
                } else {
                    boolean booleanPdf = false;
                    if (file.getName().endsWith( ".pdf" )) {
                        for (int j = 0; j < filesList.size(); j++) {
                            if (filesList.get( j ).getName().equals( file.getName() )) {

                                booleanPdf = true;

                            }
                        }
                        if (booleanPdf) {
                            booleanPdf = false;
                        } else {
                            filesList.add( file );
                        }
                    }
                }
            }
        }
        Collections.sort( filesList );
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected( item );
    }
}