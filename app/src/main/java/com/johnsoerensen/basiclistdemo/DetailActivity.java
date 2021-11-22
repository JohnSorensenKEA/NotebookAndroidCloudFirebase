package com.johnsoerensen.basiclistdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnsoerensen.basiclistdemo.model.Note;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class DetailActivity extends AppCompatActivity {

    private int SELECT_PICTURE = 200;

    private Bundle extras;
    private TextView textView;
    private String date;
    private String docID;
    private String imageID;

    private Button delBtn;
    private Button conBtn;
    private Button canBtn;

    private ImageView imageViewNote;
    private TextView textViewImg;
    private Button delImgBtn;
    private Button uploadImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        textView = findViewById(R.id.editText);
        delBtn = findViewById(R.id.detailDeleteBtn);
        conBtn = findViewById(R.id.confirmBtn);
        canBtn = findViewById(R.id.cancelBtn);

        imageViewNote = findViewById(R.id.imageViewNote);
        textViewImg = findViewById(R.id.textViewImg);
        delImgBtn = findViewById(R.id.delImgBtn);
        uploadImgBtn = findViewById(R.id.uploadImgBtn);

        conBtn.setVisibility(View.GONE);
        canBtn.setVisibility(View.GONE);

        extras = getIntent().getExtras();
        if (extras != null) {
            //i = extras.getInt("i");
            textView.setText(extras.getString("text")); //Could just access myList directly
            docID = extras.getString("docID");
            date = extras.getString("date");
            imageID = extras.getString("imageID");

            TextView dateView = findViewById(R.id.detailDateTextView);
            dateView.setText(date);

            if(imageID != null) {
                uploadImgBtn.setVisibility(View.GONE);
                textViewImg.setText(imageID);
                //imageViewNote.setImageDrawable();
                Repo.getImage(imageID, this);
            } else {
                imageViewNote.setVisibility(View.GONE);
                textViewImg.setVisibility(View.GONE);
                delImgBtn.setVisibility(View.GONE);
            }
        }
    }

    public void setImage(byte[] image) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        imageViewNote.setImageBitmap(bitmap);
    }

    public void onClickSave(View view) {
        //MainActivity.myAdapter.updateItem(i, new Note(textView.getText().toString(), new Date()));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Repo.updateDocument(new Note(textView.getText().toString(), formatter.parse(date), docID, imageID));
        } catch (Exception e) {
            System.out.println("Failed parsing detail note date: " + e);
        }
    }

    public void onCLickDelete(View view) {
        delBtn.setVisibility(View.GONE);
        conBtn.setVisibility(View.VISIBLE);
        canBtn.setVisibility(View.VISIBLE);
    }

    public void onClickDeleteConfirm(View view) {
        Repo.deleteDocument(docID);
        finish();
    }

    public void onClickDeleteCancel(View view) {
        delBtn.setVisibility(View.VISIBLE);
        conBtn.setVisibility(View.GONE);
        canBtn.setVisibility(View.GONE);
    }

    public void onCLickUpload(View view) {
        //TODO: Gallerylistener, activirylauncer, image getter

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        byte[] inputData = getBytes(inputStream);
                        String imageID = Repo.addImage(inputData);
                        this.imageID = imageID;
                        uploadImgBtn.setVisibility(View.GONE);
                        textViewImg.setText(imageID);
                        setImage(inputData);
                        delImgBtn.setVisibility(View.VISIBLE);
                        textViewImg.setVisibility(View.VISIBLE);
                        imageViewNote.setVisibility(View.VISIBLE);
                        onClickSave(null);

                    } catch (FileNotFoundException e) {
                        System.out.println("Could not get image file: " + e);
                    } catch (IOException e) {
                        System.out.println("Could not parse selected image inputStream to byte[]: " + e);
                    }
                }
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void onClickDelImg(View view) {
        Repo.deleteImage(imageID);
        imageID = null;
        imageViewNote.setVisibility(View.GONE);
        textViewImg.setVisibility(View.GONE);
        delImgBtn.setVisibility(View.GONE);
        uploadImgBtn.setVisibility(View.VISIBLE);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Repo.updateDocument(new Note(textView.getText().toString(), formatter.parse(date), docID, imageID));
        } catch (Exception e) {
            System.out.println("Failed parsing detail note date: " + e);
        }
    }
}