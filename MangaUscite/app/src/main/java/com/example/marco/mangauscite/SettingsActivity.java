package com.example.marco.mangauscite;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener
{
    DBHandler db = new DBHandler(this);
    CheckBox orderEditor;
    CheckBox orderDate;
    CheckBox orderName;
    CheckBox light;
    CheckBox dark;
    Button save;
    Button cancel;
    Button star;
    Button planet;
    Button favourites;
    EditText starEdit, planetEdit, favouritesEdit;


    TextView titleView;
    TextView priceView;
    TextView dateView;
    TextView preso;
    TextView uscito;
    Map<String, Integer> settings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings=db.readSettings();
        findViewById(R.id.bgl0).setBackgroundColor(Color.rgb(175, 175, 175));
        orderEditor=(CheckBox) findViewById(R.id.orderEditor);
        orderDate=(CheckBox) findViewById(R.id.orderDate);
        orderName=(CheckBox) findViewById(R.id.orderName);
        light=(CheckBox) findViewById(R.id.lightTheme);
        dark=(CheckBox) findViewById(R.id.darkTheme);
        save=(Button) findViewById(R.id.buttonSave);
        cancel=(Button) findViewById(R.id.buttonCancel);
        star=(Button) findViewById(R.id.btnStar);
        planet=(Button) findViewById(R.id.btnPlanet);
        favourites=(Button) findViewById(R.id.btnFavourites);
        starEdit=(EditText)findViewById(R.id.editStar);
        planetEdit=(EditText)findViewById(R.id.editPlanet);
        favouritesEdit=(EditText)findViewById(R.id.editFavourites);
        save.setOnClickListener(SettingsActivity.this);
        cancel.setOnClickListener(SettingsActivity.this);
        star.setOnClickListener(SettingsActivity.this);
        planet.setOnClickListener(SettingsActivity.this);
        favourites.setOnClickListener(SettingsActivity.this);
        readSettings();
    }

    public void onCheckboxClicked(View view)
    {
        // Check which checkbox was clicked
        switch(view.getId())
        {
            case R.id.orderDate:
                orderEditor.setChecked(!orderDate.isChecked());
                orderName.setChecked(!orderDate.isChecked());
                break;
            case R.id.orderEditor:
                orderDate.setChecked(!orderEditor.isChecked());
                orderName.setChecked(!orderEditor.isChecked());
                break;
            case R.id.orderName:
                orderDate.setChecked(!orderName.isChecked());
                orderEditor.setChecked(!orderName.isChecked());
                break;
            case R.id.dark:
                light.setChecked(!dark.isChecked());
                break;
            case R.id.light:
                dark.setChecked(!light.isChecked());
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.buttonSave:
                saveSettings();
                finish();
                break;
            case R.id.buttonCancel:
                finish();
                break;
            case R.id.btnStar:
                //if(Pattern.matches("^\\d\\d\\d\\p{Punct}\\d\\d\\d\\p{Punct}\\d\\d\\d$", starEdit.getText()))
                {
                    String[] sColors= starEdit.getText().toString().split(";");
                    db.updateColor("StarR", Integer.parseInt(sColors[0]));
                    db.updateColor("StarG", Integer.parseInt(sColors[1]));
                    db.updateColor("StarB", Integer.parseInt(sColors[2]));
                    star.setBackgroundColor(Color.rgb(Integer.parseInt(sColors[0]), Integer.parseInt(sColors[1]), Integer.parseInt(sColors[2])));
                }
                break;
            case R.id.btnPlanet:
                //if(Pattern.matches("^\\d\\d\\d\\p{Punct}\\d\\d\\d\\p{Punct}\\d\\d\\d$", planetEdit.getText()))
                {
                    String[] pColors= planetEdit.getText().toString().split(";");
                    db.updateColor("PlanetR", Integer.parseInt(pColors[0]));
                    db.updateColor("PlanetG", Integer.parseInt(pColors[1]));
                    db.updateColor("PlanetB", Integer.parseInt(pColors[2]));
                    planet.setBackgroundColor(Color.rgb(Integer.parseInt(pColors[0]), Integer.parseInt(pColors[1]), Integer.parseInt(pColors[2])));
                }
                break;
            case R.id.btnFavourites:
                //if(Pattern.matches("^\\d\\d\\d\\p{Punct}\\d\\d\\d\\p{Punct}\\d\\d\\d$", favouritesEdit.getText()))
                {
                    String[] fColors= favouritesEdit.getText().toString().split(";");
                    db.updateColor("FavouritesR", Integer.parseInt(fColors[0]));
                    db.updateColor("FavouritesG", Integer.parseInt(fColors[1]));
                    db.updateColor("FavouritesB", Integer.parseInt(fColors[2]));
                    favourites.setBackgroundColor(Color.rgb(Integer.parseInt(fColors[0]), Integer.parseInt(fColors[1]), Integer.parseInt(fColors[2])));
                }
                break;

            default:
                break;
        }
    }

    public void saveSettings()
    {
        Map<String, Integer> settings=new HashMap<String, Integer>();
        settings.put("OrderDate",orderDate.isChecked()?1: 0 );
        settings.put("OrderEditor",orderEditor.isChecked()?1: 0 );
        settings.put("OrderName",orderName.isChecked()?1: 0 );
        settings.put("LightTheme",light.isChecked()?1: 0 );
        settings.put("DarkTheme",dark.isChecked()?1: 0 );
        db.saveSettings(settings);
        //if(Pattern.matches("^\\d\\d\\d\\p{Punct}\\d\\d\\d\\p{Punct}\\d\\d\\d$", starEdit.getText()))
        {
            String[] sColors= starEdit.getText().toString().split(";");
            db.updateColor("StarR", Integer.parseInt(sColors[0]));
            db.updateColor("StarG", Integer.parseInt(sColors[1]));
            db.updateColor("StarB", Integer.parseInt(sColors[2]));
        }
        //if(Pattern.matches("^\\d\\d\\d\\p{Punct}\\d\\d\\d\\p{Punct}\\d\\d\\d$", planetEdit.getText()))
        {
            String[] pColors= planetEdit.getText().toString().split(";");
            db.updateColor("PlanetR", Integer.parseInt(pColors[0]));
            db.updateColor("PlanetG", Integer.parseInt(pColors[1]));
            db.updateColor("PlanetB", Integer.parseInt(pColors[2]));
        }
        //if(Pattern.matches("^\\d\\d\\d\\p{Punct}\\d\\d\\d\\p{Punct}\\d\\d\\d$", favouritesEdit.getText()))
        {
            String[] fColors= favouritesEdit.getText().toString().split(";");
            db.updateColor("FavouritesR", Integer.parseInt(fColors[0]));
            db.updateColor("FavouritesG", Integer.parseInt(fColors[1]));
            db.updateColor("FavouritesB", Integer.parseInt(fColors[2]));
        }
    }

    public void readSettings()
    {
        int rs=0, gs=0, bs=0;
        int rp=0, gp=0, bp=0;
        int rf=0, gf=0, bf=0;
        for (Map.Entry<String,Integer> entry : settings.entrySet())
        {
            String key=entry.getKey();
            Integer value=entry.getValue();
            boolean blvalue=value==0? false: true;
            switch(key)
            {
                case "OrderDate": orderDate.setChecked(blvalue); break;
                case "OrderEditor": orderEditor.setChecked(blvalue); break;
                case "OrderName": orderName.setChecked(blvalue); break;
                case "LightTheme": light.setChecked(blvalue); break;
                case "DarkTheme": dark.setChecked(blvalue); break;
                case "PlanetR": rp=value; break;
                case "PlanetB": bp=value; break;
                case "PlanetG": gp=value; break;
                case "StarR": rs=value; break;
                case "StarB": bs=value; break;
                case "StarG": gs=value; break;
                case "FavouritesR": rf=value; break;
                case "FavouritesB": bf=value; break;
                case "FavouritesG": gf=value; break;
            }
        }
        star.setBackgroundColor(Color.rgb(rs, gs, bs));
        starEdit.setText(rs+";"+gs+";"+bs);
        planet.setBackgroundColor(Color.rgb(rp, gp, bp));
        planetEdit.setText(rp+";"+gp+";"+bp);
        favourites.setBackgroundColor(Color.rgb(rf, gf, bf));
        favouritesEdit.setText(rf+";"+gf+";"+bf);
    }
}
