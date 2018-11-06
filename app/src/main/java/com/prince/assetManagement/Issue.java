package com.prince.assetManagement;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static com.prince.assetManagement.Geotag.TAG;


public class Issue extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView quantity;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_issue, container, false);

//        quantity  =  view.findViewById(R.id.quantity);
        try {
            String numberQuantity = getArguments().getString("number");
            Log.e(TAG, "onCreateView: " + numberQuantity );
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(getContext(), "The Number of assets" + numberQuantity, Toast.LENGTH_SHORT).show();
//        quantity.setText(numberQuantity);
        return view;
    }
}
