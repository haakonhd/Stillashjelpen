package no.hiof.bo20_g28.stillashjelpen.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
        View v = getLayoutInflater().inflate(R.layout.report_cs_defect_dialog, null);

        EditText mailEditText = v.findViewById(R.id.defectDescriptionEditText);

        builder.setTitle("Send pdf til mail");
        mailEditText.setHint("eksempel@email.no");
        mailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        if(thisProject.getControlScheme().getLastEmailSentTo() != null)
            mailEditText.setText(thisProject.getControlScheme().getLastEmailSentTo());

        builder.setView(v);

        Button clearButton = v.findViewById(R.id.clearDescriptionButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailEditText.setText("");
            }
        });


        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = mailEditText.getText().toString();
                if(email.length() > 5){
                    //saving the email to db so it will be autofilled the next time
                    thisProject.getControlScheme().setLastEmailSentTo(email);
                    DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("projects");
                    DatabaseReference projectRef = fDatabase.child(thisProject.getProjectId());
                    projectRef.setValue(thisProject);

                    sendEmail(email);
                }
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
            CharSequence text = "En ukjent feil har oppstått. Kunne ikke sende mail";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getActivity(), text, duration);
            toast.show();
            return;
        }
        Uri uri = Uri.fromFile(file);
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

    //Saves PDF file to storage
    public void saveFile(String fileName){
        // Android needs permission from the user to save files to the external storage
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, 1);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Stillashjelpen");

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

        CharSequence text = "PDF lagret til \"nedlastninger\"-mappen.";
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

    //turns the view into a bitmap-image
    public static Bitmap loadBitmapFromView(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //creates a pdf from the bitmap image
    private PdfDocument createPdf() {
        bitmap = loadBitmapFromView(pdfContent, pdfContent.getWidth(), pdfContent.getHeight());
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;
        int convertHeight = (int) height, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        canvas.drawPaint(paint);
//        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);
        bitmap = scaleBitmapAndKeepRation(bitmap, convertHeight, convertWidth);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        return document;
    }

    public static Bitmap scaleBitmapAndKeepRation(Bitmap targetBmp,int reqHeightInPixels,int reqWidthInPixels)
    {
        Matrix matrix = new Matrix();
        matrix .setRectToRect(new RectF(0, 0, targetBmp.getWidth(), targetBmp.getHeight()), new RectF(0, 0, reqWidthInPixels, reqHeightInPixels), Matrix.ScaleToFit.CENTER);
        Bitmap scaledBitmap = Bitmap.createBitmap(targetBmp, 0, 0, targetBmp.getWidth(), targetBmp.getHeight(), matrix, true);
        return scaledBitmap;
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
            if(item.isChecked() && item.getId() == 0) cb1.setChecked(true);
            else if(item.isChecked() && item.getId() == 1) cb2.setChecked(true);
            else if(item.isChecked() && item.getId() == 2) cb3.setChecked(true);
            else if(item.isChecked() && item.getId() == 3) cb4.setChecked(true);
            else if(item.isChecked() && item.getId() == 4) cb5.setChecked(true);
            else if(item.isChecked() && item.getId() == 5) cb5a.setChecked(true);
            else if(item.isChecked() && item.getId() == 6) cb5b.setChecked(true);
            else if(item.isChecked() && item.getId() == 7) cb5c.setChecked(true);
            else if(item.isChecked() && item.getId() == 8) cb5d.setChecked(true);
            else if(item.isChecked() && item.getId() == 9) cb6.setChecked(true);
            else if(item.isChecked() && item.getId() == 10) cb7.setChecked(true);
            else if(item.isChecked() && item.getId() == 11) cb8.setChecked(true);
            else if(item.isChecked() && item.getId() == 12) cb9.setChecked(true);
            else if(item.isChecked() && item.getId() == 13) cb10.setChecked(true);
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

        View defectFoundRow1 = layoutInflater.inflate(R.layout.defect_found_row, null);
        TextView dateTextView1 = defectFoundRow1.findViewById(R.id.dateTextView);
        dateTextView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        dateTextView1.setTypeface(null, Typeface.BOLD);
        TextView descriptionTextView1 = defectFoundRow1.findViewById(R.id.defectDescriptionTextView);
        descriptionTextView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        descriptionTextView1.setTypeface(null, Typeface.BOLD);
        dateTextView1.setText("Dato");
        descriptionTextView1.setText("Tekst - mangler");
        table.addView(defectFoundRow1);

        remainingRows--;
        //Creating rows for the defects found
        for(ControlSchemeDefect defect : defects){
            View defectFoundRow = layoutInflater.inflate(R.layout.defect_found_row, null);
            TextView dateTextView = defectFoundRow.findViewById(R.id.dateTextView);
            TextView descriptionTextView = defectFoundRow.findViewById(R.id.defectDescriptionTextView);
            dateTextView.setText(getReadableDate(defect.getfoundDate()));
            descriptionTextView.setText(defect.getDefectDescription());

            table.addView(defectFoundRow);
        }

        //filling out the empty spaces
        for(int i = 0; i < remainingRows; i++){
            View defectFoundRow = layoutInflater.inflate(R.layout.defect_found_row, null);
            table.addView(defectFoundRow);
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
