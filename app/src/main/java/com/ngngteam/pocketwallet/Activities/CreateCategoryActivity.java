package com.ngngteam.pocketwallet.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Dialogs.DeleteCategoryDialog;
import com.ngngteam.pocketwallet.Extra.ColorPicker.ColorPickerDialog;
import com.ngngteam.pocketwallet.Extra.ColorPicker.ColorPickerSwatch;
import com.ngngteam.pocketwallet.Extra.LetterImageView;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.util.ArrayList;

public class CreateCategoryActivity extends AppCompatActivity implements ColorPickerSwatch.OnColorSelectedListener {

    private EditText etName;
    private ImageButton ibColor;
    private Button bOk, bCancel;
    private LetterImageView liv;
    private ColorPickerDialog dialog;

    private String name;
    private char letter;
    private int color, id;
    private boolean expense;

    private CategoryDatabase cdb;
    private DeleteCategoryDialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_create_category);

        initUI();
        setUpUI();
        init();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    private void initUI() {

        etName = (EditText) findViewById(R.id.etCatName);

        ibColor = (ImageButton) findViewById(R.id.ibCatColor);

        liv = (LetterImageView) findViewById(R.id.livCatPreview);
        liv.setOval(true);

        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);
    }

    private void init() {

        //get colors from a string array named paletteColors
        ArrayList<Integer> colors = new ArrayList();
        String[] array = getResources().getStringArray(R.array.categoryColors);
        //and convert it to array of int , for the color picker
        for (String item : array) {
            colors.add(Color.parseColor(item));
        }
        int[] mColor = new int[colors.size()];
        for (int i = 0; i < colors.size(); i++) {
            mColor[i] = colors.get(i);
        }

        //init the color picker dialog
        dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title, mColor, 0, 4, ColorPickerDialog.SIZE_SMALL);
        dialog.setOnColorSelectedListener(this);

        //get the category name from the intent
        name = getIntent().getStringExtra("Name");
        if (name != null) {
            //if there is a name in the intent , then it's edit mode
            //get the rest attributes from the intent
            color = getIntent().getIntExtra("Color", 0);
            letter = getIntent().getStringExtra("Letter").charAt(0);
            id = getIntent().getIntExtra("Id", -2);
            fillUIValues();
        } else {
            //if there is no String name in the Intent , then it's creation mode
            color = mColor[0];
            liv.setmBackgroundPaint(color);
            id = -1;
        }
        dialog.setSelectedColor(color);

        expense = getIntent().getExtras().getBoolean("Expense");

        cdb = new CategoryDatabase(getApplicationContext());

    }

    private void fillUIValues() {

        etName.setText(name);
        dialog.setSelectedColor(color);
        liv.setmBackgroundPaint(color);
        liv.setLetter(name.charAt(0));

    }

    private void setUpUI() {

        //color picker listener
        ibColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show(getFragmentManager(), "Color Picker");
            }
        });

        //edit text listener
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                if (charSequence.toString().trim().length() > 0) {
                    letter = charSequence.toString().toUpperCase().trim().charAt(0);

                } else {
                    letter = ' ';
                }
                liv.setLetter(letter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //ok button listener
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = etName.getText().toString().trim();
                //Check if the user gave a name
                if (name.length() == 0) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_category), Toast.LENGTH_SHORT).show();
                } else {

                    if (cdb.checkIfNameExists(name, expense) && id == -1) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_category_name_conflict), Toast.LENGTH_SHORT).show();
                    } else {
                        String newCat = etName.getText().toString().trim().toUpperCase();
                        letter = newCat.charAt(0);
                        String sletter = String.valueOf(letter);

                        color = dialog.getSelectedColor();
                        //if we are trying to add a category with color-letter already in db , prevent it
                        if (cdb.checkIfLetterAndColorExists(sletter, color, expense) && (id == -1)) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_category_color_combination), Toast.LENGTH_SHORT).show();

                        } else {
                            if (id == -1) {
                                cdb.insertCategory(name, sletter, color, expense);
                            } else {
                                cdb.updateCategory(id, name, sletter, color, expense);
                                if (expense) {
                                    new MoneyDatabase(CreateCategoryActivity.this).updateCategory(getIntent().getStringExtra("Name"), name);
                                } else {
                                    new MoneyDatabase(CreateCategoryActivity.this).updateSource(getIntent().getStringExtra("Name"), name);
                                }
                            }
                            cdb.close();
                            finish();
                        }
                    }
                }
            }
        });

        //button cancel listener
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
        //if it's edit mode , add the action in the action bar for deleting the category
        if (id != -1) {
            final MenuItem delete = menu.add(getResources().getString(R.string.action_delete)).setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    MoneyDatabase mdb = new MoneyDatabase(CreateCategoryActivity.this);
                    if (mdb.categoryHasItems(name, expense)) {

                        deleteDialog = DeleteCategoryDialog.newInstance(name, expense);
                        deleteDialog.show(getFragmentManager(), "Delete");

                    } else {
                        cdb.deleteCategory(name, expense);
                        Toast.makeText(CreateCategoryActivity.this, name + " " + getResources().getString(R.string.toast_category_deleted), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    mdb.close();
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public void onColorSelected(int color) {
        this.color = color;
        dialog.setSelectedColor(color);
        dialog.dismiss();
        liv.setmBackgroundPaint(color);
        liv.invalidate();
    }
}


