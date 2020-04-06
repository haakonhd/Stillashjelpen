package no.hiof.bo20_g28.stillashjelpen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.hiof.bo20_g28.stillashjelpen.adapter.MessageRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.adapter.ProjectRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.adapter.WallRecyclerViewAdapter;
import no.hiof.bo20_g28.stillashjelpen.model.ControlScheme;
import no.hiof.bo20_g28.stillashjelpen.model.Message;
import no.hiof.bo20_g28.stillashjelpen.model.Project;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ProjectActivity extends AppCompatActivity implements WallRecyclerViewAdapter.ItemClickListener, MessageRecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private DatabaseReference databaseWalls;
    private DatabaseReference databaseMessages;
    private ArrayList<Message> messages = new ArrayList<>();
    private ArrayList<Wall> walls = new ArrayList<>();

    private MessageRecyclerViewAdapter messageRecyclerViewAdapter;
    private WallRecyclerViewAdapter wallRecyclerViewAdapter;

    private Project thisProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Intent i = getIntent();
        thisProject = (Project) i.getSerializableExtra("passedProject");

        firebaseAuth = FirebaseAuth.getInstance();
        databaseWalls = FirebaseDatabase.getInstance().getReference("walls");
        databaseMessages = FirebaseDatabase.getInstance().getReference("messages");
    }

    private void getMessagesFromDatabase(String userId, DataSnapshot dataSnapshot){
        messages.clear();

        for(DataSnapshot messageSnapshot: dataSnapshot.getChildren()){
            Message message = messageSnapshot.getValue(Message.class);
            if(message.getProjectId().equals(thisProject.getProjectId())) {
                messages.add(message);
            }
        }

        fillMessageRecyclerList();
    }

    private void getWallsFromDatabase(String userId, DataSnapshot dataSnapshot){
        walls.clear();

        for(DataSnapshot wallSnapshot: dataSnapshot.getChildren()){
            Wall wall = wallSnapshot.getValue(Wall.class);
            if(wall.getProjectId().equals(thisProject.getProjectId())) {
                walls.add(wall);
            }
        }

        fillWallRecyclerList();
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (firebaseAuth.getCurrentUser() != null) {
                    getMessagesFromDatabase(firebaseAuth.getCurrentUser().getUid(), dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseWalls.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (firebaseAuth.getCurrentUser() != null) {
                    getWallsFromDatabase(firebaseAuth.getCurrentUser().getUid(), dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewMessageToDatabase(String messageContent){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messageRef = databaseReference.child("messages").push();

        Date dateCreated = new Date();
        Message message = new Message(messageRef.getKey(), firebaseAuth.getCurrentUser().getUid(), thisProject.getProjectId(), messageContent, dateCreated);

        messageRef.setValue(message);
    }

    private void addNewWallToDatabase(String wallName){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference wallRef = databaseReference.child("walls").push();

        Date dateCreated = new Date();
        Wall wall = new Wall(wallRef.getKey(), firebaseAuth.getCurrentUser().getUid(), thisProject.getProjectId(), wallName, dateCreated);

        wallRef.setValue(wall);
    }

    private void deleteMessageFromDatabase(String messageId){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messageRef = databaseReference.child("messages");

        messageRef.child(messageId).removeValue();
    }

    private void fillWallRecyclerList() {
        RecyclerView recyclerView = findViewById(R.id.wallRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wallRecyclerViewAdapter = new WallRecyclerViewAdapter(this, walls);
        wallRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(wallRecyclerViewAdapter);
    }

    private void fillMessageRecyclerList() {
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerViewAdapter = new MessageRecyclerViewAdapter(this, messages);
        messageRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(messageRecyclerViewAdapter);
    }


    @Override
    public void onItemClick(View view, int position) {
        Message clickedItem = messageRecyclerViewAdapter.getItem(position);
        deleteMessageDialogbox(clickedItem.getMessageId());
        Toast.makeText(this, "Du trykket på beskjed (id: " + clickedItem.getMessageId() + ")", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWallItemClick(View view, int position) {
        Wall clickedItem = wallRecyclerViewAdapter.getItem(position);
        Toast.makeText(this, "Du trykket på " + clickedItem.getWallName(), Toast.LENGTH_SHORT).show();
    }

    //------------------------Button Click Handling-------------------------------------------------


    public void newWallButtonClicked(View view) {
        openNewWallDialogbox();
    }

    public void newMessageButtonClicked(View view) {
        openNewMessageDialogbox();
    }

    public void controlSchemeButtonClicked(View view) {
        Toast.makeText(this, "Du trykket på 'Kontrollskjema'-knappen", Toast.LENGTH_SHORT).show();
    }


    //------------------------Dialog boxes----------------------------------------------------------

    private void openNewMessageDialogbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ny beskjed");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNewMessageToDatabase(input.getText().toString());
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

    private void openNewWallDialogbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ny vegg");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNewWallToDatabase(input.getText().toString());
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

    private void deleteMessageDialogbox(final String messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Slett beskjed");

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMessageFromDatabase(messageId);
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
}
