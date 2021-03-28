package com.example.notation_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import jp.wasabeef.richeditor.RichEditor;

public class ListNotes extends AppCompatActivity{
    int ITEM_CODE = 1;
    NoteList a;
    ListView listView;
    MaterialSearchView searchView;
    Button Add;
    Toolbar toolbar;
    ArrayList<String> title;
    ArrayList<String> time;
    ArrayList<String> desc;
    ArrayList<String> tags;
    ArrayList<Note> list;
    SharedPreferences note;
    MyAdapter adapter;

    void getListViewData(ArrayList<Note> a){
        title = new ArrayList<>();
        time = new ArrayList<>();
        desc = new ArrayList<>();
        tags  = new ArrayList<>();
        for(int i = 0; i < a.size();i++){
            title.add(a.get(i).title);
            time.add(a.get(i).time);
            desc.add(a.get(i).description);
            String t = String.join(",",a.get(i).tags);
            tags.add(t);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pause");
    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resume");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("App outed");
        try {
            a.SaveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Note saved");
    }
    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Stop");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_notes);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Material search");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        searchView= findViewById(R.id.search_view);
        searchView.setHint("Search by title or tags");
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                showList();
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()){
                    ArrayList<Note> rel = a.search(newText);
                    showLisSearch(rel);
                }
                else{
                    showList();
                }
                return true;
            }
        });
        a = new NoteList(getApplicationContext());
        a.ReadFromFile();
        list = a.getlist();
        getListViewData(list);
        note = getSharedPreferences("note_share",Context.MODE_PRIVATE);

        listView = findViewById(R.id.list_view);
        adapter = new MyAdapter(ListNotes.this, title, time, tags);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String tit = this.title.get(position);
            String time = this.time.get(position);
            String des = this.desc.get(position);
            String tags = this.tags.get(position);
            SharedPreferences.Editor editor = note.edit();
            editor.putInt("index",position);
            editor.putString("title",tit);
            editor.putString("time",time);
            editor.putString("description",des);
            editor.putString("tags",tags);
            editor.commit();
            Intent Page = new Intent(this,ShowNotes.class);
            startActivityForResult(Page,ITEM_CODE);
        });
        Add = findViewById(R.id.btn_adding);
        Add.setOnClickListener(view->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LinearLayout layout = new LinearLayout(this);
            LinearLayout btn_layout = new LinearLayout(this);
            btn_layout.setOrientation(LinearLayout.HORIZONTAL);
            RichEditor mEditor = new RichEditor(this);
            mEditor.setEditorHeight(60);
            mEditor.setEditorFontSize(22);
            mEditor.setEditorFontColor(Color.BLACK);
            mEditor.setPadding(10, 10, 10, 10);
            mEditor.setPlaceholder("Insert text here...");
            Button l = new Button(this);
            Button r = new Button(this);
            Button c = new Button(this);
            l.setText("Left");
            c.setText("Center");
            r.setText("Right");
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setAlignLeft();
                }
            });
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setAlignCenter();
                }
            });
            r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditor.setAlignRight();
                }
            });
            btn_layout.addView(l);
            btn_layout.addView(c);
            btn_layout.addView(r);
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText title = new EditText(this);
            final EditText tags = new EditText(this);
            title.setInputType(InputType.TYPE_CLASS_TEXT);
            tags.setInputType(InputType.TYPE_CLASS_TEXT);
            title.setHint("Title");
            layout.addView(title);
            tags.setHint("Tags (Each tag separates with a comma)");
            layout.addView(tags);
            layout.addView(btn_layout);
            layout.addView(mEditor);
            builder.setView(layout);
            builder.setPositiveButton("Add", (dialog, whichButton) -> {
                String t = title.getText().toString();
                ArrayList<String> tag =new ArrayList<>(Arrays.asList(tags.getText().toString().split(",")));
                String de = mEditor.getHtml();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String Date = formatter.format(date);
                Note temp = new Note(t,de,Date,tag);
                a.addNote(temp);
                showList();
            });
            builder.setNegativeButton("Cancel",null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }
    void showLisSearch(ArrayList<Note> a){
        list = a;
        getListViewData(list);
        adapter.clear();
        adapter.notifyDataSetChanged();
        adapter = new MyAdapter(ListNotes.this, title, time, tags);
        listView.setAdapter(adapter);
    }
    void showList(){
        list = a.getlist();
        getListViewData(list);
        adapter.clear();
        adapter.notifyDataSetChanged();
        adapter = new MyAdapter(ListNotes.this, title, time, tags);
        listView.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ITEM_CODE) {
            note = getSharedPreferences("note_share",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = note.edit();
            ArrayList<String> tags = new ArrayList<>(Arrays.asList(note.getString("tags","").split(",")));
            Note temp = new Note(note.getString("title",""),note.getString("description",""),note.getString("time",""),tags);
            if (note.getInt("update",-1)==1){
                a.Update(temp,note.getInt("index",-1));
                editor.putInt("update",0);
            }
            else if(note.getInt("delete",-1)==1){
                a.Delete(note.getInt("index",-1));
                editor.putInt("delete",0);
            }
            editor.commit();
            showList();
        }
    }//onActivityResult


    class MyAdapter extends ArrayAdapter<String>{
        Context context;
        ArrayList<String> rTitle;
        ArrayList<String> rTime;
        ArrayList<String> rTags;
        MyAdapter(Context c, ArrayList<String> title, ArrayList<String> time, ArrayList<String> tag){
            super(c,R.layout.row,R.id.editor,title);
            this.context = c;
            this.rTitle = title;
            this.rTime = time;
            this.rTags = tag;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            TextView title = row.findViewById(R.id.row_title);
            TextView time = row.findViewById(R.id.modified_time);
            TextView tag = row.findViewById(R.id.tag);
            title.setText(rTitle.get(position));
            time.setText(rTime.get(position));
            tag.setText(rTags.get(position));
            return row;
        }
    }
}