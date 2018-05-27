package com.rym.benjmaa.alzheimermate.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.rym.benjmaa.alzheimermate.Activities.DetailsActivity;
import com.rym.benjmaa.alzheimermate.Activities.Main2Activity;
import com.rym.benjmaa.alzheimermate.Models.MembreFamille;
import com.rym.benjmaa.alzheimermate.R;
import com.rym.benjmaa.alzheimermate.Utils.AppSingleton;
import com.rym.benjmaa.alzheimermate.Utils.SharedData;
import com.rym.benjmaa.alzheimermate.controllers.GridViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FamilyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FamilyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FamilyFragment extends Fragment {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    ProgressBar progressBar;
   // ListView lst;
    List<MembreFamille> membres;
    String url1;
   //PersonneAdapter adapter;
    Intent intent;
    private OnFragmentInteractionListener mListener;
    private boolean connected;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 100;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    //private GifLoadingView mGifLoadingView;
    public FamilyFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FamilyFragment newInstance() {
        FamilyFragment fragment = new FamilyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connected = checkInternetConnection(getContext());
      /*  if(FamilyFragment.this.isVisible()){
            System.out.println("family members on\n ********************************************************");
        }*/
        if(!connected){
            new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("You must be connected to retrieve data")
                    .setCustomImage(R.drawable.no_service)
                    .setConfirmText("Turn On!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            getActivity().finish();
                        }
                    })
                    .show();
        }else {
            System.out.println("family loading");
            // lst = view.findViewById(R.id.listviewper);
            url1 = SharedData.url + "getper?malade=" + SharedData.alzMail;
            makeJsonObjectRequest(url1);
            if (membres == null) {
                membres = new ArrayList<>();
            }
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {
                showPermissionAlert();
            }
            gridView = view.findViewById(R.id.gridView);
            progressBar = view.findViewById(R.id.spin_kit_dp);
            WanderingCubes doubleBounce = new WanderingCubes();
            progressBar.setIndeterminateDrawable(doubleBounce);
            gridView.setVisibility(View.INVISIBLE);
            mWaveSwipeRefreshLayout =  view.findViewById(R.id.main_swipe_family);
            mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
            mWaveSwipeRefreshLayout.setWaveColor(Color.argb(255,86,0,39));
            FrameLayout fm = view.findViewById(R.id.framefam);

            mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
                @Override public void onRefresh() {
                    // Do work to refresh the list here.(red: 0.34, green: 0.00, blue: 0.15, alpha: 1.0)
                    new Task().execute();
                }
            });
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    MembreFamille item = (MembreFamille) parent.getItemAtPosition(position);

                    System.out.println(item.toString());

                    intent = new Intent(getContext(), DetailsActivity.class);
                    intent.putExtra("np", item.getPrenom() + " " + item.getNom());
                    intent.putExtra("image", item.getImage_per());
                    intent.putExtra("lien", item.getLien());
                    intent.putExtra("email", item.getEmail());
                    intent.putExtra("num", item.getNum_tel());

                    //Start details activity
                    startActivity(intent);


                    // getFragmentManager().beginTransaction().add(DetailsFragment.newInstance(item.getNom()+ " "+ item.getPrenom(),item.getLien(),item.getNum_tel(),item.getEmail(),item.getImage_per()));

                }
            });
        }
    }
    private void showPermissionAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.permission_request_title);
        builder.setMessage(R.string.app_permission_notice);
        builder.create();
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED*/
                // {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,}, PERMISSION_LOCATION_REQUEST_CODE);
                // }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), R.string.permission_refused, Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
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
                    membres = new ArrayList<>();
                    for (int i = 0; i < js.length(); i++) {
                        // JSONObject item = js.getJSONObject(i);
                        MembreFamille membre = new MembreFamille();
                        membre.setNom(js.getJSONObject(i).getString("nom"));
                        membre.setPrenom(js.getJSONObject(i).getString("prenom"));
                        membre.setEmail(js.getJSONObject(i).getString("email"));
                        membre.setLien(js.getJSONObject(i).getString("lien"));
                        membre.setNum_tel(js.getJSONObject(i).getInt("num_tel"));
                        membre.setImage_per(js11.getString(i));

                        membres.add(membre);
                        System.out.println("Liste personnes"+membre.toString());

                    }
                    gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, membres);
                    if(membres.isEmpty()){
                        new SweetAlertDialog((getContext()), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("No persons to show yet!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        startActivity(new Intent(getContext(),Main2Activity.class));

                                    }
                                })
                                .show();}
                    gridView.setAdapter(gridAdapter);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                    gridView.setVisibility(View.VISIBLE);

                                    progressBar.setVisibility(View.INVISIBLE);

                            }
                        }, 2000);
                        System.out.println("family end loading");

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
        AppSingleton.getInstance(getContext()).addToRequestQueue(jsonObjReq, REQUEST_TAG);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_family, container, false);
/*
        mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.per);
        mGifLoadingView.show(getActivity().getFragmentManager(), "");*/

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static boolean checkInternetConnection(Context context)
    {
        try
        {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            assert conMgr != null;
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())
                return true;
            else
                return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
    private class Task extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            return new String[0];
        }

        @Override protected void onPostExecute(String[] result) {
           /* url1 = SharedData.url + "getper?malade=" + SharedData.alzMail;
            makeJsonObjectRequest(url1);*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    url1 = SharedData.url + "getper?malade=" + SharedData.alzMail;
                    makeJsonObjectRequest(url1);
                    if (membres == null) {
                        membres = new ArrayList<>();
                    }
                    mWaveSwipeRefreshLayout.setRefreshing(false);
                }
            }, 1500);
            super.onPostExecute(result);
        }
    }
}
