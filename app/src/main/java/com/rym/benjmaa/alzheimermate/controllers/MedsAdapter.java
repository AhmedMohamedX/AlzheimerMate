package com.rym.benjmaa.alzheimermate.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rym.benjmaa.alzheimermate.Models.CircleTransform;
import com.rym.benjmaa.alzheimermate.Models.Medicament;
import com.rym.benjmaa.alzheimermate.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Rym on 19/11/2017.
 */

public class MedsAdapter extends ArrayAdapter<Medicament> {
    Context ctx;
    //private List<Medicament> array;
    public static class ViewHolder{
        TextView txtNom;
        TextView txtnbPrise;
        TextView txtHeures;
        ImageView image;
    }

    public MedsAdapter(@NonNull Context context, List<Medicament> array) {
        super(context, 0, array);
        this.ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Medicament med = getItem(position);
        ViewHolder vh;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(ctx);
            convertView = inflater.inflate(R.layout.med_row, parent, false);
            vh = new ViewHolder();
            vh.txtNom = convertView.findViewById(R.id.txt_nom);
            vh.txtnbPrise = convertView.findViewById(R.id.txt_nbPrises);
            vh.txtHeures =  convertView.findViewById(R.id.txt_heures);
            vh.image =  convertView.findViewById(R.id.imMed_id);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        vh.txtNom.setText("Medecine name: "+med.getNom());
        vh.txtnbPrise.setText(String.valueOf(med.getNbPrises())+" prise(s) per day");
        vh.txtHeures.setText("Chaque "+med.getHeures_prises());
       /* String jo1 =  med.getImage_med();
        byte[] decodedString = Base64.decode(jo1, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        System.out.println(med.getImage_med());
        Bitmap bm =Bitmap.createScaledBitmap(decodedByte, 1024, 1024, false);
        vh.image.setImageBitmap(getCircleBitmap(bm));*/

        Picasso.with(ctx).load(med.getImage_med()).transform(new CircleTransform()).into( vh.image);
        return convertView;
    }
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}
