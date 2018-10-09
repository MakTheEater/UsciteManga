package com.example.marco.mangauscite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class DBHandler extends SQLiteOpenHelper
{
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "usciteInfo";
    // Contacts table name
    private static final String TABLE_USCITE = "uscite";
    private static final String TABLE_FAVOURITES = "preferiti";
    private static final String TABLE_OWN = "presi";
    private static final String TABLE_SETTINGS = "impostazioni";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NOME = "nome";
    private static final String KEY_COPERTINA = "copertina";
    private static final String KEY_DATA = "data";
    private static final String KEY_PREZZO = "prezzo";
    private static final String KEY_SETTIMANA = "settimana";
    private static final String KEY_PRESO = "preso";
    private static final String KEY_TIPO = "tipo";
    private static final String KEY_VALUE = "valore";


    public DBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USCITE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NOME + " TEXT,"
                + KEY_COPERTINA + " TEXT,"
                //+ KEY_DATA + " TEXT,"
                + KEY_DATA + " NUMERIC,"
                + KEY_PREZZO + " REAL,"
                + KEY_SETTIMANA + " INTEGER,"
                + KEY_PRESO + " TEXT,"
                + KEY_TIPO + " TEXT" + ")";
        String CREATE_FAVOURITES_TABLE = "CREATE TABLE " + TABLE_FAVOURITES + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NOME + " TEXT" + ")";
        String CREATE_OWN_TABLE = "CREATE TABLE " + TABLE_OWN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NOME + " TEXT" + ")";
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NOME + " TEXT,"
                + KEY_VALUE + " INTEGER"+")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_FAVOURITES_TABLE);
        db.execSQL(CREATE_OWN_TABLE);
        db.execSQL(CREATE_SETTINGS_TABLE);

        createColors(db);
        createBaseSettings(db);
    }

    public void createBaseSettings(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_NOME, "LightTheme");
        values.put(KEY_VALUE, 1);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "DarkTheme");
        values.put(KEY_VALUE, 0);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "OrderDate");
        values.put(KEY_VALUE, 1);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "OrderEditor");
        values.put(KEY_VALUE, 0);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "OrderName");
        values.put(KEY_VALUE, 0);
        db.insert(TABLE_SETTINGS, null, values);
    }

    public void createColors(SQLiteDatabase db)
    {
        //Star
        ContentValues values = new ContentValues();
        values.put(KEY_NOME, "StarR");
        values.put(KEY_VALUE, 176);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "StarG");
        values.put(KEY_VALUE, 196);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "StarB");
        values.put(KEY_VALUE, 222);
        //Planet
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "PlanetR");
        values.put(KEY_VALUE, 233);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "PlanetG");
        values.put(KEY_VALUE, 150);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "PlanetB");
        values.put(KEY_VALUE, 122);
        //Favourites
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "FavouritesR");
        values.put(KEY_VALUE, 60);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "FavouritesG");
        values.put(KEY_VALUE, 179);
        db.insert(TABLE_SETTINGS, null, values);
        values = new ContentValues();
        values.put(KEY_NOME, "FavouritesB");
        values.put(KEY_VALUE, 113);
        db.insert(TABLE_SETTINGS, null, values);
    }

    public void updateColor(String name, int value)
    {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_VALUE, value);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_SETTINGS, newValues, "LOWER("+KEY_NOME+")='"+name.toLowerCase()+"'", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USCITE);
        // Creating tables again
        onCreate(db);
    }

    // Adding new shop
    public void addUscita(Uscita u, String t)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOME, u.getNome());
        values.put(KEY_COPERTINA, u.getCopertina());

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date dates = null;
        try
        {
            dates = format.parse(u.getData());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        values.put(KEY_DATA, dates.getTime());
        values.put(KEY_PREZZO, u.getPrezzo());
        values.put(KEY_SETTIMANA, u.getSettimana());
        values.put(KEY_PRESO, u.getPreso());
        values.put(KEY_TIPO, t);

        // Inserting Row
        db.insert(TABLE_USCITE, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<String> getFavs()
    {
        ArrayList<String> fav=new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_FAVOURITES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                // Adding contact to list
                fav.add(cursor.getString(1));
            }
            while (cursor.moveToNext());
        }
        return fav;
    }

    public ArrayList<Uscita> getFavsList()
    {
        Map<String, Integer> settings= readSettings();
        ArrayList<Uscita> fav=new ArrayList<Uscita>();
        int cont=0;
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_FAVOURITES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String where="WHERE (", orderby="";
        int favN=0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                String pref=cursor.getString(1); //"Naruto <Il mito> <gold>"
                String[] parts = pref.split("<");
                pref=parts[0];
                if(favN!=0)
                    where+=" OR ";
                where+="("+KEY_NOME+ " LIKE '%"+pref+"%'";
                if(parts.length>1)
                {
                    for (int i = 1; i < parts.length; i++)
                    {
                        parts[i] = parts[i].substring(0, parts[i].indexOf(">"));
                        parts[i]= parts[i].toLowerCase();
                        where+=" AND "+KEY_NOME+ " NOT LIKE '%"+parts[i]+"%'";
                    }
                }
                where+=")";
                favN++;

                /*String selectQuery2 = "SELECT * FROM " + TABLE_USCITE+ " WHERE "+KEY_NOME+ " LIKE '%"+pref+"%'";
                Cursor cursor2 = db.rawQuery(selectQuery2, null);

                boolean ad=true;

                // looping through all rows and adding to list
                if (cursor2.moveToFirst())
                {
                    do
                    {
                        for (int i = 1; i < parts.length; i++)
                        {
                            if (cursor2.getString(1).toLowerCase().contains(parts[i]))
                                ad=false;
                        }
                        if (ad)
                        {
                            // Adding contact to list
                            Uscita u = new Uscita(cursor2.getString(1), cursor2.getString(2), cursor2.getString(3), Double.parseDouble(cursor2.getString(4)), Integer.parseInt(cursor2.getString(5)), cursor2.getString(6));
                            // Adding contact to list
                            fav.add(u);
                        }
                        ad=true;
                    }
                    while (cursor2.moveToNext());
                }*/
            }
            while (cursor.moveToNext());
            where+=")";

            if(settings!=null && settings.get("OrderDate")==1)
            {
                orderby=" ORDER BY "+KEY_DATA+" ASC, "+KEY_NOME+" ASC";
            }
            else if(settings!=null && settings.get("OrderName")==1)
            {
                orderby=" ORDER BY "+KEY_NOME+" ASC";
            }
            else if(settings!=null && settings.get("OrderEditore")==1)
            {
                orderby=" ORDER BY "+KEY_TIPO+" ASC, "+KEY_NOME+" ASC";
            }

            String selectQuery2 = "SELECT * FROM " + TABLE_USCITE+ " "+where+orderby;
            Cursor cursor2 = db.rawQuery(selectQuery2, null);

            // looping through all rows and adding to list
            if (cursor2.moveToFirst())
            {
                do
                {
                    // Adding contact to list
                    //Uscita u = new Uscita(cursor2.getString(1), cursor2.getString(2), cursor2.getString(3), Double.parseDouble(cursor2.getString(4)), Integer.parseInt(cursor2.getString(5)), cursor2.getString(6));
                    Uscita u = new Uscita(cursor2.getString(1), cursor2.getString(2), cursor2.getLong(3), Double.parseDouble(cursor2.getString(4)), Integer.parseInt(cursor2.getString(5)), cursor2.getString(6));
                    // Adding contact to list
                    fav.add(u);
                }
                while (cursor2.moveToNext());
            }
        }
        return fav;
    }

    public void addFav(String f)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String countQuery = "SELECT * FROM " + TABLE_FAVOURITES + " WHERE "+KEY_NOME+" = '"+f+"'";
        Cursor cursorq = db.rawQuery(countQuery, null);
        //cursor.close();

        // return count
        if(cursorq.getCount()==0)
        {
            ContentValues values = new ContentValues();
            values.put(KEY_NOME, f);

            // Inserting Row
            db.insert(TABLE_FAVOURITES, null, values);
        }
        db.close(); // Closing database connection
    }

    public void editFav(String old, String f)
    {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NOME, f);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_FAVOURITES, newValues, "LOWER("+KEY_NOME+")='"+old.toLowerCase()+"'", null);
    }

    public void deleteFav(String nome)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_FAVOURITES+" WHERE "+KEY_NOME+" = '"+nome+"'");
        //db.delete(TABLE_USCITE, "", null);
        db.close();
    }

    public ArrayList<Planet> getUscitePlanet(int w, int y)
    {
        ArrayList<Planet> p=new ArrayList<Planet>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_USCITE+ " WHERE "+KEY_TIPO+" = 'Planet' AND "+ KEY_SETTIMANA+ " = " +w+" ORDER BY "+KEY_NOME;//+ " AND "+KEY_DATA + " LIKE '%"+y+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                if(getDate(cursor.getLong(3), "dd/MM/yyyy").endsWith(String.valueOf(y)))
                {
                    Planet pl = new Planet(cursor.getString(1), cursor.getString(2), cursor.getLong(3), Double.parseDouble(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), cursor.getString(6));
                    // Adding contact to list
                    p.add(pl);
                }
            }
            while (cursor.moveToNext());
        }

        // return contact list
        return p;
    }

    // Getting All Shops
    public ArrayList<Star> getUsciteStar(int w, int y)
    {
        ArrayList<Star> s=new ArrayList<Star>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_USCITE+ " WHERE "+KEY_TIPO+" = 'Star' AND "+ KEY_SETTIMANA+ " = "+w+" ORDER BY "+KEY_NOME;//+ " AND "+KEY_DATA + " LIKE '%"+y+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                if(getDate(cursor.getLong(3), "dd/MM/yyyy").endsWith(String.valueOf(y)))
                {
                    Star pl = new Star(cursor.getString(1), cursor.getString(2), cursor.getLong(3), Double.parseDouble(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), cursor.getString(6));
                    // Adding contact to list
                    s.add(pl);
                }
            }
            while (cursor.moveToNext());
        }

        // return contact list
        return s;
    }

    public void deleteUscite(String t)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_USCITE+" WHERE "+KEY_TIPO+" = '"+t+"'");
        //db.delete(TABLE_USCITE, "", null);
        db.close();
    }

    // Getting shops Count
    public int getUsciteCount()
    {
        String countQuery = "SELECT * FROM " + TABLE_USCITE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();

        // return count
        return cursor.getCount();
    }

    public void setPreso(String nome, String p)
    {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_PRESO, p);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_USCITE, newValues, "LOWER("+KEY_NOME+")='"+nome.toLowerCase()+"'", null);
    }

    public int getPresiCount()
    {
        String countQuery = "SELECT * FROM " + TABLE_USCITE + " WHERE "+ KEY_PRESO + "='Si'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();

        // return count
        return cursor.getCount();
    }

    public void addPreso(String t)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOME, t);
        // Inserting Row
        db.insert(TABLE_OWN, null, values);
        db.close(); // Closing database connection
    }

    public void deletePreso(String t)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_OWN+" WHERE LOWER("+KEY_NOME+") = '"+t.toLowerCase().replace("'", "''")+"'");
        //db.delete(TABLE_USCITE, "", null);
        db.close();
    }

    public boolean isPreso(String t)
    {
        String countQuery = "SELECT * FROM " + TABLE_OWN + " WHERE LOWER("+ KEY_NOME + ") = '"+t.toLowerCase().replace("'", "''")+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        boolean ok;
        if(cursor.getCount()>0)
            ok=true;
        else
            ok=false;
        return ok;
    }

    public Map<String, Integer> readSettings()
    {
        Map<String, Integer> settings=new HashMap<String, Integer>();
        String settingsQuery = "SELECT * FROM " + TABLE_SETTINGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(settingsQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                settings.put(cursor.getString(1), cursor.getInt(2));
            }
            while (cursor.moveToNext());
        }
        // return contact list
        return settings;
    }

    public void saveSettings(Map<String, Integer> settings)
    {
        Map<String,Integer> appSettings=readSettings();
        SQLiteDatabase db = this.getWritableDatabase();
        for (Map.Entry<String,Integer> entry : settings.entrySet())
        {
            String key=entry.getKey();
            Integer value=entry.getValue();
            if(appSettings.get(key)==null)
            {
                ContentValues values = new ContentValues();
                values.put(KEY_NOME, key);
                values.put(KEY_VALUE, value);
                db.insert(TABLE_SETTINGS, null, values);
            }
            else
            {
                ContentValues newValues = new ContentValues();
                newValues.put(KEY_VALUE, value);
                db.update(TABLE_SETTINGS, newValues, "LOWER("+KEY_NOME+")='"+key.toLowerCase()+"'", null);
            }

        }
        db.close();
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}

