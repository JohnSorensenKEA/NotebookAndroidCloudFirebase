package com.johnsoerensen.basiclistdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.johnsoerensen.basiclistdemo.model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class Repo {

    private static StorageReference storage;

    private static FirebaseFirestore db;
    private static String NOTESKEY = "notes";
    private static String TXTKEY = "txt";
    private static String DATEKEY = "date";
    private static String IMGKEY = "imageid";
    private static MainActivity main;

    public synchronized static void createNote(String text) {
        DocumentReference document = db.collection(NOTESKEY).document();
        Map<String, Object> data = new HashMap<>();
        data.put(TXTKEY, text);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        data.put(DATEKEY, formatter.format(new Date()));
        document.set(data);
    }

    public static void init(Context context) {
        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();
        System.out.println("Init mate, with DB");

        storage = FirebaseStorage.getInstance().getReference();
    }

    public static void setActivity(MainActivity mainActivity) {
        main = mainActivity;
    }

    public static void startListener() {
        db.collection(NOTESKEY).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("Failed getting documents: " + e);
                    return;
                }

                ArrayList<Note> notes = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshot) {
                    if (doc.get(TXTKEY) != null) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            String imageID = null;
                            if (doc.contains(IMGKEY)) {
                                imageID = doc.getString(IMGKEY);
                            }
                            notes.add(new Note(doc.getString(TXTKEY), formatter.parse(doc.getString(DATEKEY)), doc.getId(), imageID));
                        } catch (ParseException parseException) {
                            System.out.println("Failed parsing date: " + doc.getString(DATEKEY) + " - " + doc.getId());
                        }
                    }
                }
                main.setListData(notes);
            }
        });
    }

    public static void deleteDocument(String documentID) {
        db.collection(NOTESKEY).document(documentID).delete();
    }

    public static void updateDocument(Note note) {
        DocumentReference document = db.collection(NOTESKEY).document(note.getDocID());
        Map<String, Object> data = new HashMap<>();
        data.put(TXTKEY, note.getText());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        data.put(DATEKEY, formatter.format(note.getDate()));
        if (note.getImageID() != null) {
            data.put(IMGKEY, note.getImageID());
        }
        document.set(data);
    }

    public static void deleteImage(String imageID) {
        if (imageID == null) {
            return;
        }
        storage.child(imageID).delete();
    }

    public static String addImage(byte[] image) {
        String uuid = UUID.randomUUID().toString();
        StorageReference ref = storage.child(uuid);
        ref.putBytes(image);
        return uuid;
    }

    public static void getImage(String imageID, DetailActivity activity) {
        StorageReference imgRef = storage.child(imageID);

        final long ONE_MEGABYTE = 1024 * 1024 *5;
        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                activity.setImage(bytes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("Failed getting image: " + imageID);
            }
        });

    }
}
