package android.kgs_rastede.danieloltmanns.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Daniel Oltmanns on 14.02.2016.
 */
public class View1ListAdapter extends ArrayAdapter<View1ListItem> {
    private ArrayList<View1ListItem> objects;

    public View1ListAdapter(Context c, int res, ArrayList<View1ListItem> obj) {
        super(c,res,obj);
        this.objects = obj;
    }

    public View getView(int pos, View cview,ViewGroup vg) {
        View v = cview;

        if(v == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.view_1_item,null);
        }

        View1ListItem i = objects.get(pos);

        if(i != null) {
            //Textviews erden geladen und abgeändert
            TextView tv_date = (TextView)v.findViewById(R.id.tv_date);
            TextView tv_hour = (TextView)v.findViewById(R.id.tv_hour);
            TextView tv_subject = (TextView)v.findViewById(R.id.tv_subject);
            TextView tv_teacher = (TextView)v.findViewById(R.id.tv_teacher);
            TextView tv_status = (TextView)v.findViewById(R.id.tv_status);
            TextView tv_room = (TextView)v.findViewById(R.id.tv_room);
            TextView tv_supply = (TextView)v.findViewById(R.id.tv_supply);

            Date date = new Date(Long.parseLong(i.getDate())*1000);
            SimpleDateFormat df = new SimpleDateFormat("d.M.yyyy", Locale.GERMANY);
            tv_date.setText(df.format(date));

            tv_hour.setText(i.getHour());
            tv_subject.setText(i.getSubject());
            tv_teacher.setText(i.getTeacher());
            tv_status.setText(i.getStatus());
            tv_room.setText(i.getRoom());
            tv_supply.setText(i.getSupply());
        }

        return v;
    }
}
