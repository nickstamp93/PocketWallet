package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.Activities.CategoriesManagerActivity;
import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 9/24/2014.
 */
//dialog for adding a new category
public class AddCategoryDialog extends DialogFragment{

    //dialog object
    Dialog dialog;
    //buttons
    Button bOk , bCancel;
    //edit text name
    EditText etName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //init dialog
        dialog = new Dialog(getActivity());

        //remove title feature from dialog
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //set content view
        dialog.setContentView(R.layout.dialog_add_category);

        //init UI
        initUI();

        //init Listeners
        initListeners();

        //return the dialog object
        return dialog;

    }

    //init UI
    private void initUI(){
        bOk = (Button) dialog.findViewById(R.id.bOk);
        bCancel = (Button) dialog.findViewById(R.id.bCancel);

        etName = (EditText) dialog.findViewById(R.id.etName);
    }

    //init Listeners
    private void initListeners(){
        bOk.setOnClickListener(listener);
        bCancel.setOnClickListener(listener);
    }

    //click listener
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //button ok clicked
                case R.id.bOk:
                    //get the typed name
                    String name = etName.getText().toString();
                    //if its a valid name (> 0 letters)
                    if(name.trim().length() > 0){
                        //open category db and store it there
                        CategoryDatabase db = new CategoryDatabase(getActivity());
                        db.insertCategory(name);
                        db.close();

                        //notify user
                        Toast.makeText(getActivity() , "Category \"" + name +  "\"\n created successfully" , Toast.LENGTH_SHORT).show();

                        //close dialog
                        dismiss();
                    }

                    break;

                //button cancel clicked
                case R.id.bCancel:
                    //close dialog without saving anything
                    dismiss();
                    break;
            }
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        //on dialog dismiss refresh the categories list
        ((CategoriesManagerActivity)getActivity()).refreshList();
    }
}
