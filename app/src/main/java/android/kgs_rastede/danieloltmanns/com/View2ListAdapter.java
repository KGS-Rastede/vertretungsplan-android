package android.kgs_rastede.danieloltmanns.com;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Daniel Oltmanns on 15.02.2016.
 */
public class View2ListAdapter extends ArrayAdapter<View2ListItem> {

    private ArrayList<View2ListItem> objects;

    public View2ListAdapter(Context c, int res, ArrayList<View2ListItem> obj) {
        super(c,res,obj);
        this.objects = obj;
    }

    public View getView(int pos, View cview,ViewGroup vg) {
        View v = cview;

        if(v == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.view_2_item,null);
        }

        View2ListItem i = objects.get(pos);

        if(i != null) {
            String info = "";
            info = i.getInfo();
            if(info.equals("Frei")) { info = ""; }
            //Textview wird geladen
            TextView tv_lesson = (TextView)v.findViewById(R.id.tv_info);
            //Text wird in die Textview gespeichert
            tv_lesson.setText(info);
            //Text-Align wird auf center gesetzt
            tv_lesson.setGravity(Gravity.CENTER);

            if(info.equals("Entfall")) { tv_lesson.setTextColor(Color.parseColor("#e74c3c")); }
            else { tv_lesson.setTextColor(Color.parseColor("#000000")); }
            tv_lesson.setBackgroundColor(Color.parseColor(i.getColor()));
        }

        return v;
    }
}
