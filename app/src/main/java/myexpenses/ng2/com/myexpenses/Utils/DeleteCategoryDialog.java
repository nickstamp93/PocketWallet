package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import myexpenses.ng2.com.myexpenses.Activities.CategoriesManagerActivity;
import myexpenses.ng2.com.myexpenses.Activities.CreateCategoryActivity;
import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 17/10/2014.
 */
public class DeleteCategoryDialog extends DialogFragment {

    private CheckBox cbChooseCat;
    private LinearLayout llCategories;
    private Button bOk, bCancel;
    private Spinner sCategories;
    private Dialog dialog;

    private CategoryDatabase cdb;
    private MoneyDatabase mdb;

    private String delCategory;
    private boolean expense;

    public DeleteCategoryDialog(String delCategory, boolean expense) {
        this.delCategory = delCategory;
        this.expense = expense;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_category_dialog);

        initUI();
        initBasicVariables();
        initListeners();


        return dialog;
    }

    private void initUI() {

        cbChooseCat = (CheckBox) dialog.findViewById(R.id.cbancat);
        llCategories = (LinearLayout) dialog.findViewById(R.id.llChooseCategory);
        bOk = (Button) dialog.findViewById(R.id.bdeletecOk);
        bCancel = (Button) dialog.findViewById(R.id.bdeletecCancel);
        sCategories = (Spinner) dialog.findViewById(R.id.sdCategories);


    }

    private void initBasicVariables() {

        for (int i = 0; i < llCategories.getChildCount(); i++) {
            View view = llCategories.getChildAt(i);
            view.setEnabled(false);
        }
        mdb = new MoneyDatabase(getActivity());
        cdb = new CategoryDatabase(getActivity());
        ArrayList<String> categories = cdb.getAllCategoriesExceptOne(delCategory, expense);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, R.id.tvFilters, categories);
        sCategories.setAdapter(adapter);

    }

    private void initListeners() {

        cbChooseCat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cbChooseCat.setChecked(b);

                for (int i = 0; i < llCategories.getChildCount(); i++) {
                    View view = llCategories.getChildAt(i);
                    view.setEnabled(b);
                }

            }
        });

        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbChooseCat.isChecked()) {
                    Log.i("OK", "Checked");
                    String newCategory=sCategories.getSelectedItem().toString();
                    mdb.updateTuplesDependedOnCategory(delCategory,expense,newCategory);
                    mdb.close();
                    cdb.deleteCategory(delCategory, expense);


/*
                    CategoriesManagerActivity activity = (CategoriesManagerActivity) getActivity();
                    activity.deleteHappen();
                    activity.refreshList(cdb.getAllCategories(expense));
                    */
                    cdb.close();
                    Toast.makeText(getActivity(), "All the data with category " + delCategory + " have been updated to "+newCategory, Toast.LENGTH_SHORT).show();


                    dialog.dismiss();
                    getActivity().finish();


                } else {
                    Log.i("OK", "!Checked");
                    mdb.deleteTuplesDependedOnCategory(delCategory, expense);
                    mdb.close();
                    cdb.deleteCategory(delCategory, expense);



                    /*
                    CategoriesManagerActivity activity = (CategoriesManagerActivity) getActivity();
                    activity.deleteHappen();
                    activity.refreshList(cdb.getAllCategories(expense));
                    cdb.close();
                    */

                    Toast.makeText(getActivity(), "All the data with category " + delCategory + " have been deleted", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    getActivity().finish();


                }

            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdb.close();
                cdb.close();
                dialog.dismiss();
            }
        });


    }

}
