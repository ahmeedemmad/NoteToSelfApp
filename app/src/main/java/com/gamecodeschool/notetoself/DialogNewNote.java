package com.gamecodeschool.notetoself;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class DialogNewNote extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        /* AlertDialog.Builder type that needs a reference to the Activity that is passed in its constructor. This is why we use
getActivity() as the argument. The getActivity method is part of the Fragment class (and therefore, DialogFragment too), and it returns a reference to the Activity
that created DialogFragment */
        LayoutInflater inflater = getActivity().getLayoutInflater();
        /* we initialize a LayoutInflater object, which we will use to inflate our XML layout. Inflate simply means to turn our XML layout into a Java object. Once this
is done, we can then access all our widgets in the usual way. We can think of inflater.inflate replacing setContentView for our dialog. And in the second
line, we do just that with the inflate method.
*/
        View dialogView = inflater.inflate(R.layout.dialog_new_note, null);

        //Many of the objects are declared final because they will be used in an anonymous class
        final EditText editTitle = (EditText) dialogView.findViewById(R.id.editTitle);
        final EditText editDescription = (EditText) dialogView.findViewById(R.id.editDescription);
        final CheckBox checkBoxIdea = (CheckBox) dialogView.findViewById(R.id.checkBoxIdea);
        final CheckBox checkBoxTodo = (CheckBox) dialogView.findViewById(R.id.checkBoxTodo);
        final CheckBox checkBoxImportant = (CheckBox) dialogView.findViewById(R.id.checkBoxImportant);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        Button btnOK = (Button) dialogView.findViewById(R.id.btnOK);
        builder.setView(dialogView).setTitle("Add a new note");
// Handle the cancel button
        btnCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // Handle the OK button
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Create a new note
                Note newNote = new Note();
// Set its variables to match the users entries on the form
                newNote.setTitle(editTitle.getText().toString());
                newNote.setDescription(editDescription.getText().toString());
                newNote.setIdea(checkBoxIdea.isChecked());
                newNote.setToDo(checkBoxTodo.isChecked());
                newNote.setImportant(checkBoxImportant.isChecked());
// Get a reference to MainActivity
                MainActivity callingActivity = (MainActivity) getActivity();
// Pass newNote back to MainActivity
                callingActivity.createNewNote(newNote);
// Quit the dialog
                dismiss();
            }
        });
        return builder.create();
    }
}

