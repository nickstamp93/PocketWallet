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
public class AddCategoryDialog extends DialogFragment{

    Dialog dialog;
    Button bOk , bCancel;
    EditText etName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_add_category);


        initUI();

        initListeners();

        return dialog;

    }

    private void initUI(){
        bOk = (Button) dialog.findViewById(R.id.bOk);
        bCancel = (Button) dialog.findViewById(R.id.bCancel);

        etName = (EditText) dialog.findViewById(R.id.etName);
    }

    private void initListeners(){
        bOk.setOnClickListener(listener);
        bCancel.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bOk:
                    String name = etName.getText().toString();
                    if(name.trim().length() > 0){
                        CategoryDatabase db = new CategoryDatabase(getActivity());
                        db.insertCategory(name);
                        db.close();

                        Toast.makeText(getActivity() , "Category \"" + name +  "\"\n created successfully" , Toast.LENGTH_SHORT).show();
                        
                        dismiss();
                    }

                    break;

                case R.id.bCancel:
                    dismiss();
                    break;
            }
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        ((CategoriesManagerActivity)getActivity()).refreshList();
    }
}
