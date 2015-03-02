package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.ColorPicker.ColorPickerDialog;
import myexpenses.ng2.com.myexpenses.ColorPicker.ColorPickerSwatch;
import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.DeleteCategoryDialog;
import myexpenses.ng2.com.myexpenses.Utils.LetterImageView;
import myexpenses.ng2.com.myexpenses.Utils.Themer;

public class CreateCategoryActivity extends Activity implements ColorPickerSwatch.OnColorSelectedListener {

    private EditText etName;
    private ImageButton ibColor;
   // private CheckBox cbpreview;
    private Button bOk, bCancel;
    private LetterImageView liv;
    private ColorPickerDialog dialog;
    private LinearLayout llCatPreview;


    private String name;
    private char letter;
    private int color,id;
    private boolean expense;

    private CategoryDatabase cdb;
    private  DeleteCategoryDialog deleteDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Themer.setThemeToActivity(this);

        setContentView(R.layout.activity_create_category);

        initUi();
        initListeners();
        initBasicVariables();

    }


    private void initUi() {
        etName = (EditText) findViewById(R.id.etCatName);
        ibColor = (ImageButton) findViewById(R.id.ibCatColor);
       // cbpreview = (CheckBox) findViewById(R.id.cbCatPreview);
        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);
        liv = (LetterImageView) findViewById(R.id.livCatPreview);
        liv.setOval(true);
       // llCatPreview = (LinearLayout) findViewById(R.id.llCatPreview);
    }

    private void initBasicVariables() {

        int[] mColor = new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.BLACK, Color.CYAN, Color.DKGRAY, Color.GRAY
                , Color.LTGRAY, Color.MAGENTA, Color.YELLOW};
        dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title, mColor, 0, 4, ColorPickerDialog.SIZE_SMALL);
        dialog.setSelectedColor(Color.RED);
        dialog.setOnColorSelectedListener(this);

        name = getIntent().getStringExtra("Name");
        if(name!=null){
            color=getIntent().getIntExtra("Color",0);
            letter=getIntent().getStringExtra("Letter").charAt(0);
            id=getIntent().getIntExtra("Id",-2);
            initUiValues();
            Log.i("Update","U");
        }else{
            Log.i("Insert","I");
            color = Color.RED;
            liv.setmBackgroundPaint(color);
            id=-1;
        }

        expense = getIntent().getExtras().getBoolean("Expense");


        cdb = new CategoryDatabase(getApplicationContext());

    }

    private void initUiValues(){

        etName.setText(name);
       // cbpreview.setChecked(true);
        dialog.setSelectedColor(color);
        liv.setmBackgroundPaint(color);
        liv.setLetter(name.charAt(0));

    }

    private void initListeners() {

        ibColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(id==-1) {
                    dialog.setSelectedColor(Color.RED);
                }*/
                dialog.show(getFragmentManager(), "Color Picker");
            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                if(charSequence.toString().trim().length()>0){
                    letter=charSequence.toString().trim().charAt(0);

                }else{
                    letter=' ';
                }
                liv.setLetter(letter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

/*
        cbpreview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cbpreview.setChecked(b);
                if (cbpreview.isChecked()) {
                    name = etName.getText().toString();
                    //Check if the user gave a name
                    if (name.equals("Name") || name.equals("")) {
                        Toast.makeText(getApplicationContext(), "Plz write a name", Toast.LENGTH_SHORT).show();
                        cbpreview.setChecked(false);
                    } else {
                        //If he gave then we take the name and the color and we display them in the LetterImageView.
                        //Default value of color is red so even if the user doesn't select color we don't have problem.
                        String newCat = etName.getText().toString().toUpperCase();

                        letter = newCat.toUpperCase().charAt(0);

                        color = dialog.getSelectedColor();
                        liv.setOval(true);
                        liv.setLetter(letter);
                        liv.setmBackgroundPaint(color);




                    }

                }
            }
        });
*/

        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    name = etName.getText().toString().trim();
                    //Check if the user gave a name
                    if ( name.length()==0) {
                        Toast.makeText(getApplicationContext(), "Plz write a name", Toast.LENGTH_SHORT).show();
                    } else {

                        if (cdb.checkIfNameExists(name, expense) && id==-1) {
                            Toast.makeText(getApplicationContext(), "There is already a category with this name plz try again", Toast.LENGTH_LONG).show();
                        } else {
                            String newCat = etName.getText().toString().trim().toUpperCase();
                            letter=newCat.charAt(0);
                            String sletter = String.valueOf(letter);

                            color=dialog.getSelectedColor();
                            if (cdb.checkIfLetterAndColorExists(sletter, color, expense)) {
                                Toast.makeText(getApplicationContext(), "There is already a category with this letter and color plz try again", Toast.LENGTH_LONG).show();

                            } else {
                                if(id==-1) {
                                    cdb.insertCategory(name, sletter, color, expense);
                                }else{

                                    cdb.updateCategory(id,name,sletter,color,expense);
                                }
                                cdb.close();
                                finish();
                            }

                        }

                    }


            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdb.close();
                finish();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_category, menu);
        if(id!=-1){
            final MenuItem delete=menu.add("Delete").setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.i("MenuItem", "activated");
                    MoneyDatabase mdb=new MoneyDatabase(CreateCategoryActivity.this);
                    if(mdb.CategoryHasItems(name,expense)){

                        deleteDialog = new DeleteCategoryDialog(name, expense);
                        deleteDialog.show(getFragmentManager(), "Delete");

                    }else {
                        cdb.deleteCategory(name,expense);
                        finish();
                    }
                    mdb.close();
                   // finish();
                    return false;
                }
            });
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onColorSelected(int color) {
        this.color=color;
        dialog.setSelectedColor(color);
        liv.setmBackgroundPaint(color);
        liv.invalidate();
    }
}


