package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);
    private TodoDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new TodoDbHelper(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });
        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }

            @Override
            public void reflash() throws ParseException {
                Log.d("flash","已经刷新");
                notesAdapter.refresh(loadNotesFromDatabase());
            }
        });

        recyclerView.setAdapter(notesAdapter);

        try {
            notesAdapter.refresh(loadNotesFromDatabase());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            try {
                notesAdapter.refresh(loadNotesFromDatabase());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Note> loadNotesFromDatabase() throws ParseException {
        // TODO 从数据库中查询数据，并转换成 JavaBeans

        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TodoContract.TodoEntry._ID,
                "Date",
                "State",
                "Content"
        };

        String sortOrder ="Date DESC";

        Cursor cursor = db.query(
                TodoContract.TodoEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );


        Log.d("zxr","13-----");
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry._ID));
            Log.d("zxr1","id"+String.valueOf(id));
            String date = cursor.getString(cursor.getColumnIndex((TodoContract.TodoEntry.COLUMN_NAME_DATE)));
            Log.d("zxr1",date);
            int state = cursor.getInt(cursor.getColumnIndexOrThrow("State"));
            Log.d("zxr1",String.valueOf(state));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("Content"));
            Log.d("zxr1",content);

            Note temp = new Note(id);
            temp.setContent(content);
            temp.setDate(SIMPLE_DATE_FORMAT.parse(date));
            if(state==1)
                temp.setState(State.DONE);
            else
                temp.setState(State.TODO);


            notes.add(temp);
            Log.d("zxr",temp.toString());
        }
        cursor.close();
        Log.d("zxrDB","查询成功 !");
        return notes;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String id = String.valueOf(note.id);
        String[] selectionArgs = {id};
        int deletedRows = db.delete(TodoContract.TodoEntry.TABLE_NAME, TodoContract.TodoEntry._ID+" LIKE ?", selectionArgs);
        Log.d("zxrDB","删除成功 ! "+deletedRows);
    }

    private void updateNode(Note note) {
        // 更新数据

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id = note.id;
        Date new_date = note.getDate();
        State new_state = note.getState();
        String new_content = note.getContent();

        ContentValues values = new ContentValues();
        values.put("Date",SIMPLE_DATE_FORMAT.format(new_date));
        values.put("Content",new_content);
        if(new_state==State.DONE)
            values.put("State",(int)1);
        else
            values.put("State",(int)0);

        int count = db.update(TodoContract.TodoEntry.TABLE_NAME,values,
                TodoContract.TodoEntry._ID+" LIKE ?",new String[]{String.valueOf(id)});
        Log.d("zxrDB","更新成功 ! "+count);
    }

    private void additems(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();
        Date new_date = new Date(System.currentTimeMillis());

        values.put("Date",SIMPLE_DATE_FORMAT.format(new_date));
        values.put("State",1);
        values.put("Content","This is a test");
        db.insert(TodoContract.TodoEntry.TABLE_NAME,null,values);

        values2.put("Date",SIMPLE_DATE_FORMAT.format(new_date));
        values2.put("State",0);
        values2.put("Content","This is another test");
        db.insert(TodoContract.TodoEntry.TABLE_NAME,null,values2);
        Log.d("zxr","插入成功 ！");

        return;
    }

}
