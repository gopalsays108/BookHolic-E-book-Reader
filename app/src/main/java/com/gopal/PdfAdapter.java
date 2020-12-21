package com.gopal;

import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gopal.ebookapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class PdfAdapter extends ArrayAdapter<File> {

    Context context;
    ViewHolder viewHolder; // object of Fclass viewholder is declared here
    ArrayList<File> arrayListPdf;

    public PdfAdapter(Context context, ArrayList<File> arrayListPdf) {
        super( context, R.layout.single_upload_pdf_layout, arrayListPdf );
        this.context = context;
        this.arrayListPdf = arrayListPdf;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (arrayListPdf.size() > 0) {
            return arrayListPdf.size();
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from( getContext() ).inflate( R.layout.single_upload_pdf_layout, parent, false );
            viewHolder = new ViewHolder();
            viewHolder.fileName = convertView.findViewById( R.id.pdfName );
            convertView.setTag( viewHolder );

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.fileName.setText( arrayListPdf.get( position ).getName() );

        viewHolder.fileName.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText( context, "GOPAl =>" + arrayListPdf.get( position ).getName(), Toast.LENGTH_SHORT ).show();
                try {
                    int number = countPages( arrayListPdf.get( position ).getAbsoluteFile() );
                    Intent uploadIntent = new Intent( getContext(), UploadBooksActivity.class );
                    uploadIntent.putExtra( "number", number );
                    uploadIntent.putExtra( "filepath", arrayListPdf.get( position ).getPath() );
                    uploadIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

                    context.startActivity( uploadIntent );
                } catch (IOException e) {
                    String error = e.getLocalizedMessage();
                    if (error != null)
                        Log.i( "ERROR", error );

                }
            }
        } );


        //Log.i( "goPAL" , String.valueOf( arrayListPdf.get( position ). ) );
        return convertView;
    }

    public static class ViewHolder {
        TextView fileName;
    }

    // page count only open document
    private int countPages(File pdfFile) throws IOException {
        int totalpages = 0;
        try {
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open( pdfFile, ParcelFileDescriptor.MODE_READ_ONLY );
            PdfRenderer pdfRenderer = null;
            pdfRenderer = new PdfRenderer( parcelFileDescriptor );
            totalpages = pdfRenderer.getPageCount();

            Log.i( "COUNTT", String.valueOf( totalpages ) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return totalpages;
    }
}

