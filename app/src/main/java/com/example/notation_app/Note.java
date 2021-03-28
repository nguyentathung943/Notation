package com.example.notation_app;

import android.content.Context;
import android.view.Display;

import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Note {
    String title;
    String description;
    String time;
    ArrayList<String> tags;
    Note(String a, String b, String c, ArrayList<String> d){
        this.title = a;
        this.description = b;
        this.time = c;
        this.tags = d;
    }
}
class NoteList{
    Context context;
    private ArrayList<Note> list;
    NoteList(Context c){
        this.context = c;
        list = new ArrayList<>();
    }
    void Update(Note temp, int index){
        list.set(index,temp);
    }
    void Delete(int index){
        list.remove(index);
    }
    ArrayList<Note> getlist(){
        return this.list;
    }
    ArrayList<Note> search(String keyword){
        ArrayList<Note> temp = new ArrayList<>();
        for(int i =0 ; i< this.list.size() ;i++){
            Note a = this.list.get(i);
            if(a.title.toLowerCase().contains(keyword.toLowerCase())){
                temp.add(a);
            }
            else{
                for (String x: a.tags){
                    if(x.toLowerCase().contains(keyword.toLowerCase())){
                        temp.add(a);
                    }
                }
            }
        }
        return temp;
    }
    public void ReadFromFile(){
        String filename = "Note.txt";
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String title = reader.readLine();
                String des = reader.readLine();
                String time = reader.readLine();
                String tag = reader.readLine();
                ArrayList<String> tagsString;
                while (title != null) {
                    tagsString = new ArrayList<>(Arrays.asList(tag.split(",")));
//                    Collections.copy(tagsString,temp);
                    Note a = new Note(title,des,time,tagsString);
                    this.list.add(a);
                    title = reader.readLine();
                    des = reader.readLine();
                    time = reader.readLine();
                    tag = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void SaveToFile() throws IOException {
        FileOutputStream fout = context.openFileOutput("Note.txt", Context.MODE_PRIVATE);
        for (int i = 0 ; i< this.list.size();i++){
            Note temp = this.list.get(i);
            fout.write((temp.title+'\n').getBytes());
            fout.write((temp.description+'\n').getBytes());
            fout.write((temp.time+'\n').getBytes());
            String t = String.join(",",temp.tags) + '\n';
            fout.write(t.getBytes());
        }
    }
    public void addNote(Note a){
        this.list.add(a);
        //SaveToFile();
    }
}