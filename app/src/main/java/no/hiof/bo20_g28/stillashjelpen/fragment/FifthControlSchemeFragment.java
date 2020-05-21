package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FifthControlSchemeFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    private View view;
    TextView textView;

    private Project thisProject;



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cs_fifth, container, false);

        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        textView = view.findViewById(R.id.cs5TestTextView);
        textView.setText(getResources().getString(R.string.tab_cs_fourth) + " for prosjekt: " + thisProject.getProjectName());

        PdfDocument document = getPdfDocument(inflater);
        saveFile("test.pdf", document);

        return view;
    }

    private PdfDocument getPdfDocument(LayoutInflater inflater){
        // create a new document
        PdfDocument document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(2250, 1400, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // draw something on the page
        View content = inflater.inflate(R.layout.pdf_page, null);

        content.measure(2250, 1400);
        content.layout(0, 0, 2250, 1400);
        content.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);
        return document;
    }

    public void saveFile(String sFileName, PdfDocument document){
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, 1);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Stillashjelpen");

        if(!file.exists()){
            file.mkdir();
        }
        File gpxfile = new File(file, sFileName);
        try {
            document.writeTo(new FileOutputStream(gpxfile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        document.close();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }
}
