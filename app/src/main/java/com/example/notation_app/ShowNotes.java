package com.example.notation_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.richeditor.RichEditor;

public class ShowNotes extends AppCompatActivity{
    SharedPreferences note;
    EditText title, time, tag;
    Button Save, Edit, Back,Delete;
    private RichEditor mEditor;
    LinearLayout btns;
    private TextView mPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);
        btns = findViewById(R.id.btns);
        btns.setVisibility(LinearLayout.INVISIBLE);
        Save = findViewById(R.id.btn_save);
        Edit = findViewById(R.id.btn_edit);
        Back = findViewById(R.id.btn_back);
        Delete = findViewById(R.id.btn_delete);
        note = getApplicationContext().getSharedPreferences("note_share", Context.MODE_PRIVATE);
        title = findViewById(R.id.note_title);
        time = findViewById(R.id.modified_time);
        tag = findViewById(R.id.tags);

        mEditor = findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");
        mEditor.setInputEnabled(false);

        title.setInputType(InputType.TYPE_NULL);
        time.setInputType(InputType.TYPE_NULL);
        tag.setInputType(InputType.TYPE_NULL);
        title.setText(note.getString("title",""));
        time.setText(note.getString("time",""));
        mEditor.setHtml(note.getString("description",""));
        tag.setText(note.getString("tags",""));


        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        Save.setOnClickListener(view -> {
            UIUtil.hideKeyboard(this);
            title.setFocusable(false);
            title.setInputType(InputType.TYPE_NULL);
            btns.setVisibility(LinearLayout.INVISIBLE);
            mEditor.setInputEnabled(false);
//            desc.setFocusable(false);
//            desc.setInputType(InputType.TYPE_NULL);
            tag.setFocusable(false);
            tag.setInputType(InputType.TYPE_NULL);
            note = getApplicationContext().getSharedPreferences("note_share", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = note.edit();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String Date = formatter.format(date);
            editor.putString("tags",tag.getText().toString());
            editor.putString("title",title.getText().toString());
            editor.putString("description", mEditor.getHtml());
            editor.putString("time",Date);
            editor.putInt("update",1);
            time.setText(Date);
            editor.commit();
            Toast.makeText(this,"Note saved",Toast.LENGTH_SHORT).show();
        });
        Edit.setOnClickListener(view -> {
            mEditor.setInputEnabled(true);
            mEditor.setFocusable(true);
            btns.setVisibility(LinearLayout.VISIBLE);
            tag.setFocusableInTouchMode(true);
            tag.setInputType(InputType.TYPE_CLASS_TEXT);
            title.setFocusableInTouchMode(true);
            title.setInputType(InputType.TYPE_CLASS_TEXT);
        });
        Back.setOnClickListener(view -> {
            finish();
        });
        Delete.setOnClickListener(view -> {
            note = getApplicationContext().getSharedPreferences("note_share", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = note.edit();
            editor.putString("tags",tag.getText().toString());
            editor.putString("title",title.getText().toString());
            editor.putString("description",mEditor.getHtml().toString());
            editor.putInt("delete",1);
            editor.commit();
            Toast.makeText(this,"Note deleted",Toast.LENGTH_SHORT).show();
            finish();
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}