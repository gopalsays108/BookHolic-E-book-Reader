package com.gopal;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gopal.ebookapp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class UploadBooksActivity extends AppCompatActivity {

    private EditText editPdfName;
    private EditText pdfAuthorName;
    private EditText pdfDescription;
    private EditText pdfTags;
    private ImageView pdfBookPhoto;
    private TextView numberOfPages;
    private Button btnUpload;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String currentUserId;
    private Uri uploadPdfFile;
    private Uri choosedImageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String filepath;
    private String pdfUrl;
    private String bookCoverPhotoUrl;
    private String bookName;
    private String authorNae;
    private String bookDesc;
    private int number;
    private String date;
    private ProgressBar progressBar;
    private static final int PERMISSION_CODE = 101;
    private String item;
    private String privacy = null;
    private String imageUrl = null;

    private String[] category = {"Choose Category", "Romance", "Thrill", "Action", "Self development", "Others"};

    public UploadBooksActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_upload_books_a_ctivity );

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();

            number = getIntent().getIntExtra( "number", 0 );
            filepath = getIntent().getStringExtra( "filepath" );

            editPdfName = findViewById( R.id.pdfName );
            btnUpload = findViewById( R.id.btnUpload );
            pdfAuthorName = findViewById( R.id.pdfAuthorName );
            pdfDescription = findViewById( R.id.pdfBookDescription );
            pdfTags = findViewById( R.id.pdfBookCategory );
            pdfBookPhoto = findViewById( R.id.pdfUploadImageCover );
            numberOfPages = findViewById( R.id.noOfPages );
            progressBar = findViewById( R.id.progressBar );

            numberOfPages.setText( String.format( "%s %s",
                    getApplicationContext().getResources().getString( R.string.no_of_pages ), number ) );

            Calendar calendar = Calendar.getInstance();
            date = DateFormat.getDateInstance().format( calendar.getTime() );

            // Spinner element
            Spinner spinner = findViewById( R.id.spinner );

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_item, category );

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

            spinner.setAdapter( dataAdapter );

            spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    item = parent.getItemAtPosition( position ).toString();
                    pdfTags.setText( item );
//                    Toast.makeText( this, category[position], Toast.LENGTH_SHORT ).show();
                    // Showing selected spinner item
                    //Toast.makeText( parent.getContext(), "Selected: " + item , Toast.LENGTH_LONG ).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            } );

            storageReference = FirebaseStorage.getInstance().getReference();
            databaseReference = FirebaseDatabase.getInstance().getReference( "Uploads" ).push();

            btnUpload.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    bookName = editPdfName.getText().toString();
                    authorNae = pdfAuthorName.getText().toString();
                    bookDesc = pdfDescription.getText().toString();

                    if (bookName.isEmpty()) {
                        editPdfName.setError( "Enter Book Name" );
                        editPdfName.requestFocus();
                    } else if (authorNae.isEmpty()) {
                        pdfAuthorName.setError( "Enter Author Name" );
                        pdfAuthorName.requestFocus();
                    } else if (bookDesc.isEmpty()) {
                        pdfDescription.setError( "Enter Description" );
                        pdfDescription.requestFocus();
                    } else if (item.equals( "Choose Category" )) {
                        Toast.makeText( UploadBooksActivity.this, "Select Book Category", Toast.LENGTH_SHORT ).show();
                    }else {
                        CharSequence[] option = new CharSequence[]{"Upload for all", "Upload for me"};

                        AlertDialog.Builder builder = new AlertDialog.Builder( UploadBooksActivity.this );
                        builder.setTitle( "Select an option" );
                        builder.setItems( option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 0) {
                                    privacy = "public";
                                    progressBar.setVisibility( View.VISIBLE );
                                    btnUpload.setEnabled( false );
                                    startUploadingImage();

                                } else if (which == 1) {
                                    privacy = "private";
                                    progressBar.setVisibility( View.VISIBLE );
                                    btnUpload.setEnabled( false );
                                    startUploadingImage();
                                }
                            }
                        } );
                        builder.show();
                    }
                }
            } );

            pdfBookPhoto.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkCameraPermission();
                }
            } );

        }

    }

    //    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        // On selecting a spinner item
