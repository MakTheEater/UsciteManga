package com.example.marco.mangauscite;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    Button btnClickMe, btnP, btnN, btnC;
    CheckBox out, own;
    ListView lv1;
    ImageView iv;
    //View v;
    Toolbar myToolbar;
    String testo="";
    ArrayList<ArrayList<Star>> uStar=new ArrayList<ArrayList<Star>>();
    ArrayList<ArrayList<Planet>> uPlanet=new ArrayList<ArrayList<Planet>>();
    ArrayList<Planet> temp;
    DBHandler db = new DBHandler(this);
    MyReader mr=null;
    MyParser mp=null;
    int wk=Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    int yr=Calendar.getInstance().get(Calendar.YEAR);
    String lastTitleDOne="";

    ArrayList<Uscita> vista=new ArrayList<Uscita>();

    private class MyReader extends AsyncTask<String, Void, String>
    {
        ArrayList<Uscita> us = new ArrayList<Uscita>();
        int sCont=0;

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                vista=new ArrayList<Uscita>();
                readDBStar();
                readDBPlanet();
            }
            catch (Exception e)
            {
            }
            return null;
        }

        public void readDBStar()
        {
            ArrayList<Star> s=db.getUsciteStar(wk, yr);
            sCont=s.size();
            for (int i=0; i<s.size(); i++)
            {
                us.add(s.get(i));
                vista.add(s.get(i));
            }
        }

        public void readDBPlanet()
        {
            ArrayList<Planet> p=db.getUscitePlanet(wk, yr);
            for (int i=0; i<p.size(); i++)
            {
                us.add(p.get(i));
                vista.add(p.get(i));
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            btnClickMe.setText(testo);
            btnClickMe.setEnabled(true);
            btnP.setEnabled(true);
            btnN.setEnabled(true);
            for (int i=0; i<us.size(); i++)
            {
                try
                {
                    if (new SimpleDateFormat("dd/MM/yyyy").parse(us.get(i).getData()).before(new Date())) {
                        us.get(i).setData(us.get(i).getData());
                    }
                }
                catch(ParseException e)
                {}
            }
            lv1.setAdapter(new CustomListAdapter(MainActivity.this, us, sCont, db, false));
        }

        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
        }

    }

    private class MyParser extends AsyncTask<String, Void, String>
    {
        public void parseStar()  throws IOException, ParseException
        {
            boolean save = true;
            int annoAttuale=Calendar.getInstance().get(Calendar.YEAR);
            int meseAttuale=Calendar.getInstance().get(Calendar.MONTH);
            boolean salta=false;
            String format = "dd/MM/yyyy";
            SimpleDateFormat df = new SimpleDateFormat(format);
            for (int y=annoAttuale; y<annoAttuale+2; y++)
            {
                for (int m=1; m<13; m++)
                {
                    salta=false;
                    if(y==annoAttuale && m<=meseAttuale-3)
                        salta=true;
                    if(y==annoAttuale-1 && m-meseAttuale>9)
                        salta=false;

                    if(!salta)
                    {
                        try
                        {
                            String link="";
                            if(m<10)
                                link+="0";
                            link+=m;
                            link+=y%2000;
                            String url = "http://maktheeater.altervista.org/"+link+".html";

                            Connection connectionTest = Jsoup.connect(url)
                                    .cookie("cookiereference", "cookievalue")
                                    .method(Connection.Method.POST);
                            Document doc = Jsoup.parse(new String(
                                    connectionTest.execute().bodyAsBytes(),"ISO-8859-15"));

                            Elements titles = doc.select("h4[class=title] > a");
                            uStar.add(new ArrayList<Star>());
                            for (Element t : titles)
                            {
                                String title=t.text().replace(" n. ", " ").toUpperCase();
                                //
                                //Fix da fare: caratteri speciali
                                //
                                if(title.contains("Â"))
                                    title=title.replace("Â", "'");
                                if(title.contains("\u0092"))
                                    title=title.replace("\u0092", "'");
                                if(title.contains("\u0096"))
                                    title=title.replace("\u0096", "-");

                                if(db.isPreso(title))
                                    uStar.get(uStar.size()-1).add(new Star(title, "", 0, 0, 0, "Si"));
                                else
                                    uStar.get(uStar.size()-1).add(new Star(title, "", 0, 0, 0, "No"));
                            }
                            Elements dates = doc.select("p > span");
                            int cont=0;
                            for (Element d : dates)
                            {
                                String data=d.text();
                                uStar.get(uStar.size()-1).get(cont).setData(data);
                                Date date = df.parse(data);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                int week = cal.get(Calendar.WEEK_OF_YEAR);
                                uStar.get(uStar.size()-1).get(cont).setSettimana(week);
                                cont++;
                            }
                            cont=0;
                            Elements prices = doc.select("span[class=price button_small] > span[class=wrap]");
                            for (Element p : prices)
                            {
                                String prezzo=p.text();
                                if (prezzo.startsWith("â"))
                                    prezzo=prezzo.substring(1);
                                uStar.get(uStar.size()-1).get(cont).setPrezzo(Double.parseDouble(prezzo.substring(2).replace(",", ".")));
                                cont++;
                            }
                            cont=0;
                            Elements covers = doc.select("div[class=photo] > a > img");
                            for (Element c : covers)
                            {
                                uStar.get(uStar.size()-1).get(cont).setCopertina("http://maktheeater.altervista.org/"+c.attr("src"));
                                cont++;
                            }
                        }
                        catch(HttpStatusException e)
                        {
                            save = false;
                            //Tante pagine non esistono, quindi evito di mostrare tante notifiche
                            /*Context context = getApplicationContext();
                            CharSequence text = "HttpStatusException!";
                            int duration = Toast.LENGTH_SHORT;
                            runOnUiThread(new Runnable(){

                                @Override
                                public void run(){
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            });*/
                        }
                        catch (SocketTimeoutException e)
                        {
                            save = false;
                            Context context = getApplicationContext();
                            CharSequence text = "SocketTimeoutException!";
                            int duration = Toast.LENGTH_SHORT;
                            runOnUiThread(new Runnable(){

                                @Override
                                public void run(){
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            });
                        }
                        catch (FileNotFoundException e)
                        {
                            Log.e("MYAPP", "exception", e);
                        }
                        catch (ParseException e)
                        {
                            Log.e("MYAPP", "exception", e);
                        }
                        catch (IOException e)
                        {
                            Log.e("MYAPP", "exception", e);
                        }
                    }
                }
            }
            int a=1+0;
        }

        public void parsePlanet()  throws IOException, ParseException
        {
            boolean save = true;
            temp=new ArrayList<Planet>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try
            {
                int pag=0;
                uPlanet.add(new ArrayList<Planet>());
                String ur0="http://comics.panini.it/calendario/uscite-scorse-settimane/";
                int i=0;
                int cont=0;
                while (pag<2)
                {
                    Document doc=Jsoup.connect(ur0).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                            .referrer("http://www.google.com")
                            .get();
                    Elements usc = doc.select("div[data-linea-editoriale=linea760695] > div > div[class=col-sm-3] > a[class=product-image] > img");
                    for (Element u : usc) {
                        temp.add(new Planet("", u.attr("src"), 0, 0, 0, "No"));
                    }
                    usc = doc.select("div[data-linea-editoriale=linea760695] > div > div[class=col-sm-9]");
                    i = cont;
                    //Prendo tutti i nomi
                    for (Element u : usc)
                    {
                        Elements title=u.select("h3 > a");
                        String nome=title.text();
                        //Copio descrizione nel nome per differenziare ristampe o altre edizioni
                        Elements serie=u.select("h3 > small[class=serie]");
                        String s=serie.text();
                        if (s.contains(nome) || s.contains(new String ("Naruto il mito ")))
                            nome=s;
                        Elements variant=u.select("h3 > small.subtitle.lightText");
                        if(!variant.text().equals(new String ("")))
                            nome=nome + ": " + variant.text();

                        Elements reprint=u.select("div[class=row] > div[class=col-sm-6] > div[class=pnn-attributes] > h5");
                        if(!reprint.text().equals(new String ("")))
                            nome=nome + " (" + reprint.text()+ ")";
                        temp.get(i).setNome(nome.toUpperCase());
                        if(db.isPreso(nome))
                            temp.get(i).setPreso("Si");
                        lastTitleDOne=nome;
                        i++;
                    }
                    usc = doc.select("div[data-linea-editoriale=linea760695] > div > div[class=col-sm-9] > div[class=row] > div[class=col-sm-6] > div[class=pnn-attributes] > p > span");
                    i = cont;
                    //Prendo tutte le date
                    for (Element u : usc)
                    {
                        temp.get(i).setData(u.text());
                        Calendar cal = Calendar.getInstance();
                        Date date = sdf.parse(u.text());
                        cal.setTime(date);
                        int week = cal.get(Calendar.WEEK_OF_YEAR);
                        temp.get(i).setSettimana(week);
                        i++;
                    }
                    usc = doc.select("div[data-linea-editoriale=linea760695] > div > div[class=col-sm-9] > div[class=row] > div[class=col-sm-12] > div[class=old-price]");
                    i = cont;
                    //Prendo tutti i prezzi
                    for (Element u : usc)
                    {
                        String p = u.text().trim().substring(2);
                        double prezzo = Double.parseDouble(p.replace(",", "."));
                        temp.get(i).setPrezzo(prezzo);
                        i++;
                    }
                    cont=temp.size();
                    pag++;
                    if (pag==1)
                        ur0="http://comics.panini.it/calendario/uscite-prossime-settimane/";
                }
            }
            catch (SocketTimeoutException e)
            {
                save = false;
                Context context = getApplicationContext();
                CharSequence text = "SocketTimeoutException!";
                int duration = Toast.LENGTH_SHORT;
                runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
            }
            catch(HttpStatusException e)
            {
                save = false;
                //Non troverà tante pagine Star Comics, quind ievito di mostrare le notifiche
                Context context = getApplicationContext();
                CharSequence text = "HttpStatusException!";
                int duration = Toast.LENGTH_SHORT;
                runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
            }
            if(save)
            {
                ArrayList<Date> date=new ArrayList<Date>();
                for (int j=0; j<temp.size(); j++)
                {
                    String dat=temp.get(j).getData();
                    if (!dat.equals(""))
                    {
                        Date parsed = sdf.parse(dat);
                        if (!date.contains(parsed))
                        {
                            date.add(parsed);
                        }
                    }
                }
                Collections.sort(date,
                        (o1, o2) -> (o1).compareTo(o2));
                for (int k = 0; k < date.size(); k++) {
                    uPlanet.add(new ArrayList<Planet>());
                }
                for (int j = 0; j < temp.size(); j++) {
                    String dat = temp.get(j).getData();
                    if (!dat.equals("")) {
                        Date data = sdf.parse(dat);
                        int pos = date.indexOf(data);
                        //Controllo che non ci siano doppioni (Planet ogni tanto ha le stesse uscite in più pagine)
                        boolean found = false;
                        for (int l = 0; l < uPlanet.get(pos).size(); l++) {
                            if (temp.get(j).getNome() == uPlanet.get(pos).get(l).getNome() && temp.get(j).getData() == uPlanet.get(pos).get(l).getData() && temp.get(j).getSettimana() == uPlanet.get(pos).get(l).getSettimana() && temp.get(j).getPrezzo() == uPlanet.get(pos).get(l).getPrezzo()) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            uPlanet.get(pos).add(temp.get(j));
                        }
                    }
                }
            }
        }

        /*public void parsePlanetOLD()  throws IOException, ParseException
        {
            ArrayList<Planet> temp=new ArrayList<Planet>();
            String format = "dd/MM/yyyy";
            SimpleDateFormat df = new SimpleDateFormat(format);
            int annoAttuale=Calendar.getInstance().get(Calendar.YEAR);
            int settimanaAttuale=Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            int lastMonth=-1;
            boolean salta=false;
            int contLast=0, contLast2=0;
            for (int y=annoAttuale; y<annoAttuale+2; y++)
            {
                for (int w=1; w<53; w++)
                {
                    salta=false;
                    if(y==annoAttuale && w<=settimanaAttuale-8)
                        salta=true;

                    try
                    {
                        if(!salta)
                        {
                            Document doc=Jsoup.connect("http://www.paninicomics.it/web/guest/planetmanga/checklist?year="+y+"&weekOfYear="+w).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                                    .referrer("http://www.google.com")
                                    .get();

                            Elements dates = doc.select("div[class=logo_brand] > span");
                            int cont=contLast2;
                            Elements titles = doc.select("div[class=cover] > img");
                            for (Element t : titles)
                            {
                                temp.add(new Planet(t.attr("alt"), "", "", 0, 0));
                                //System.out.println(t.attr("alt"));
                                //uPlanet.get(uPlanet.size()-1).add(new Star(t.text(), "", "", 0, 0));
                                contLast++;
                            }
                            for (Element d : dates)
                            {
                                String data=d.text();
                                temp.get(cont).setData(data);
                                Date date = df.parse(data);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                int week = cal.get(Calendar.WEEK_OF_YEAR);
                                temp.get(cont).setSettimana(week);
                                //System.out.println(data+"("+week+")");
                                cont++;
                            }
                            cont=contLast2;
                            Elements prices = doc.select("div[class=price] > h4");
                            for (Element p : prices)
                            {
                                //System.out.println(p.text().replaceAll("prezzo: € ", ""));
                                temp.get(cont).setPrezzo(Double.parseDouble(p.text().substring(p.text().lastIndexOf(" ")+1)));
                                cont++;
                            }
                            cont=contLast2;
                            Elements covers = doc.select("div[class=cover] > img");
                            for (Element c : covers)
                            {
                                //System.out.println("http://www.paninicomics.it/"+c.attr("src"));
                                temp.get(cont).setCopertina("http://maktheeater.altervista.org/"+c.attr("src"));
                                cont++;
                            }
                            contLast2=contLast;
                        }
                    }
                    catch(HttpStatusException e)
                    {}
                    catch (SocketTimeoutException e)
                    {}
                }
            }

            System.out.println(temp.size());
            for (int i=0; i<temp.size(); i++)
            {
                Date date = df.parse(temp.get(i).getData());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH);
                int week = cal.get(Calendar.WEEK_OF_YEAR);
                if (month!=lastMonth)
                {
                    uPlanet.add(new ArrayList<Planet>());
                }
                uPlanet.get(uPlanet.size()-1).add(new Planet(temp.get(i).getNome(), temp.get(i).getCopertina(), temp.get(i).getData(), temp.get(i).getPrezzo(), week));
                lastMonth=month;
                //System.out.println(temp.get(i).getData());
            }
        }*/

        public void delete(String t)
        {
            db.deleteUscite(t);
        }

        public void insert()
        {
            for (int i=0; i<uStar.size(); i++)
            {
                for (int j=0; j<uStar.get(i).size(); j++)
                {
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    Date dates = null;
                    try
                    {
                        dates = format.parse(uStar.get(i).get(j).getData());
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                    db.addUscita(new Star(uStar.get(i).get(j).getNome(), uStar.get(i).get(j).getCopertina(),  dates.getTime(), uStar.get(i).get(j).getPrezzo(), uStar.get(i).get(j).getSettimana(), uStar.get(i).get(j).getPreso()), "Star");
                }
            }

            for (int i=0; i<uPlanet.size(); i++)
            {
                for (int j=0; j<uPlanet.get(i).size(); j++)
                {
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    Date datep = null;
                    try
                    {
                        datep = format.parse(uPlanet.get(i).get(j).getData());
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                    db.addUscita(new Planet(uPlanet.get(i).get(j).getNome(), uPlanet.get(i).get(j).getCopertina(),  datep.getTime(), uPlanet.get(i).get(j).getPrezzo(), uPlanet.get(i).get(j).getSettimana(), uPlanet.get(i).get(j).getPreso()), "Planet");
                }
            }
        }

        @Override
        protected String doInBackground(String... params)
        {
            Document doc = null;
            uPlanet=new ArrayList<ArrayList<Planet>>();
            uStar=new ArrayList<ArrayList<Star>>();
            try {
                if (params[0].contains("Star"))
                {
                    parseStar();
                    delete("Star");
                }
                if (params[0].contains("Planet"))
                {
                    parsePlanet();
                    delete("Planet");
                }
                if(!params[0].equals(new String("")))
                {
                    insert();
                }
            }
            catch (ParseException e)
            {
                Log.e("MYAPP", "exception", e);
            }
            catch (IOException e)
            {
                Log.e("MYAPP", "exception", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            //setTitle(getTitle()+ " " + String.valueOf(uPlanet.size()) +" "+ lastTitleDOne);
            btnClickMe.setText(testo);
            //btnClickMe.setText(String.valueOf(uPlanet.size()));
            new MyReader().execute("StarPlanet");
        }

        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        findViewById(R.id.bgl0).setBackgroundColor(Color.rgb(175, 175, 175));

        btnClickMe = (Button) findViewById(R.id.button);
        btnP = (Button) findViewById(R.id.buttonP);
        btnN = (Button) findViewById(R.id.buttonN);
        btnC = (Button) findViewById(R.id.buttonClose);
        btnC.setVisibility(View.GONE);

        out = (CheckBox) findViewById(R.id.out);
        own = (CheckBox) findViewById(R.id.out2);

        btnClickMe.setBackgroundColor(Color.rgb(175, 175, 175));
        btnP.setBackgroundColor(Color.rgb(175, 175, 175));
        btnN.setBackgroundColor(Color.rgb(175, 175, 175));
        btnC.setBackgroundColor(Color.rgb(175, 175, 175));

        findViewById(R.id.bgl).setBackgroundColor(Color.rgb(175, 175, 175));

        iv = (ImageView) findViewById(R.id.imageView1);
        iv.setVisibility(View.GONE);

        /*v = (View) findViewById(R.id.view1);
        v.setBackgroundColor(Color.rgb(175, 175, 175));
        v.setVisibility(View.GONE);*/


        lv1 = (ListView) findViewById(R.id.custom_list);
        lv1.setBackgroundColor(Color.rgb(175, 175, 175));
        lv1.setLongClickable(true);
        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id)
            {
                    setTitle(vista.get(pos).getNome());
                    btnN.setEnabled(false);
                    btnP.setEnabled(false);
                    btnClickMe.setEnabled(false);
                    lv1.setEnabled(false);
                    iv.setVisibility(View.VISIBLE);
                    //v.setVisibility(View.VISIBLE);
                    btnC.setVisibility(View.VISIBLE);
                    new DownloadImageTask(iv).execute(vista.get(pos).getCopertina());
                return true;
            }
        });

        btnClickMe.setOnClickListener(MainActivity.this);
        btnP.setOnClickListener(MainActivity.this);
        btnN.setOnClickListener(MainActivity.this);
        btnC.setOnClickListener(MainActivity.this);

        if(db.getUsciteCount()<=0)
        {
            btnClickMe.setText("Aggiorna");
            btnN.setEnabled(false);
            btnP.setEnabled(false);
            btnClickMe.setEnabled(false);
            lv1.setClickable(false);
        }
        else
        {
            btnClickMe.performClick();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_update:
                // User chose the "Settings" item, show the app settings UI...
                btnClickMe.setEnabled(false);
                btnP.setEnabled(false);
                btnN.setEnabled(false);
                new MyParser().execute("StarPlanet");
                return true;
            case R.id.action_planet:
                // User chose the "Settings" item, show the app settings UI...
                btnClickMe.setEnabled(false);
                btnP.setEnabled(false);
                btnN.setEnabled(false);
                new MyParser().execute("Planet");
                return true;
            case R.id.action_star:
                // User chose the "Settings" item, show the app settings UI...
                btnClickMe.setEnabled(false);
                btnP.setEnabled(false);
                btnN.setEnabled(false);
                new MyParser().execute("Star");
                return true;

            case R.id.action_favs:
                // User chose the "Settings" item, show the app settings UI...
                printFav();
                return true;

            case R.id.action_mfavs:
                // User chose the "Settings" item, show the app settings UI...
                favs();
                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                settings();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void settings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void favs()
    {
        Intent intent = new Intent(this, FavsActivity.class);
        startActivity(intent);
    }

    private void printFav()
    {
        ArrayList<Uscita> list = db.getFavsList();
        vista=new ArrayList<Uscita>(list);

        //instantiate custom adapter

        CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, list, -1, db, true);

        //handle listview and assign adapter
        for (int i=0; i<list.size(); i++)
        {
            try
            {
                if (new SimpleDateFormat("dd/MM/yyyy").parse(list.get(i).getData()).before(new Date())) {
                    list.get(i).setData(list.get(i).getData());
                }
            }
            catch(ParseException e)
            {}
        }
        ListView lView = (ListView)findViewById(R.id.custom_list);
        lView.setAdapter(adapter);

        btnP.setEnabled(false);
        btnN.setEnabled(false);
        btnClickMe.setText("Preferiti");
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button:
                start();
                break;

            case R.id.buttonN:
                next();
                break;

            case R.id.buttonP:
                previous();
                break;

            case R.id.buttonClose:
                hide();
                break;

            default:
                break;
        }
    }

    public void onCheckboxClicked(View view)
    {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId())
        {
            case R.id.out:
                if (checked)
                    ((CheckBox)view).setChecked(false);
                else

                    ((CheckBox)view).setChecked(true);
                break;
            case R.id.out2:
                break;
        }
    }

    private void start()
    {
        int quanti = db.getUsciteCount();
        wk= Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        yr= Calendar.getInstance().get(Calendar.YEAR);
        if (quanti <= 0)
        {
            mp = new MyParser();
            mp.execute("StarPlanet");
        }
        else
        {
            mr= new MyReader();
            mr.execute("StarPlanet");
        }
        btnN.setEnabled(true);
        btnP.setEnabled(true);
        btnClickMe.setEnabled(true);
        testo=printDate();
    }

    private void hide()
    {
        btnC.setVisibility(View.GONE);
        iv.setVisibility(View.GONE);
        //v.setVisibility(View.GONE);
        btnN.setEnabled(true);
        btnP.setEnabled(true);
        btnClickMe.setEnabled(true);
        lv1.setEnabled(true);
        setTitle("Uscite Manga");
    }

    private void next()
    {
        if (wk < 52)
        {
            wk++;
        }
        else
        {
            wk=1;
            yr++;
        }

        testo=printDate();

        mr= new MyReader();
        mr.execute("StarPlanet");
    }

    private void previous()
    {
        if(wk>1)
        {
            wk--;
        }
        else
        {
            wk=52;
            yr--;
        }


        testo=printDate();

        mr= new MyReader();
        mr.execute("StarPlanet");
    }

    private String printDate()
    {
        String te="";

        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.WEEK_OF_YEAR, wk);
        cal.set(Calendar.YEAR, yr);



        te+=sdf.format(cal.getTime());
        cal.add(Calendar.DATE, 6);
        te+="\n"+sdf.format(cal.getTime());
        return te;
        //return "LUN "+sdf.format(cal.getTime());
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage)
    {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try
        {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e)
        {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result)
    {
        bmImage.setImageBitmap(result);
    }
}