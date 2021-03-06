package com.todolistapp;

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
 * Created on 10/20/17.
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

            TodoItem newTodoItem = new TodoItem(newTodo);

            newTodoItem.note = getIntent().getExtras().getString("note");

            newTodoItem.created = Long.parseLong(getIntent().getExtras().getString("date"));
            newTodoItem.priority = getIntent().getExtras().getInt("priority");

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


    public void onCalendarClicked(View v) {


        CalendarFragment cf = new CalendarFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, cf).addToBackStack(null).commit();



    }


    public void onNewDateSelected(Date dateSelected)
    {

    }


    public void updateActivity(TodoItem note) {
        // TODO Auto-generated method stub
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        setNewDueDate(sdf.format(note.getDueDate()));
        String dateString = String.valueOf((note.getCreated()));
        String todoText = note.getTask();
        String noteText = " " + " \n" +  note.getNote();
        int priorityLevel = note.getPriority();

        String dueDateString = getNewDueDate();


        Intent result = new Intent();
        result.putExtra("todo", todoText);
        result.putExtra("date", dateString);
        result.putExtra("note", noteText);
        result.putExtra("priority", priorityLevel);
        result.putExtra("dueDate", dueDateString);

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