//        String item = parent.getItemAtPosition( position ).toString();
//
//        Toast.makeText( this, category[position], Toast.LENGTH_SHORT ).show();
//        // Showing selected spinner item
//        Toast.makeText( parent.getContext(), "Selected: " + item , Toast.LENGTH_LONG ).show();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> arg0) {
//        // TODO Auto-generated method stub
//    }
    private void checkCameraPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission( Manifest.permission.CAMERA ) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_DENIED) {

                //permission not enabled request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions( permission, PERMISSION_CODE );

            } else {
                CropImage.activity().setGuidelines( CropImageView.Guidelines.ON )
                        .setCropShape( CropImageView.CropShape.OVAL )
                        .setAspectRatio( 1, 1 )
                        .start( this );
            }

        } else {

            CropImage.activity().setGuidelines( CropImageView.Guidelines.ON )
                    .setCropShape( CropImageView.CropShape.OVAL )
                    .setAspectRatio( 1, 1 )
                    .start( this );
        }
    }

    //------ To retrieve Image after intent-----------
    //-----this methods is called when user presses allow or deny from permission request popup
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult( data );
            if (resultCode == RESULT_OK) {
                if (result != null)
                    choosedImageUri = result.getUri();
                pdfBookPhoto.setImageURI( choosedImageUri );
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                if (result != null) {
                    Exception error = result.getError();
                    Toast.makeText( this, error.getMessage(), Toast.LENGTH_LONG ).show();
                }
            }
        }
    }

    private void startUploadingImage() {

        if (choosedImageUri != null) {

            if (choosedImageUri.getPath() != null) {
                File thumb_filePath = new File( choosedImageUri.getPath() );

                Bitmap thumb_bitmap = null;

                try {
                    thumb_bitmap = new Compressor( this )
                            .setMaxWidth( 200 )
                            .setMaxHeight( 200 )
                            .setQuality( 50 )
                            .compressToBitmap( thumb_filePath );

                } catch (IOException e) {
                    e.printStackTrace();//nikal acticity yehi hai dishhh vaala
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                if (thumb_bitmap != null)
                    thumb_bitmap.compress( Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream );
                byte[] final_Image = byteArrayOutputStream.toByteArray();

                final StorageReference storageReference1 = FirebaseStorage.getInstance().getReference();

                if (choosedImageUri.getLastPathSegment() != null) {

                    final StorageReference imagereference1 = storageReference1.child( "Users" ).child( currentUserId ).child( "Pdf" )
                            .child( "bookCover" )
                            .child( choosedImageUri.getLastPathSegment() );

                    UploadTask uploadTask = imagereference1.putBytes( final_Image );
                    uploadTask.addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagereference1.getDownloadUrl().addOnCompleteListener( new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult() != null)
                                            bookCoverPhotoUrl = task.getResult().toString();
                                        Log.i( "DOWNLOAD", "=>>" + bookCoverPhotoUrl );
                                        uploadPdfFile();

                                    } else {
                                        if (task.getException() != null) {
                                            progressBar.setVisibility( View.INVISIBLE );
                                            btnUpload.setEnabled( true );
                                            String error = task.getException().getLocalizedMessage();
                                            Toast.makeText( getApplicationContext(), error, Toast.LENGTH_SHORT ).show();
                                        }
                                    }
                                }
                            } );
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility( View.INVISIBLE );
                            btnUpload.setEnabled( true );
                            Toast.makeText( getApplicationContext(), e.toString(), Toast.LENGTH_SHORT ).show();
                        }
                    } );
                } else {
                    progressBar.setVisibility( View.INVISIBLE );
                    btnUpload.setEnabled( true );
                    Toast.makeText( this, "An Error Occurred \nTry Again", Toast.LENGTH_SHORT ).show();
                }
            } else {
                progressBar.setVisibility( View.INVISIBLE );
                btnUpload.setEnabled( true );
                Toast.makeText( this, "Try Again", Toast.LENGTH_SHORT ).show();
            }

        } else {
            imageUrl = "default";
            uploadPdfFile();
        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
//        if (requestCode == 101) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                selectPdfFile();
//            } else {
//                Toast.makeText( this, "Please Grant Permission!", Toast.LENGTH_SHORT ).show();
//            }
//        }
//    }
//    private void selectPdfFile() {
//
//        Intent intent = new Intent();
//        intent.setType( "application/pdf" );
//        intent.setAction( Intent.ACTION_GET_CONTENT );
//        startActivityForResult( Intent.createChooser( intent, "Select Pdf File" ), 1 );
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult( requestCode, resultCode, data );
//        if (requestCode == 1 && resultCode == RESULT_OK
//                && data != null && data.getData() != null) {
//
//            uploadPdfFile( data.getData() );
//
//
//
//            File file = new File( data.getData().getPath() );
//
//            File[] sdf = file.listFiles();
//
//            try {
//                countPages( dir );
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

    //
//    // page count only open document
//    private void countPages(File pdfFile) throws IOException {
//        try {
//            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open( pdfFile, ParcelFileDescriptor.MODE_READ_ONLY );
//            PdfRenderer pdfRenderer = null;
//            pdfRenderer = new PdfRenderer( parcelFileDescriptor );
//            int totalpages = pdfRenderer.getPageCount();
//
//            Toast.makeText( this, "Count =>" + totalpages, Toast.LENGTH_SHORT ).show();
//            Log.i( "COUNTT", String.valueOf( totalpages ) );
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Log.i( "COUNT3", e.getLocalizedMessage() );
//            Toast.makeText( this, e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
//        }
//    }
    private long random = System.currentTimeMillis();

    // -------todo: uploaded pdf file--------------
    private void uploadPdfFile() {

        Uri file = Uri.fromFile( new File( filepath ) );
        final StorageReference pdfReference = storageReference.child( "Users" ).child( currentUserId ).child( "Pdf" )
                .child( String.valueOf( random ) );

        pdfReference.putFile( file )
                .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        pdfReference.getDownloadUrl().addOnCompleteListener( new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null) {
                                        pdfUrl = task.getResult().toString();
                                        uploadDatabase();
                                    }

                                } else {
                                    progressBar.setVisibility( View.INVISIBLE );
                                    btnUpload.setEnabled( true );
                                    Toast.makeText( UploadBooksActivity.this, "Failed", Toast.LENGTH_SHORT ).show();

                                }

                            }
                        } );
                        progressBar.setVisibility( View.INVISIBLE );
                        btnUpload.setEnabled( true );
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressBar.setVisibility( View.INVISIBLE );
                        btnUpload.setEnabled( true );
                        Toast.makeText( UploadBooksActivity.this, "Failed", Toast.LENGTH_SHORT ).show();
                        // ...
                    }
                } );

    }

    private void uploadDatabase() {

        if (bookCoverPhotoUrl == null){
            finalUpload(imageUrl);
        }else{
            finalUpload( bookCoverPhotoUrl );
        }

    }

    private void finalUpload(String coverUrl) {

        Map<String, Object> uploadMap = new HashMap<>();
        uploadMap.put( "pdfUrl", pdfUrl );
        uploadMap.put( "coverUrl", coverUrl );
        uploadMap.put( "bookName", bookName );
        uploadMap.put( "authorName", authorNae );
        uploadMap.put( "desc", bookDesc );
        uploadMap.put( "type", item );
        uploadMap.put( "number", number );
        uploadMap.put( "date", date );
        uploadMap.put( "timestamp", ServerValue.TIMESTAMP );
        uploadMap.put( "uploadId", currentUserId );
        uploadMap.put( "privacy", privacy );

        databaseReference.setValue( uploadMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility( View.INVISIBLE );
                    btnUpload.setEnabled( true );
                    Toast.makeText( UploadBooksActivity.this, "Uploaded", Toast.LENGTH_SHORT ).show();
                    finish();
                } else {
                    progressBar.setVisibility( View.INVISIBLE );
                    btnUpload.setEnabled( true );
                    Toast.makeText( UploadBooksActivity.this, "Try Again", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

    }

}


//        final ProgressDialog progressDialog = new ProgressDialog( this );
//        progressDialog.setTitle( "UPloading..." );
//
//        progressDialog.show();
//
//
//        StorageReference reference = storageReference.child( "PDF" + System.currentTimeMillis() + ".pdf" );
//        reference.putFile( data ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText( UploadBooksActivity.this, "Done", Toast.LENGTH_SHORT ).show();
//                progressDialog.dismiss();
//            }
//        } ).addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//
//                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//
//                progressDialog.setMessage( "Uploaded : " + (int) progress + "%" );
//
//            }
//        } );