package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import no.hiof.bo20_g28.stillashjelpen.adapter.TabCalculationAdapter;
import no.hiof.bo20_g28.stillashjelpen.fragment.WallInfoFragment;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WallActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;


    private TabCalculationAdapter tabCalculationAdapter() {
        TabCalculationAdapter adapter = new TabCalculationAdapter(this);
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(tabCalculationAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    }
                }).attach();
        setTabLayout();
        setTabText();
    }

    private void setTabText(){
        tabLayout.getTabAt(0).setText(getString(R.string.tab_wall_info));
        tabLayout.getTabAt(1).setText(getString(R.string.tab_soleboard));
        tabLayout.getTabAt(2).setText(getString(R.string.tab_anchor));
    }

    private int[] tabIcons = {
        R.drawable.ic_wall_info_light,
        R.drawable.ic_soleboard,
        R.drawable.ic_anchor
    };

    private int[] tabLabels = {
            R.string.tab_wall_info,
            R.string.tab_soleboard,
            R.string.tab_anchor
    };


    private void setTabLayout(){
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

            TextView tab_label = (TextView) tab.findViewById(R.id.tab_label);
            ImageView tab_icon = (ImageView) tab.findViewById(R.id.tab_icon);
            tab_label.setText(tabLabels[i]);

            // set the home to be active at first
            if(i == 0) {
//                tab_label.setTextColor(getResources().getColor(R.color.efent_color));
                tab_icon.setImageResource(tabIcons[i]);
            } else {
                tab_icon.setImageResource(tabIcons[i]);
            }
            tabLayout.getTabAt(i).setCustomView(tab);
        }
    }

}
