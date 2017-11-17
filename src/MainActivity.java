package com.eugenesumaryev.floatingbuttons;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements TodoFragment.OnNewItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>, TodoItemAdapter.OnDeleteClickedListener {

    private TodoItemAdapter ta;
    private ArrayList<TodoItem> todoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TodoItem newTodoItem = new TodoItem("newItem");
                // todoItems.add(0, newToDoItem);
                // ta.notifyDataSetChanged();

                startDetailsActivity(newTodoItem);
            }
        });

        FragmentManager fm = getFragmentManager();
        TodoFragment todoFragment =
                (TodoFragment) fm.findFragmentById(R.id.TodoFragment);

        // Create the array list of to do items
        todoItems = new ArrayList<TodoItem>();

        // Create the array adapter to bind the array to the listview
        int resID = R.layout.list_row2;
        ta = new TodoItemAdapter(this, resID, todoItems);
        // Bind the array adapter to the listview.
        todoFragment.setListAdapter(ta);

        getLoaderManager().initLoader(0, null, this);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //	Log.v("ResultCode: ", String.valueOf(requestCode));

        if (resultCode == Activity.RESULT_OK) {

            String taskItem = data.getExtras().getString("todo");
            String noteItem = data.getExtras().getString("note");
            String dateItem = data.getExtras().getString("date");
            int priority = data.getExtras().getInt("priority");
            String dueDate = data.getExtras().getString("dueDate");

            TodoItem newTodoItem = new TodoItem(taskItem);
            newTodoItem.note = noteItem;
            newTodoItem.created = Long.parseLong(dateItem);
            newTodoItem.priority = priority;
            Log.v("rtndprioritychanged : ", Integer.toString(priority));
           // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            try {
                newTodoItem.dueDate = sdf.parse(dueDate);
               // Log.v("SECONDDUEDATE: ", newTodoItem.dueDate.toString());
               // Log.v("ToDoItemThird ", newTodoItem.note);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           // Log.v("ToDoItemThird ", newTodoItem.task);
            updateActivity(newTodoItem);

        }

    }

    //Starts the DetailsActivity using the Intent
    private void startDetailsActivity(TodoItem todoItem){


      //  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, DetailsActivity.class);
        intent.putExtra("todo", todoItem.getTask());
        intent.putExtra("note", todoItem.getNote());
        intent.putExtra("date", String.valueOf(todoItem.getCreated()));
        intent.putExtra("priority", todoItem.getPriority());
        intent.putExtra("dueDate", sdf.format((todoItem.getDueDate())));
      //  Log.v("DateCreatedFirst ", String.valueOf(todoItem.getCreated()));
      //  Log.v("ToDoItemFirst ", todoItem.getTask() );
        startActivityForResult(intent, 1);

    }

    private void updateActivity(TodoItem noteItem) {
        //Is note in NoteArray?
        //Note.dateCreated compareTo each note in array
        //If doesnt exist, put in Array.
        //if matches, update it
        //Update database

       // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String newTask = noteItem.getTask();
        String newNote = noteItem.getNote();
        long created = noteItem.getCreated();
        //String createdString = sdf.format(created);

        int priority = noteItem.getPriority();
        Date dueDate = noteItem.getDueDate();
        String dueDateString = sdf.format(dueDate);

      //  Log.v("priorityInUpdtingAct : ", Integer.toString(priority));
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        values.put(TodoContentProvider.KEY_TASK, newTask);
        values.put(TodoContentProvider.KEY_NOTE, newNote);
        values.put(TodoContentProvider.KEY_PRIORITY, priority);
        values.put(TodoContentProvider.KEY_CREATION_DATE, created);
        values.put(TodoContentProvider.KEY_DUEDATE, dueDateString);


        String where = TodoContentProvider.KEY_CREATION_DATE + " = " + created;

       // Log.v("DateCreatedSecond ", String.valueOf(created));

        //If note is new, insert it into provider, if not then update it
        Cursor query = cr.query(TodoContentProvider.CONTENT_URI, null, where, null, null);

        if (query.getCount() == 0)
            cr.insert(TodoContentProvider.CONTENT_URI, values);
        else
            cr.update(TodoContentProvider.CONTENT_URI, values, where, null);


       // ta.notifyDataSetChanged();

        query.close();
       // ta.notifyDataSetChanged();



        //getLoaderManager().initLoader(1, null, this);


        getLoaderManager().restartLoader(1, null, this);

        getFragmentManager().popBackStack();

        ta.notifyDataSetChanged();

        /*
        TodoFragment tdf =
                (TodoFragment) getFragmentManager().findFragmentById(R.id.TodoFragment);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(tdf).attach(tdf).commit();
        */


    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String where = null;
        CursorLoader loader = new CursorLoader(this,
                TodoContentProvider.CONTENT_URI, null, where, null, null);

        return loader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        /*
        ContentResolver cr = getContentResolver();
        String where = TodoContentProvider.KEY_CAT + "=?";
        Cursor query = cr.query(TodoContentProvider.CONTENT_URI, null, where, new String [] {newCategory}, null);

        */
        int keyTaskIndex = cursor.getColumnIndexOrThrow(TodoContentProvider.KEY_TASK);
        int keyNoteIndex = cursor.getColumnIndexOrThrow(TodoContentProvider.KEY_NOTE);
        int keyPriorityIndex = cursor.getColumnIndexOrThrow(TodoContentProvider.KEY_PRIORITY);
        int keyDateCreatedIndex = cursor.getColumnIndexOrThrow(TodoContentProvider.KEY_CREATION_DATE);
        int keyDueDateIndex = cursor.getColumnIndexOrThrow(TodoContentProvider.KEY_DUEDATE);


        if (cursor != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            todoItems.clear();
            while (cursor.moveToNext()) {
                TodoItem newItem = new TodoItem(cursor.getString(keyTaskIndex));
                newItem.note = cursor.getString(keyNoteIndex);
                newItem.priority = cursor.getInt(keyPriorityIndex);
                newItem.created = cursor.getLong(keyDateCreatedIndex);
                try {
                    newItem.dueDate = sdf.parse(cursor.getString(keyDueDateIndex));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                todoItems.add(newItem);
              //  Log.v("ToDoItemFourth ", newItem.getTask());
            }
            ta.notifyDataSetChanged();
        }
        cursor.close();

    }

    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(1, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewItemSelected(int position) {

        TodoItem todo = todoItems.get(position);

        //extract todoItem and send it to this method
        startDetailsActivity(todo);
    }

    @Override
    public void deleteItem(int position) {

        TodoItem note = todoItems.get(position);

        todoItems.remove(position);
        ta.notifyDataSetChanged();

        ContentResolver cr = getContentResolver();


        String where = TodoContentProvider.KEY_TASK + " = " + "?";
        String[] whereArgs = new String[] { note.getTask() };

        Cursor query = cr.query(TodoContentProvider.CONTENT_URI, null, where, whereArgs, null);

        if (query.getCount() != 0) {
            // while(query.moveToNext())
            cr.delete(TodoContentProvider.CONTENT_URI, where, whereArgs);
        }


        // todoItems.remove(position);
        // ta.notifyDataSetChanged();

        query.close();

    }

}

