package com.eugenesumaryev.floatingbuttons;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by eugenesumaryev on 10/21/17.
 */
public class NewDialogFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static String DIALOG_TODO = "DIALOG_TODO";
    private static String DIALOG_NOTE = "DIALOG_NOTE";
    private static String DIALOG_DATE = "DIALOG_DATE";
    private static String DIALOG_PRIORITY = "DIALOG_PRIORITY";
    private static String DIALOG_DUEDATE = "DIALOG_DUEDATE";
    private int newPriorityLevel;
    private TextView tv, et2, subTask, noteditabletext;
    private EditText editabletext, et, noteText;
    private static TodoItem todoItem;
    private boolean returned;


    public interface OnUpdateClickedListener {
        public void updateActivity(TodoItem note);
        public void cancelActivity();
        public void onDateClicked(View v);
        public void onCalendarClicked(View v);
    }

    private OnUpdateClickedListener onUpdateClickedListener;

    public static NewDialogFragment newInstance(Context context, TodoItem _todo) {

        // Create a new Fragment instance with the specified
        // parameters.
        NewDialogFragment fragment = new NewDialogFragment();
        Bundle args = new Bundle();

        todoItem = _todo;
       // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String dateString = sdf.format(todoItem.getCreated());
        String todoText = todoItem.getTask();
       // Log.v("TodoFromNDF ", todoText);
        String noteText = todoItem.getNote();
     //   Log.v("AndnoteTEXT ", noteText);
        int priorityLevel = todoItem.getPriority();
        String dueDateString = sdf.format(todoItem.getDueDate());

        args.putString(DIALOG_TODO, todoText);
        args.putString(DIALOG_NOTE, noteText);
        args.putString(DIALOG_DATE, dateString);
        args.putInt(DIALOG_PRIORITY, priorityLevel);
        args.putString(DIALOG_DUEDATE, dueDateString);
        fragment.setArguments(args);

        Log.v("priortyNwInstnceNDF : ", Integer.toString(todoItem.getPriority()));

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_practice_layout, container, false);

        String newTodo = getArguments().getString(DIALOG_TODO);
        int stringLimit = (newTodo.length()<10) ? newTodo.length() : 10;
        //newTodo = newTodo.trim().substring(0, stringLimit);
        String newNote = getArguments().getString(DIALOG_NOTE);
        //final String[] modNote = newNote.split("\n", 2);
        String newDueDate = getArguments().getString(DIALOG_DUEDATE);
        newPriorityLevel = getArguments().getInt(DIALOG_PRIORITY);
        String newDueDateString = getArguments().getString(DIALOG_DUEDATE);

        noteText = (EditText)view.findViewById(R.id.content_text);
        noteText.setText(newNote);

        editabletext = (EditText)view.findViewById(R.id.editableTodoText);
        editabletext.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                   // editabletext.setText(modNote[0]);
                } else {

                }
            }
        });

        if (newTodo == "")
            editabletext.setEnabled(false);


        editabletext.setText(newTodo);



        tv = (TextView)view.findViewById(R.id.due_date);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        if (returned = true) {
            tv.setText("Due Date: " + sdf.format(todoItem.dueDate));
            returned = false;
        }
        else
            tv.setText("Due Date: " + newDueDate);

        Spinner spinner = (Spinner) view.findViewById(R.id.priority_spinner);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.priority_array, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(newPriorityLevel);


        //SetDate button to set up due date
        Button calButton = (Button)view.findViewById(R.id.button_id2);
        calButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onUpdateClickedListener.onCalendarClicked(v);
            }
        });

        Button okButton = (Button)view.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When okButton is clicked, call up to owning activity and save.
                //okClicked;
                //Activity will populate selected note with info

                todoItem.task = editabletext.getText().toString();
                todoItem.note = noteText.getText().toString();
		    
                onUpdateClickedListener.updateActivity(todoItem);

            }
        });

        Button cancelButton = (Button)view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When cancelButton is clicked, just return.
                // dismiss();
                onUpdateClickedListener.cancelActivity();
            }
        });



        return view;
    }


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            // onDateClickedListener = (OnDateClickedListener)activity;
            onUpdateClickedListener = (OnUpdateClickedListener)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnDateClickedListener");
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //An item was selected. You can retrieve the selected item using
        String selection = (String) parent.getItemAtPosition(pos);



        if (selection.equals("HIGH"))
            todoItem.priority = 2;
        else if (selection.equals("MEDIUM")) {
            todoItem.priority = 1;
        }
        else
            todoItem.priority = 0;

    }

    public void onNothingSelected(AdapterView<?> parent) {
        //An item was selected. You can retrieve the selected item using
        parent.setSelection(newPriorityLevel);

    }


    public void updateDate(Date newDate) {
	    
        todoItem.dueDate = newDate;
        returned = true;

    }

}
