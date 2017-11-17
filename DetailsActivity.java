package com.eugenesumaryev.floatingbuttons;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by eugenesumaryev on 10/20/17.
 */
public class DetailsActivity extends Activity implements NewDialogFragment.OnUpdateClickedListener,
CalendarFragment.DateChangedListener
{

    private String newDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_second_layout);



            // During initial setup, plug in the details fragment.
            String newTodo = getIntent().getExtras().getString("todo");
            //Log.v("Todofromdetails: ", newTodo);
            TodoItem newTodoItem = new TodoItem(newTodo);
             //Log.v("Todofromdetails: ", newTodoItem.getTask());
            newTodoItem.note = getIntent().getExtras().getString("note");
           // newTodoItem.note = "";
             // Log.v("Noetfromdetails: ", newTodoItem.getNote());
            newTodoItem.created = Long.parseLong(getIntent().getExtras().getString("date"));
            newTodoItem.priority = getIntent().getExtras().getInt("priority");
           // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
             SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            try {
                newTodoItem.dueDate = sdf.parse(getIntent().getExtras().getString("dueDate"));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            NewDialogFragment details = NewDialogFragment.newInstance(this, newTodoItem);

            getFragmentManager().beginTransaction().add(R.id.fragment_container, details, "details_fragment").commit();
    }


    public void onDateClicked(View v){

        /*
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
        */


    }

    public void onCalendarClicked(View v) {



       // startActivityForResult(new Intent(Intent.ACTION_PICK).setDataAndType(null, CalendarActivity.MIME_TYPE), 100);
        CalendarFragment cf = new CalendarFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, cf).addToBackStack(null).commit();



    }


    public void onNewDateSelected(Date dateSelected)
    {
        /*
        DialogFragment df = (DialogFragment)
                getFragmentManager().findFragmentByTag(tag);

        df.updateDate(dateSelected);
        */

    }


    public void updateActivity(TodoItem note) {
        // TODO Auto-generated method stub



      //  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        setNewDueDate(sdf.format(note.getDueDate()));
        String dateString = String.valueOf((note.getCreated()));
        String todoText = note.getTask();
        String noteText = " " + " \n" +  note.getNote();
        int priorityLevel = note.getPriority();

        String dueDateString = getNewDueDate();
       // Log.v("priority: ", Integer.toString(priorityLevel));



        Intent result = new Intent();
        result.putExtra("todo", todoText);
        result.putExtra("date", dateString);
        result.putExtra("note", noteText);
        result.putExtra("priority", priorityLevel);
        result.putExtra("dueDate", dueDateString);

      //  Log.v("ToDoItemSecond ", todoText);
        Log.v("priorityinDetails : ", Integer.toString(priorityLevel));

        setResult(RESULT_OK, result);
        finish();
    }


    public void cancelActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note, menu);


        return true;
    }

/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK) {
            int year = data.getIntExtra("year", 0);
            int month = data.getIntExtra("month", 0) ;
            int day = data.getIntExtra("day", 0);
            final Calendar dat = Calendar.getInstance();
            dat.set(Calendar.YEAR, year);
            dat.set(Calendar.MONTH, month);
            dat.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
            Toast.makeText(DetailsActivity.this, sdf.format(dat.getTime()), Toast.LENGTH_LONG).show();

        }
    }
 */


    @Override
    public void onDateChanged(int year, int month, int day) {

        final Calendar dat = Calendar.getInstance();
        dat.set(Calendar.YEAR, year);
        dat.set(Calendar.MONTH, month);
        dat.set(Calendar.DAY_OF_MONTH, day);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        setNewDueDate(sdf.format(dat.getTime()));

        NewDialogFragment ndf = (NewDialogFragment) getFragmentManager().
                findFragmentByTag("details_fragment");
        ndf.updateDate(dat.getTime());



        getFragmentManager().popBackStack();


        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(ndf).attach(ndf).commit();



    }

    public void setNewDueDate(String newDate){
        newDueDate = newDate;
    }

    public String getNewDueDate(){
        return newDueDate;
    }


}
