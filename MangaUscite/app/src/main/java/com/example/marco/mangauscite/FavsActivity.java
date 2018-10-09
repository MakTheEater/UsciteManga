package com.example.marco.mangauscite;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class FavsActivity extends AppCompatActivity implements View.OnClickListener
{
    Button btnAdd;
    ListView lv1;
    EditText tf;
    DBHandler db = new DBHandler(this);
    boolean edit=false;
    String oldTitle="";

    public void setTextBar(String title)
    {
        edit=true;
        tf.setText(title);
        oldTitle=title;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.add_fav:
                if(!edit)
                {
                    addF();
                }
                else
                {
                    editF();
                    edit=false;
                }
                printF();
                break;

            default:
                break;
        }
    }

    public void addF()
    {
        String f=tf.getText().toString();
        if(!f.equals(new String("")))
        {
            db.addFav(f);
            tf.setText("");
        }
    }

    public void editF()
    {
        String f=tf.getText().toString();
        if(!f.equals(new String("")))
        {
            db.editFav(oldTitle, f);
            tf.setText("");
        }
    }

    public void printF()
    {
        ArrayList<String> list = db.getFavs();

        //instantiate custom adapter
        CustomListAdapter2 adapter = new CustomListAdapter2(list, this, db);

        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.favs_list);
        lView.setAdapter(adapter);
    }

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favs);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.favs_toolbar);
        //setSupportActionBar(myToolbar);
        // myToolbar.setBackgroundColor(Color.rgb(25, 25, 25);

        tf = (EditText) findViewById(R.id.editText);
        tf.setBackgroundColor(Color.WHITE);

        btnAdd = (Button) findViewById(R.id.add_fav);
        btnAdd.setBackgroundColor(Color.rgb(175, 175, 175));

        findViewById(R.id.bgl3).setBackgroundColor(Color.rgb(175, 175, 175));

        lv1 = (ListView) findViewById(R.id.favs_list);
        lv1.setBackgroundColor(Color.rgb(3, 158, 60));

        btnAdd.setOnClickListener(FavsActivity.this);

        printF();

        /*if(db.getUsciteCount()<=0)
        {
            btnClickMe.setText("Aggiorna");
            btnN.setEnabled(false);
            btnP.setEnabled(false);
        }
        else
        {
            btnClickMe.performClick();
        }*/
    }
	
}
