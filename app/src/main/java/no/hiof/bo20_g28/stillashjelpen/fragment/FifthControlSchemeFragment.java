package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.ChecklistItem;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefect;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefectFixed;
import no.hiof.bo20_g28.stillashjelpen.model.Project;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FifthControlSchemeFragment extends Fragment {
    public static final String ARG_OBJECT = "object";
    private View view;
    Button saveButton;
    Button sendEmailButton;
    ConstraintLayout pdfPreview;

    private Project thisProject;
    private PdfDocument document;
    private View pdfContent;
    private LayoutInflater layoutInflater;
    private Bitmap bitmap;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutInflater = inflater;
        view = layoutInflater.inflate(R.layout.fragment_cs_fifth, container, false);

        Intent i = getActivity().getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        pdfPreview = view.findViewById(R.id.pdfPreview);
        pdfPreview.setOnTouchListener((v, event) -> preventHorizontalScrollOnListClick(v, event));

        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveFile(thisProject.getProjectName() + ".pdf"));

        sendEmailButton = view.findViewById(R.id.sendMailButton);
        sendEmailButton.setOnClickListener(v -> openSendEmailDialog());

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

    private void openSendEmailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        EditText mailEditText = new EditText(getActivity());
        mailEditText.setHint("eksempel@email.no");
        mailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setTitle("Send pdf til mail");

        builder.setView(mailEditText);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = mailEditText.getText().toString();
                if(email.length() > 5)
                    sendEmail(email);
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void sendEmail(String email){
        document = createPdf();

        saveFileToTemp(thisProject.getProjectName() + ".pdf");
        File folder = new File(getActivity().getExternalCacheDir(), "stillashjelpen_temp");
        File file = new File(folder, thisProject.getProjectName() + ".pdf");

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent .setType("message/rfc822");
        String to[] = {email};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Rapportskjema for " + thisProject.getProjectName());
        if (!file.exists() || !file.canRead()) {
            CharSequence text = "An unknown error has occured. Could not send email.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getActivity(), text, duration);
            toast.show();
            return;
        }
        Uri uri = Uri.fromFile(file);
//        Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider",file);
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        startActivity(Intent.createChooser(emailIntent , "Send rapportskjema som mail"));
    }

    public void saveFile(String fileName){
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, 1);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Stillashjelpen");

//        document = getPdfDocument();
        document = createPdf();
        if(!file.exists()){
            file.mkdir();
        }
        File gpxfile = new File(file, fileName);
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

    private void saveFileToTemp(String fileName){
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, 1);
        File file = new File(getActivity().getExternalCacheDir(), "stillashjelpen_temp");
        document = createPdf();
        if(!file.exists()){
            file.mkdir();
        }
        File gpxfile = new File(file, fileName);
        try {
            document.writeTo(new FileOutputStream(gpxfile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        document.close();
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    private PdfDocument createPdf() {
        bitmap = loadBitmapFromView(pdfContent, pdfContent.getWidth(), pdfContent.getHeight());
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;
        int convertHight = (int) hight, convertWidth = (int) width;
        //        Resources mResources = getResources();
        //        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHight, true);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        return document;
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
        fillDefectsFoundTable();
        fillDefectsFixedTable();

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

    private String getReadableDate(Date date){
        String dateText;
        Format formatter;
        formatter = new SimpleDateFormat("dd/mm/yyyy");
        return formatter.format(date);
    }

    private void fillDefectsFoundTable(){
        ArrayList<ControlSchemeDefect> defects = thisProject.getControlScheme().getControlSchemeDefects();
        TableLayout table = pdfContent.findViewById(R.id.defectsTable);
        table.removeAllViews();

        int remainingRows = 13;

        // creating first row with descriptions
        TableRow firstRow = new TableRow(getActivity());

        TextView dateTextView1 = new TextView(getActivity());
        dateTextView1.setText("Dato");
        dateTextView1.setTypeface(dateTextView1.getTypeface(), Typeface.BOLD);
        dateTextView1.setPadding(8,8,5,12);
        dateTextView1.setBackgroundResource(R.drawable.border_black);

        TextView defectDescriptionTextView1 = new TextView(getActivity());
        defectDescriptionTextView1.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        defectDescriptionTextView1.setText("Tekst - mangler");
        defectDescriptionTextView1.setTypeface(defectDescriptionTextView1.getTypeface(), Typeface.BOLD);
        defectDescriptionTextView1.setPadding(8,8,5,12);
        defectDescriptionTextView1.setBackgroundResource(R.drawable.border_black);

        firstRow.addView(dateTextView1);
        firstRow.addView(defectDescriptionTextView1);

        remainingRows--;
        table.addView(firstRow);

        //Creating rows for the defects found
        for(ControlSchemeDefect defect : defects){
            TableRow row = new TableRow(getActivity());

            TextView dateTextView = new TextView(getActivity());
//            dateTextView.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            dateTextView.setText(getReadableDate(defect.getfoundDate()));
            dateTextView.setPadding(8,8,5,8);
            dateTextView.setBackgroundResource(R.drawable.border_black);

            TextView defectDescriptionTextView = new TextView(getActivity());
            defectDescriptionTextView.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            defectDescriptionTextView.setText(defect.getDefectDescription());
            defectDescriptionTextView.setPadding(8,8,5,8);
            defectDescriptionTextView.setBackgroundResource(R.drawable.border_black);

            row.addView(dateTextView);
            row.addView(defectDescriptionTextView);

            remainingRows--;
            table.addView(row);
        }

        //filling out the empty spaces
        for(int i = 0; i < remainingRows; i++){
            TableRow row = new TableRow(getActivity());

            TextView dateTextView = new TextView(getActivity());
            dateTextView.setPadding(8,2,5,8);
            dateTextView.setBackgroundResource(R.drawable.border_black);

            TextView defectDescriptionTextView = new TextView(getActivity());
            defectDescriptionTextView.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            defectDescriptionTextView.setPadding(8,2,5,8);
            defectDescriptionTextView.setBackgroundResource(R.drawable.border_black);

            row.addView(dateTextView);
            row.addView(defectDescriptionTextView);

            table.addView(row);
        }
    }

    private void fillDefectsFixedTable(){
        ArrayList<ControlSchemeDefectFixed> defects = thisProject.getControlScheme().getControlSchemeDefectFixed();
        LinearLayout table = pdfContent.findViewById(R.id.defectFixedTable);
        table.removeAllViews();

        int remainingRows = 12;

        for(ControlSchemeDefectFixed defect : defects){
            View row = layoutInflater.inflate(R.layout.defect_fixed_row, null);
            TextView row1 = row.findViewById(R.id.row1);
            TextView row2 = row.findViewById(R.id.row2);
            TextView row3 = row.findViewById(R.id.row3);

            row1.setText(getReadableDate(defect.getControlDate()));
            row2.setText(getReadableDate(defect.getDefectFixedDate()));
            row3.setText(defect.getSignature());

            remainingRows--;
            table.addView(row);
        }
        for(int i = 0; i < remainingRows; i++){
            View row = layoutInflater.inflate(R.layout.defect_fixed_row, null);
            table.addView(row);
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
