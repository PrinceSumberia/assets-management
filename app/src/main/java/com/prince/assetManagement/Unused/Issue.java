package com.prince.assetManagement.Unused;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prince.assetManagement.R;

import static com.prince.assetManagement.Unused.Geotag.TAG;


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
