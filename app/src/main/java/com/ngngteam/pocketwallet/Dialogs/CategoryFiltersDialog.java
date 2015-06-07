package com.ngngteam.pocketwallet.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.R;

import java.util.ArrayList;

/**
 * Created by Nikos on 7/6/2015.
 */
public class CategoryFiltersDialog extends DialogFragment {

    private CategoryDatabase categoryDatabase;
    ListView listView;
    CustomAdapter adapter;

    private Dialog dialog;
    ArrayList<String> checkedCategories;


    public CategoryFiltersDialog() {

    }

    public static CategoryFiltersDialog newInstance(ArrayList checkedCategories) {
        CategoryFiltersDialog d = new CategoryFiltersDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable("arg", checkedCategories);

        d.setArguments(bundle);

        return d;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_categories_filters);

        checkedCategories = (ArrayList) getArguments().getSerializable("arg");
//        expense = getArguments().getBoolean("key_expense");

        init();
        initUI();
        setUpUI();

        return dialog;
    }


    private void initUI() {
        listView = (ListView) dialog.findViewById(R.id.listCategories);
    }

    private void init() {
        categoryDatabase = new CategoryDatabase(getActivity());

        adapter = new CustomAdapter(getActivity(), R.layout.list_item_category_filter, checkedCategories);

    }

    private void setUpUI() {

        listView.setAdapter(adapter);
    }

    private class CustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> checked;
        private ArrayList<String> allCategories;
        private int resource;

        public CustomAdapter(Context context, int resource, ArrayList<String> checkedCategories) {
            super(context, resource, checkedCategories);
            allCategories = categoryDatabase.getCategories(true);
            this.resource = resource;
            this.checked = checkedCategories;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(resource, parent, false);
            }

            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.chbCategory);
            checkBox.setChecked(true);
            checkBox.setText(allCategories.get(position) );
            return convertView;
        }
    }

}
