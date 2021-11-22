package com.johnsoerensen.basiclistdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.johnsoerensen.basiclistdemo.DetailActivity;
import com.johnsoerensen.basiclistdemo.MainActivity;
import com.johnsoerensen.basiclistdemo.R;
import com.johnsoerensen.basiclistdemo.Repo;
import com.johnsoerensen.basiclistdemo.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter {

    private List<Note> items;
    private LayoutInflater layoutInflater;
    private MainActivity mainActivity;

    public MyAdapter(Context context, List<Note> items, MainActivity mainActivity) {
        this.items = items;
        this.mainActivity = mainActivity;

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.my_row, null);
        }
        TextView textView = view.findViewById(R.id.textViewInRow);

        if (items.get(i).getText().length() > 30) {
            textView.setText(items.get(i).getText().substring(0, 30) + "...");
        } else {
            textView.setText(items.get(i).getText());
        }

        TextView date = view.findViewById(R.id.dateTextView);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(formatter.format(items.get(i).getDate()));

        Button delBtn = (Button) view.findViewById(R.id.delBtn);
        delBtn.setOnClickListener(v -> {
            Repo.deleteDocument(items.get(i).getDocID());
            //items.remove(i);
            //notifyDataSetChanged();
        });

        view.setOnClickListener(v -> {
            Intent intent = new Intent(mainActivity, DetailActivity.class);
            //intent.putExtra("i", i);
            intent.putExtra( "text", items.get(i).getText());
            intent.putExtra("docID", items.get(i).getDocID());
            intent.putExtra("imageID", items.get(i).getImageID());
            intent.putExtra("date", formatter.format(items.get(i).getDate()));
            mainActivity.startActivity(intent);
        });

        return view;
    }

    /*
    //Old list methods
    public void addItem(Note item) {
        items.add(0, item);
        notifyDataSetChanged();
    }

    public void updateItem(int i, Note note) {
        items.set(i, note);
        notifyDataSetChanged();
    }*/

    public void updateList(ArrayList<Note> list) {
        items = list;
        notifyDataSetChanged();
    }
}
