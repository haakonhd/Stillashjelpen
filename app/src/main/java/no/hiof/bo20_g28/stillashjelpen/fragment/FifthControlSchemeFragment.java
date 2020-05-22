package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.ChecklistItem;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefect;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FifthControlSchemeFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    private View view;
    Button saveButton;
    ConstraintLayout pdfPreview;

    private Project thisProject;
    private PdfDocument document;
    private View pdfContent;
    private LayoutInflater layoutInflater;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutInflater = inflater;
        view = layoutInflater.inflate(R.layout.fragment_cs_fifth, container, false);

        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        pdfPreview = view.findViewById(R.id.pdfPreview);
        pdfPreview.setOnTouchListener((v, event) -> preventHorizontalScrollOnListClick(v, event));

        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveFile("test.pdf"));

        pdfContent = layoutInflater.inflate(R.layout.pdf_page, null);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        setPdfValues();
        pdfPreview = view.findViewById(R.id.pdfPreview);
        pdfPreview.removeAllViews();
        pdfPreview.addView(pdfContent);
    }

    private PdfDocument getPdfDocument(){
        // create a new document
        PdfDocument document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(2250, 1400, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // draw something on the page
//        View pdfContent = layoutInflater.inflate(R.layout.pdf_page, null);

        pdfContent.measure(2250, 1400);
        pdfContent.layout(0, 0, 2250, 1400);
        pdfContent.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);
        return document;
    }

    public void saveFile(String sFileName){
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, 1);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Stillashjelpen");

        document = getPdfDocument();

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

        CharSequence text = "PDF saved to the downloads folder.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getActivity(), text, duration);
        toast.show();
    }


    private void setPdfValues(){
        ControlScheme cs = thisProject.getControlScheme();

        TextView tvBuilderCompanyName = pdfContent.findViewById(R.id.tvBuilderCompanyName);
        String builderCompanyName = cs.getBuilderCompanyName();
        if(builderCompanyName != null) tvBuilderCompanyName.setText(builderCompanyName);

        TextView tvRefNum = pdfContent.findViewById(R.id.tvRefNum);
        String refNum = cs.getRefNum();
        if(refNum != null) tvRefNum.setText(refNum);

        TextView tvPlaceName = pdfContent.findViewById(R.id.tvPlaceName);
        String placeName = cs.getPlaceName();
        if(placeName != null) tvPlaceName.setText(placeName);

        String scaffoldType = cs.getScaffoldType();
        if(scaffoldType != null){
            if(scaffoldType.equals("Facade")){
                CheckBox cb = pdfContent.findViewById(R.id.cb_facade);
                cb.setChecked(true);
            }
            else if(scaffoldType.equals("Tower")){
                CheckBox cb = pdfContent.findViewById(R.id.cb_tower);
                cb.setChecked(true);
            }
            else if(scaffoldType.equals("Roller")){
                CheckBox cb = pdfContent.findViewById(R.id.cb_roll);
                cb.setChecked(true);
            }
            else if(scaffoldType.equals("Other")){
                CheckBox cb = pdfContent.findViewById(R.id.cb_other);
                cb.setChecked(true);
            }
        }

        TextView tv_length = pdfContent.findViewById(R.id.tv_length);
        String length = cs.getScaffoldLength();
        if(length != null) tv_length.setText("Lengde: " + length);

        TextView tv_bredth = pdfContent.findViewById(R.id.tv_bredth);
        String bredth = cs.getScaffoldWidth();
        if(bredth != null) tv_bredth.setText("Bredde: " + bredth);

        TextView tv_height = pdfContent.findViewById(R.id.tv_height);
        String height = cs.getScaffoldHeight();
        if(height != null) tv_height.setText("Høyde: " + height);

        TextView tv_builtByName = pdfContent.findViewById(R.id.tv_builtByName);
        String builtByName = cs.getBuiltByName();
        if(builtByName != null) tv_builtByName.setText(builtByName);

        TextView tv_firm = pdfContent.findViewById(R.id.tv_firm);
        String firm = cs.getUserCompanyName();
        if(firm != null) tv_firm.setText(firm);

        TextView tv_tlf = pdfContent.findViewById(R.id.tv_tlf);
        String tlf = cs.getUserPhoneNum();
        if(tlf != null) tv_tlf.setText(tlf);

        TextView tv_contact_person = pdfContent.findViewById(R.id.tv_contact_person);
        String contactPerson = cs.getContactPersonName();
        if(contactPerson != null) tv_contact_person.setText(contactPerson);

        TextView tv_builder_control = pdfContent.findViewById(R.id.tv_builder_control);
        String builderControlDate = cs.getBuilderControlDate();
        String builderControlName = cs.getBuilderNameControlName();
        if(builderControlDate != null && builderControlName != null) tv_builder_control.setText(builderControlDate + " - " + builderControlName);

        TextView tv_user_control = pdfContent.findViewById(R.id.tv_user_control);
        String userControlDate = cs.getUserControlDate();
        String userControlName = cs.getUserNameControlName();
        if(userControlDate != null && userControlName != null) tv_user_control.setText(userControlDate + " - " + userControlName);

        TextView tv_num_of_wall_anchors = pdfContent.findViewById(R.id.tv_num_of_wall_anchors);
        String numOfWallAnchors = cs.getNumOfWallAnchors();
        if(numOfWallAnchors != null && userControlName != null) tv_num_of_wall_anchors.setText(numOfWallAnchors);

        TextView tv_num_of_wall_anchor_tests = pdfContent.findViewById(R.id.tv_num_of_wall_anchor_tests);
        String numOfWallAnchorTests = cs.getNumOfWallAnchorTests();
        if(numOfWallAnchorTests != null && userControlName != null) tv_num_of_wall_anchor_tests.setText(numOfWallAnchorTests);

        TextView tv_wall_anchor_holds = pdfContent.findViewById(R.id.tv_wall_anchor_holds);
        String wallAnchorHolds = cs.getWallAnchorHolds();
        if(wallAnchorHolds != null && userControlName != null) tv_wall_anchor_holds.setText(wallAnchorHolds);

        TextView tv_wallAnchorTestResult = pdfContent.findViewById(R.id.tv_wallAnchorTestResult);
        String anchorTestResult = cs.getWallAnchorTestResult();
        if(anchorTestResult != null && userControlName != null) tv_wallAnchorTestResult.setText(anchorTestResult);

        fillCheckList();


    }

    private void fillCheckList(){
        CheckBox cb1 = pdfContent.findViewById(R.id.cb_1);
        CheckBox cb2 = pdfContent.findViewById(R.id.cb_2);
        CheckBox cb3 = pdfContent.findViewById(R.id.cb_3);
        CheckBox cb4 = pdfContent.findViewById(R.id.cb_4);
        CheckBox cb5 = pdfContent.findViewById(R.id.cb_5);
        CheckBox cb5a = pdfContent.findViewById(R.id.cb_5a);
        CheckBox cb5b = pdfContent.findViewById(R.id.cb_5b);
        CheckBox cb5c = pdfContent.findViewById(R.id.cb_5c);
        CheckBox cb5d = pdfContent.findViewById(R.id.cb_5d);
        CheckBox cb6 = pdfContent.findViewById(R.id.cb_6);
        CheckBox cb7 = pdfContent.findViewById(R.id.cb_7);
        CheckBox cb8 = pdfContent.findViewById(R.id.cb_8);
        CheckBox cb9 = pdfContent.findViewById(R.id.cb_9);
        CheckBox cb10 = pdfContent.findViewById(R.id.cb_10);

        ArrayList<ChecklistItem> checklistItems = thisProject.getControlScheme().getChecklistItems();
        if(checklistItems == null) return;

        for(ChecklistItem item : checklistItems){
            if(item.isChecked() && item.getId() == 1) cb1.setChecked(true);
            else if(item.isChecked() && item.getId() == 2) cb2.setChecked(true);
            else if(item.isChecked() && item.getId() == 3) cb3.setChecked(true);
            else if(item.isChecked() && item.getId() == 4) cb4.setChecked(true);
            else if(item.isChecked() && item.getId() == 5) cb5.setChecked(true);
            else if(item.isChecked() && item.getId() == 6) cb5a.setChecked(true);
            else if(item.isChecked() && item.getId() == 7) cb5b.setChecked(true);
            else if(item.isChecked() && item.getId() == 8) cb5c.setChecked(true);
            else if(item.isChecked() && item.getId() == 9) cb5d.setChecked(true);
            else if(item.isChecked() && item.getId() == 10) cb6.setChecked(true);
            else if(item.isChecked() && item.getId() == 11) cb7.setChecked(true);
            else if(item.isChecked() && item.getId() == 12) cb8.setChecked(true);
            else if(item.isChecked() && item.getId() == 13) cb9.setChecked(true);
            else if(item.isChecked() && item.getId() == 14) cb10.setChecked(true);
        }
    }

    private void fillDefectsFoundTable(){
        ArrayList<ControlSchemeDefect> defects = thisProject.getControlScheme().getControlSchemeDefects();
        for(ControlSchemeDefect defect : defects){

        }
    }

    private boolean preventHorizontalScrollOnListClick(View v, MotionEvent event){
        int action = event.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        // Handle Seekbar touch events.
        v.onTouchEvent(event);
        return true;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
    }
}
