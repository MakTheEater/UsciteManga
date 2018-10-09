package com.example.marco.mangauscite;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

class CustomListAdapter2 extends BaseAdapter
{
    Map<String, Integer> settings;
    private ArrayList<String> listData;
    private LayoutInflater layoutInflater;
    int s;
    DBHandler db;
    Context c;
    int rf=0, gf=0, bf=0;

    public CustomListAdapter2(ArrayList<String> listData, Context aContext, DBHandler d)
    {
        this.listData = listData;
        db=d;
        c=aContext;
        layoutInflater = LayoutInflater.from(aContext);
        settings=db.readSettings();
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
            convertView = layoutInflater.inflate(R.layout.my_custom_list_layout, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            holder.remove = (Button) convertView.findViewById(R.id.delete_btn);
            holder.remove.setOnClickListener(new View.OnClickListener()
            {
                public void onClick (View v)
                {
                    String title=holder.titleView.getText().toString();
                    db.deleteFav(title);
                    Object item = getItem(position);
                    listData.remove(item);
                    notifyDataSetChanged();
                }
            });
            holder.edit = (Button) convertView.findViewById(R.id.edit_btn);
            holder.edit.setOnClickListener(new View.OnClickListener()
            {
                public void onClick (View v)
                {
                    ((FavsActivity)c).setTextBar(holder.titleView.getText().toString());
                }
            });

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.titleView.setText(listData.get(position));
        convertView.setBackgroundColor(Color.rgb(rf,gf,bf));

        return convertView;
    }

    static class ViewHolder
    {
        TextView titleView;
        Button remove;
        Button edit;
    }
}