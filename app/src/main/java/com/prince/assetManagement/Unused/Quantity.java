package com.prince.assetManagement.Unused;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.prince.assetManagement.R;


public class Quantity extends Fragment {
    Button next;
    EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quantity, container, false);
        editText = view.findViewById(R.id.quantity);
        next = view.findViewById(R.id.next);
        String quan = editText.getText().toString();

        int quantity = Integer.parseInt(quan);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Bill();

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_area, fragment);
                ft.commit();
            }
        });
        return view;
    }
}
