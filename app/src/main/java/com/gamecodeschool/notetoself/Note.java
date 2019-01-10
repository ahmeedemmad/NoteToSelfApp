package com.gamecodeschool.notetoself;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Note {

    private String mTitle ;
    private String mDescription ;
    private boolean mToDo ;
    private boolean mIdea ;
    private boolean mImportant ;

    private static final String JSON_TITLE = "title";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_IDEA = "idea" ;
    private static final String JSON_TODO = "todo";
    private static final String JSON_IMPORTANT = "important";

    // Constructor
    // Only used when new is called with a JSONObject
    public Note (JSONObject jo) throws JSONException {

        mTitle = jo.getString(JSON_TITLE);
        mDescription = jo.getString(JSON_DESCRIPTION);
        mToDo = jo.getBoolean(JSON_TODO);
        mIdea = jo.getBoolean(JSON_IDEA);
        mImportant = jo.getBoolean(JSON_IMPORTANT);
    }
    // Now we must provide an empty default constructor
// for when we create a Note as we provide a
// specialized constructor that must be used.
    public Note (){

    }
    //All we need to do is call put with the appropriate key and the matching member
    //variable. This method returns JSONObject (we will see where in a minute), and it
    //also throws a JSONException exception. Add the code we have just discussed:

    public JSONObject convertToJSON() throws JSONException {

     JSONObject jo = new JSONObject();
     jo.put(JSON_TITLE,mTitle);
     jo.put(JSON_DESCRIPTION,mDescription);
     jo.put(JSON_TODO,mToDo);
     jo.put(JSON_IDEA,mIdea);
     jo.put(JSON_IMPORTANT,mImportant);

     return jo ;

    }
    public static class JSONSerializer{

        private String mFileName ;
        private Context mcontext ;
        public JSONSerializer (String  fn, Context con){
            mFileName = fn ;
            mcontext = con ;
        }
        public void save(List<Note> notes) throws IOException ,JSONException{
            // Make an array in JSON format
            JSONArray jArray = new JSONArray();
            // And load it with the notes
            for(Note n : notes)
                jArray.put(n.convertToJSON());
            // Now write it to the private disk space of our app
            Writer writer = null ;
            try{

                OutputStream out = mcontext.openFileOutput(mFileName,mcontext.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(jArray.toString());
            }finally {
                if (writer != null){
                    writer.close();
                }
            }
        }
        public ArrayList<Note> load ()throws  IOException , JSONException {

            ArrayList<Note> noteList = new ArrayList<Note>();
            BufferedReader reader = null ;
            try {
                InputStream in = mcontext.openFileInput(mFileName);
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line = null ;
                while ((line = reader.readLine()) != null ){
                    jsonString.append(line);
                }
                JSONArray jArray = (JSONArray)new JSONTokener(jsonString.toString()).nextValue();
                for (int i = 0; i <jArray.length() ; i++) {
                    noteList.add(new Note(jArray.getJSONObject(i)));
                }

            }catch (FileNotFoundException e){
                // we will ignore this one, since it happens
// when we start fresh. You could add a log here.
            }finally {
                // This will always run
              if (reader != null )
                  reader.close();
            }
            return noteList ;
        }

    }
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public boolean isToDo() {
        return mToDo;
    }

    public void setToDo(boolean mToDo) {
        this.mToDo = mToDo;
    }

    public boolean isIdea() {
        return mIdea;
    }

    public void setIdea(boolean mIdea) {
        this.mIdea = mIdea;
    }

    public boolean isImportant() {
        return mImportant;
    }

    public void setImportant(boolean mImportant) {
        this.mImportant = mImportant;
    }

}

