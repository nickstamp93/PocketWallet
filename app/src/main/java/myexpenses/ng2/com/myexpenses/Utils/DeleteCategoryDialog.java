package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 17/10/2014.
 */
public class DeleteCategoryDialog extends DialogFragment {

    private CheckBox cbChooseCat;
    private Button bOk, bCancel;
    private Spinner sCategories;
    private Dialog dialog;

    private CategoryDatabase cdb;
    private MoneyDatabase mdb;

    private String delCategory;
    private boolean expense;

    private ArrayList<String> categories;
    private SpinnerAdapter adapter;

    public DeleteCategoryDialog(String delCategory, boolean expense) {
        this.delCategory = delCategory;
        this.expense = expense;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_category_dialog);

        init();
        initUI();
        setUpUI();

        return dialog;
    }

    private void initUI() {

        cbChooseCat = (CheckBox) dialog.findViewById(R.id.cbancat);
        sCategories = (Spinner) dialog.findViewById(R.id.sdCategories);

        bOk = (Button) dialog.findViewById(R.id.bOK);
        bCancel = (Button) dialog.findViewById(R.id.bCancel);

    }

    private void init() {

        mdb = new MoneyDatabase(getActivity());
        cdb = new CategoryDatabase(getActivity());
        categories = cdb.getAllCategoriesExceptOne(delCategory, expense);

    }

    private void setUpUI() {

        //=========================spinner==========================================================
        ArrayList<SpinnerItem> spinnerItems = new ArrayList<SpinnerItem>();

        for (int i = 0; i < categories.size(); i++) {
            String name = categories.get(i);
            int color = cdb.getColorFromCategory(name, true);
            char letter = cdb.getLetterFromCategory(name, true);
            spinnerItems.add(new SpinnerItem(name, color, letter));

        }

        adapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, spinnerItems);

        sCategories.setAdapter(adapter);
        sCategories.setEnabled(false);

        //========================check box move listener===========================================
        cbChooseCat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cbChooseCat.setChecked(b);
                sCategories.setEnabled(b);

            }
        });

        //====================================button ok listener=====================================
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbChooseCat.isChecked()) {

                    int position = sCategories.getSelectedItemPosition();

                    String newCategory = categories.get(position);

                    mdb.updateTuplesDependedOnCategory(delCategory, expense, newCategory);
                    mdb.close();
                    cdb.deleteCategory(delCategory, expense);

                    cdb.close();

                    dialog.dismiss();
                    getActivity().finish();

                } else {
                    mdb.deleteTuplesDependedOnCategory(delCategory, expense);
                    mdb.close();
                    cdb.deleteCategory(delCategory, expense);


                    dialog.dismiss();
                    getActivity().finish();

                }
                Toast.makeText(getActivity(), "Category " + delCategory + " deleted", Toast.LENGTH_SHORT).show();

            }
        });

        //========================================= button cancel listener==========================
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
