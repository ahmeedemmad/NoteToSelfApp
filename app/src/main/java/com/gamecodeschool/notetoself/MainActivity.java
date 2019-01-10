package com.gamecodeschool.notetoself;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int mIdBeep = -1 ;
    SoundPool mSp ;
    Animation mAnimFlash ;
    Animation mFadeIn ;

    private NoteAdapter mNoteAdapter;
    private Note.JSONSerializer mSerializer; ;
    private boolean mSound ;
    private int mAnimOption;
    private SharedPreferences mPrefs;

    @Override
    protected void onPause() {
        super.onPause();
        mNoteAdapter.saveNote();
    }

    public void createNewNote(Note n) {

        mNoteAdapter.addNote(n);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Instantiate our sound pool
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSp = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(5)
                    .build();

        }else {
            mSp = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        }
        try{
            AssetManager assetManager = this.getAssets();
            AssetFileDescriptor descriptor ;

            descriptor = assetManager.openFd("fx1.ogg");
            mIdBeep = mSp.load(descriptor,0);
        }catch (IOException e){
            Log.e("error","failed to load sound effect");
        }
        mNoteAdapter = new NoteAdapter();
        final ListView listNote = (ListView)findViewById(R.id.listView);
        listNote.setAdapter(mNoteAdapter);
        // So we can long click it
        listNote.setLongClickable(true);
        // Now to detect long clicks and delete the note
        listNote.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                // Ask NoteAdapter to delete this entry
                mNoteAdapter.deleteNote(position);
                return true;
            }
        });

        listNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note tempNote = (Note) mNoteAdapter.getItem(position);
                DialogShowNote dialog = new DialogShowNote();
                dialog.sendNoteSelected(tempNote);
                dialog.show(getFragmentManager(),"");

                if(mSound){
                    mSp.play(mIdBeep,1,1,0,1,1);

                }
            }
        });
         }

    @Override
    protected void onResume() {
        super.onResume();

        mPrefs = getSharedPreferences("Note to self",MODE_PRIVATE);
        mSound = mPrefs.getBoolean("sound",true);
        mAnimOption = mPrefs.getInt("anim option",SettingsActivity.FAST);

        mFadeIn = AnimationUtils.loadAnimation(getApplicationContext()  ,R.anim.fade_in);
        mAnimFlash = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.flash);

        if (mAnimOption == SettingsActivity.FAST){
            mAnimFlash.setDuration(100);
        }else if (mAnimOption == SettingsActivity.SLOW){
            mAnimFlash.setDuration(1000);
        }
        mNoteAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true ;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){

    if (menuItem.getItemId() == R.id.action_add) {
        DialogNewNote dialog = new DialogNewNote();
        dialog.show(getFragmentManager(), "");
    }
        if (menuItem.getItemId() == R.id.menuSettings){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);

            return true ;
        }
        return super.onOptionsItemSelected(menuItem) ;

}
    public class NoteAdapter extends BaseAdapter{
        public NoteAdapter (){
            mSerializer = new Note.JSONSerializer("NoteToSelf.json",MainActivity.this.getApplicationContext());
            try {
                noteList = mSerializer.load();
            }catch (Exception e){
                noteList = new ArrayList<Note>();
                Log.e("Error loading notes : ","" ,e);
            }
        }
        public void saveNote (){
            try{

                mSerializer.save(noteList);
            }catch (Exception e){

                Log.e("Error saving Note" , "",e);
            }
        }

        List<Note> noteList = new ArrayList<Note>();

        @Override
        public int getCount() {
            return noteList.size();
        }

        @Override
        public Object getItem(int position) {
            return noteList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position    ;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem, parent, false);
            }
            TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
            ImageView ivImportant = (ImageView) view.findViewById(R.id.imageViewImportant);
            ImageView ivTodo = (ImageView) view.findViewById(R.id.imageViewTodo);
            ImageView ivIdea = (ImageView) view.findViewById(R.id.imageViewIdea);

            Note  tempNote = noteList.get(position);

            if (tempNote.isImportant() && mAnimOption != SettingsActivity.NONE){
                view.setAnimation(mAnimFlash);
            }else{
                view.setAnimation(mFadeIn);
            }
            if (!tempNote.isToDo())
                ivTodo.setVisibility(View.GONE);
            if (!tempNote.isImportant())
                ivImportant.setVisibility(View.GONE);
            if(!tempNote.isIdea())
                ivIdea.setVisibility(View.GONE);


            txtTitle.setText(tempNote.getTitle());
            txtDescription.setText(tempNote.getDescription());

            return view;
        }

        public void addNote(Note n){
            noteList.add(n);
            notifyDataSetChanged();
        }
        public void deleteNote (int n){
            noteList.remove(n);
            notifyDataSetChanged();
        }

    }

}
