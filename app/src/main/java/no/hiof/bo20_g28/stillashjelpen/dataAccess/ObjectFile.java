package no.hiof.bo20_g28.stillashjelpen.dataAccess;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import no.hiof.bo20_g28.stillashjelpen.model.Message;
import no.hiof.bo20_g28.stillashjelpen.model.Project;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

/**
 * This class saves objects locally on device.
 * Firebase already has handling for offline syncing, so this is just in case we need to store stuff
 * on device that wont be uploaded to Firebase.
 */

public class ObjectFile {

    private Context parent;
    private FileInputStream fileIn;
    private FileOutputStream fileOut;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private String filePath;

    private String projectsFile = "projects.txt";
    private String wallsFile = "walls.txt";
    private String messagesFile = "messages.txt";
    private String controlSchemesFile = "control_schemes.txt";
    private String scaffoldingSystemsFile = "scaffolding_systems.txt";

    public ObjectFile(Context c){
        parent = c;
    }

    public ArrayList<Object> readObject(String fileName){

        ArrayList<Object> objects= new ArrayList<Object>();

        try {
            filePath = parent.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
            fileIn = new FileInputStream(filePath);
            objectIn = new ObjectInputStream(fileIn);

            objects = (ArrayList) objectIn.readObject();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return objects;
    }

    public void writeObject(ArrayList<Object> objects, String fileName){
        try {
            filePath = parent.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
            fileOut = new FileOutputStream(new File(filePath));
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(objects);
            fileOut.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //Next 3 methods not finished. Will overwrite already existing files------------------------------

    public void saveProject(Project project){
        ArrayList<Object> projects = new ArrayList<>();
        projects.add(project);

        ObjectFile objectFile = new ObjectFile(parent);
        objectFile.writeObject(projects, projectsFile);
    }

    public void saveWall(Wall wall){
        ArrayList<Object> walls = new ArrayList<>();
        walls.add(wall);

        ObjectFile objectFile = new ObjectFile(parent);
        objectFile.writeObject(walls, wallsFile);
    }

    public void saveMessage(Message message){
        ArrayList<Object> messages = new ArrayList<>();
        messages.add(message);

        ObjectFile objectFile = new ObjectFile(parent);
        objectFile.writeObject(messages, messagesFile);
    }



}
