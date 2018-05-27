package com.rym.benjmaa.alzheimermate.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rym.benjmaa.alzheimermate.Models.Medicament;
import com.rym.benjmaa.alzheimermate.R;
import com.rym.benjmaa.alzheimermate.Utils.AppSingleton;
import com.rym.benjmaa.alzheimermate.Utils.SharedData;
import com.rym.benjmaa.alzheimermate.controllers.MedsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MedsActivity extends AppCompatActivity {
    ListView lst;
    List<Medicament> medss;
    String url1;
    MedsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_meds);
        lst = findViewById(R.id.listview);
        url1 = SharedData.url+"getmed?malade="+SharedData.alzMail;
        makeJsonObjectRequest(url1);
    if (medss == null){
        medss = new ArrayList<>();
    }

    }

    public void makeJsonObjectRequest(String urlJsonObj) {


        String REQUEST_TAG = "com.androidAlzheimerMate.medRequest";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, (JSONObject) null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                JSONArray js = null;
                JSONArray js11 = null;
                try {
                     js = response.getJSONArray("data");
                    js11 = response.getJSONArray("photos");
                    medss = new ArrayList<>();
                    for (int i = 0; i < js.length(); i++) {
                       // JSONObject item = js.getJSONObject(i);
                        Medicament med = new Medicament();
                        med.setNom(js.getJSONObject(i).getString("nom"));
                        med.setNbPrises(js.getJSONObject(i).getInt("nbr_prises"));
                        med.setHeures_prises(js.getJSONObject(i).getString("heure_prise"));

                        med.setImage_med(js11.getString(i));

                        medss.add(med);
                        System.out.println("Liste médicaments"+med.toString());

                    }
                    adapter = new MedsAdapter(getBaseContext(), medss);

                    lst.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                System.out.println(error.getMessage());
            }
        });
        // Adding String request to request queue
        AppSingleton.getInstance(getApplication()).addToRequestQueue(jsonObjReq, REQUEST_TAG);

    }


}
