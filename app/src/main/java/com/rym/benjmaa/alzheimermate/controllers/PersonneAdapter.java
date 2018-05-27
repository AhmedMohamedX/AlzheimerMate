package com.rym.benjmaa.alzheimermate.controllers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rym.benjmaa.alzheimermate.Models.MembreFamille;
import com.rym.benjmaa.alzheimermate.R;

import java.util.List;

/**
 * Created by Rym on 12/12/2017.
 */

public class PersonneAdapter  extends ArrayAdapter<MembreFamille> {
    private  Context ctx;

    public static class ViewHolder {
        TextView txtNom_p;
        TextView txtlien;
        TextView txtemail;
        TextView txtnum_tel;
        ImageView image;
        Button call;
    }

    public PersonneAdapter(@NonNull Context context, List<MembreFamille> array) {
        super(context, 0, array);
        this.ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MembreFamille per = getItem(position);
       final PersonneAdapter.ViewHolder vh;
        System.out.println(per.getImage_per());
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            convertView = inflater.inflate(R.layout.family_row, parent, false);
            vh = new PersonneAdapter.ViewHolder();
            vh.txtNom_p = convertView.findViewById(R.id.txt_nom_p);
            vh.txtlien = convertView.findViewById(R.id.txt_lien_parente);
            vh.txtemail = convertView.findViewById(R.id.txt_email);
            vh.txtnum_tel = convertView.findViewById(R.id.txt_num_tel);
            vh.image = convertView.findViewById(R.id.imPer_id);
            vh.call=convertView.findViewById(R.id.call);
            convertView.setTag(vh);
        } else {
            vh = (PersonneAdapter.ViewHolder) convertView.getTag();
        }
        vh.txtNom_p.setText(per.getNom()+" "+ per.getPrenom());
        vh.txtlien.setText(per.getLien());
        vh.txtemail.setText(per.getEmail());
        vh.txtnum_tel.setText(String.valueOf(per.getNum_tel()));
        vh.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        // a potentially  time consuming task
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + vh.txtnum_tel.getText().toString()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                ctx.startActivity(callIntent);

                            }
                        }
                    }


                }).start();

            }
        });
       String jo1 = per.getImage_per();
        byte[] decodedString = Base64.decode(jo1, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        System.out.println(per.getImage_per());
        Bitmap bm =Bitmap.createScaledBitmap(decodedByte, 125, 125, false);
        vh.image.setImageBitmap(bm);

        // Picasso.with(ctx).load(per.getImage_per()).into(vh.image);
    //  Picasso.with(ctx).load(per.getImage_per()).transform(new CircleTransform()).into(vh.image);

        return convertView;
    }

}