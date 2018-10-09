package com.example.marco.mangauscite;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

class CustomListAdapter extends BaseAdapter
{
    Map<String, Integer> settings;
    //private ArrayList<Star> listData;
    private ArrayList<Uscita> listData;
    private LayoutInflater layoutInflater;
    int rs=0, gs=0, bs=0;
    int rp=0, gp=0, bp=0;
    int rf=0, gf=0, bf=0;

    int s;
    DBHandler db;
    boolean favs;

    public CustomListAdapter(Context aContext, ArrayList<Uscita> listData, int s, DBHandler d, boolean f)
    {
        this.listData = listData;
        this.s=s;
        db=d;
        layoutInflater = LayoutInflater.from(aContext);
        favs=f;
        settings=db.readSettings();

        rs=settings.get("StarR");
        gs=settings.get("StarG");
        bs=settings.get("StarB");
        rp=settings.get("PlanetR");
        gp=settings.get("PlanetG");
        bp=settings.get("PlanetB");
        rf=settings.get("FavouritesR");
        gf=settings.get("FavouritesG");
        bf=settings.get("FavouritesB");
    }

    @Override
    public int getCount()
    {
        return listData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            holder.priceView = (TextView) convertView.findViewById(R.id.price);
            holder.dateView = (TextView) convertView.findViewById(R.id.date);
            holder.out = (CheckBox) convertView.findViewById(R.id.out);
            holder.own = (CheckBox) convertView.findViewById(R.id.out2);
            holder.preso = (TextView) convertView.findViewById(R.id.preso);
            holder.uscito = (TextView) convertView.findViewById(R.id.uscito);

            /*if(settings.get("LightTheme")==1)
            {
                holder.titleView.setTextColor(Color.rgb(0,0,0));
                holder.priceView.setTextColor(Color.rgb(0,0,0));
                holder.dateView.setTextColor(Color.rgb(0,0,0));
                holder.preso.setTextColor(Color.rgb(0,0,0));
                holder.uscito.setTextColor(Color.rgb(0,0,0));
            }
            else
            {
                holder.titleView.setTextColor(Color.rgb(7,7,7));
                holder.priceView.setTextColor(Color.rgb(7,7,7));
                holder.dateView.setTextColor(Color.rgb(7,7,7));
                holder.preso.setTextColor(Color.rgb(7,7,7));
                holder.uscito.setTextColor(Color.rgb(7,7,7));
            }*/


            holder.own.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if(isChecked)
                    {
                        db.setPreso(holder.titleView.getText().toString().replace("'", "''").trim(), "Si");
                        db.addPreso(holder.titleView.getText().toString().trim());
                    }
                    else
                    {
                        db.setPreso(holder.titleView.getText().toString().replace("'", "''").trim(), "No");
                        db.deletePreso(holder.titleView.getText().toString().trim());
                    }
                }
            });

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleView.setText(listData.get(position).getNome());
        holder.priceView.setText("Prezzo: " + listData.get(position).getPrezzo()+"\u20ac");
        holder.dateView.setText("Uscita: " + listData.get(position).getData());
        if (listData.get(position).getPreso().equals("Si"))
        {
            holder.own.setChecked(true);
        }
        else
        {
            holder.own.setChecked(false);
        }
        try
        {
            String us=listData.get(position).getData();
            Date da=new SimpleDateFormat("dd/MM/yyyy").parse(us);
            if (da.before(new Date()))
            {
                holder.out.setChecked(true);
                //holder.titleView.setText(holder.titleView.getText()+ "/////"+da.toString()+" prima di "+new Date().toString());
                int deb=0;
                /*if (favs)
                {
                    String titolo=listData.get(position).getNome();
                    Date uscita= new SimpleDateFormat("dd/MM/yyyy").parse(listData.get(position).getData());
                    Date today= new Date();
                    boolean uscito=uscita.before(today);
                    deb=1;
                }*/
            }
            else
            {
                holder.out.setChecked(false);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if(s!=-1)
        {
            if (position <s)
            {
                convertView.setBackgroundColor(Color.rgb(rs,gs,bs));
            }
            else
            {
                convertView.setBackgroundColor(Color.rgb(rp,gp,bp));
            }
        }
        else
            convertView.setBackgroundColor(Color.rgb(rf,gf,bf));

        return convertView;
    }

    static class ViewHolder
    {
        TextView titleView;
        TextView priceView;
        TextView dateView;
        TextView preso;
        TextView uscito;

        CheckBox out;
        CheckBox own;
    }
}