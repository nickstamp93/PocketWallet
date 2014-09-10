package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import myexpenses.ng2.com.myexpenses.Data.ExpenseItem;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.CalendarDialog;

public class AddExpenseActivity extends Activity {

  private Button Ok,Cancel;
  private ImageButton Calendar,Camera;

  private EditText Notes,Price;
  private ImageView Photo;
  private Spinner Categories;
  private MoneyDatabase mydb;
  private ExpenseItem item;

  private boolean image;
  private  CalendarDialog dialog;

  private final int REQUEST_CODE=1;
  private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        initUi();
        initListeners();

        image=false;


        mydb=new MoneyDatabase(AddExpenseActivity.this);
        try {
            mydb.openDatabase();
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(),"Problem with our database",Toast.LENGTH_SHORT).show();
        }
    }

    private void initUi(){

      Ok=(Button) findViewById(R.id.bOK);
      Cancel=(Button) findViewById(R.id.bCancel);
      Calendar=(ImageButton) findViewById(R.id.ibCalendar);
      Camera=(ImageButton) findViewById(R.id.ibCamera);
      Price=(EditText) findViewById(R.id.etPrice);
      Notes=(EditText) findViewById(R.id.etNotes);
      Photo=(ImageView) findViewById(R.id.ivReceive);
      Categories=(Spinner) findViewById(R.id.sCategories);

    }

    private void initListeners(){
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ok=true;
                double price=0;
                String category,notes,date;
                try {
                    price = Double.parseDouble(String.valueOf(Price.getText()));
                }catch (NumberFormatException e){
                    ok=false;
                    Toast.makeText(getApplicationContext(),"Plz Press a numerical in Price and not a character",Toast.LENGTH_LONG).show();
                }
              if(ok){

               category=Categories.getSelectedItem().toString();
               notes=Notes.getText().toString();
               date=dialog.getDate();
               item=new ExpenseItem(category,notes,price,date);
               if(image){
                   item.setReceive(bm);
               }
               mydb.InsertExpense(item);
               mydb.close();
               finish();

              }
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finish();
            }
        });

        Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new CalendarDialog();
                dialog.show(getFragmentManager(),"Calendar Dialog");


            }
        });

        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            if(data!=null){
                image=true;
                bm=(Bitmap)data.getExtras().get("data");
                Photo.setImageBitmap(bm);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_expense, menu);
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


}
