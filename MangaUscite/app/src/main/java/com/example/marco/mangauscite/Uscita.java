package com.example.marco.mangauscite;

import java.text.SimpleDateFormat;
import java.util.Calendar;

class Uscita
{
    protected String nome;
    protected String copertina;
    protected String data;
    protected double prezzo;
    protected int settimana;
    protected String preso;

    public Uscita(String n, String c, long d, double p, int s, String pr)
    {
        nome=n;
        copertina=c;
        data=getDate(d, "dd/MM/yyyy");
        //data=d;
        prezzo=p;
        settimana=s;
        preso=pr;
    }

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public String getNome()
    {
        return nome;
    }

    public String getCopertina()
    {
        return copertina;
    }

    public String getData()
    {
        return data;
    }

    public double getPrezzo()
    {
        return prezzo;
    }

    public int getSettimana()
    {
        return settimana;
    }

    public String getPreso()
    {
        return preso;
    }

    public void setNome(String n)
    {
        nome=n;
    }

    public void setCopertina(String c)
    {
        copertina=c;
    }

    public void setData(String d)
    {
        data=d;
    }

    public void setPrezzo(double p)
    {
        prezzo=p;
    }

    public void setSettimana(int s)
    {
        settimana=s;
    }

    public void setPreso(String p)
    {
        preso=p;
    }
}

class Planet extends Uscita
{
    public Planet(String nome, String cover, long data, double prezzo, int settimana, String preso)
    {
        super (nome, cover, data, prezzo, settimana, preso);
    }
}

class Star extends Uscita
{
    public Star(String nome, String cover, long data, double prezzo, int settimana, String preso)
    {
        super (nome, cover, data, prezzo, settimana, preso);
    }
}
