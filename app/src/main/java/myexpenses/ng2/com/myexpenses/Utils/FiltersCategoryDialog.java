package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 14/9/2014.
 */
public class FiltersCategoryDialog extends DialogFragment  {

    private Button bOk,bCancel;
    private RadioGroup rgCategories;
    private RadioButton rbCategory;

    private Dialog dialog;
    private String category;
    private Category cat;

public interface Category{
    public void getTheCategoryFromDialogFragment(String category);
}


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog=new Dialog(getActivity());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filters_catergory_dialog);

        initUI();
        initListeners();

        return dialog;
    }

    private void initUI(){

        rgCategories=(RadioGroup)dialog.findViewById(R.id.rgCategories);
        bOk=(Button) dialog.findViewById(R.id.bdCatOk);
        bCancel=(Button)dialog.findViewById(R.id.bdCatCancel);

        cat=(Category) getTargetFragment();

        int selectedId=rgCategories.getCheckedRadioButtonId();
        rbCategory=(RadioButton) rgCategories.findViewById(selectedId);
        category=rbCategory.getText().toString();
        cat.getTheCategoryFromDialogFragment(category);

    }



    private void initListeners(){


      bOk.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

           int selectedId=rgCategories.getCheckedRadioButtonId();
           rbCategory=(RadioButton) rgCategories.findViewById(selectedId);
           category=rbCategory.getText().toString();
           cat.getTheCategoryFromDialogFragment(category);
           getTargetFragment().onResume();
           dialog.dismiss();
          }
      });


        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           Toast.makeText(getActivity(),"Return back no choice happened",Toast.LENGTH_LONG).show();
           dialog.dismiss();
            }
        });

    }


}
