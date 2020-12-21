package com.gopal.ebookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_about_us );

        TextView emailIntent = findViewById( R.id.emailIntent );
        TextView privacy = findViewById( R.id.privacy );
        TextView terms  = findViewById( R.id.termsandcondition );
        TextView noResult = findViewById( R.id.noResultLink );
        TextView iconLink = findViewById( R.id.iconLink );

        iconLink.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( "https://www.flaticon.com/authors/freepik" ) );
                startActivity( intent );
            }
        } );

        noResult.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( "https://dribbble.com/AnnaGolde" ) );
                startActivity( intent );
            }
        } );

        emailIntent.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"devjunctionofficial@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Book issue");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AboutUsActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        } );

        privacy.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( "https://bookholic.flycricket.io/privacy.html" ) );
                startActivity( intent );
            }
        } );

        terms.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( "https://bookholic.flycricket.io/terms.html" ) );
                startActivity( intent );
            }
        } );

    }

}