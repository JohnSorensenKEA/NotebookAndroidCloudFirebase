package com.johnsoerensen.basiclistdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.johnsoerensen.basiclistdemo.adapter.MyAdapter;
import com.johnsoerensen.basiclistdemo.model.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    public static MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.myListView);

        ArrayList<Note> initList = getInitList();
        Collections.reverse(initList);
        myAdapter = new MyAdapter(this, initList, this);
        listView.setAdapter(myAdapter);

        Repo.init(this);
        Repo.setActivity(this);
        Repo.startListener();
    }

    private ArrayList<Note> getInitList() {
        ArrayList<Note> resList = new ArrayList<>();

        resList.add(new Note("item 0", new Date(),""));
        resList.add(new Note("item 1", new Date(),""));
        resList.add(new Note("item 3", new Date(),""));
        resList.add(new Note("item 4", new Date(),""));

        return resList;
    }

    public void onClickAddRowBtn(View view) {
        //myAdapter.addItem(new Note("New Note", new Date(), ""));
        Repo.createNote("New Note");
    }

    public void setListData(ArrayList<Note> list) {
        Collections.reverse(list);
        myAdapter.updateList(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myAdapter.notifyDataSetChanged();
    }
}
/*
             //From onCreate, old listener
        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            System.out.println(view);

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("i", i);
            intent.putExtra( "text", myList.get(myList.size() - i).getText());
            startActivity(intent);

        });
*/