package com.hfad.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutUs_Fragment extends Fragment {
    private TextView prevText,descText;
    private ImageView plus, minus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_about_us, container, false);
        prevText = rootView.findViewById(R.id.prevText);
        descText = rootView.findViewById(R.id.descText);
        plus =  rootView.findViewById(R.id.plus);
        minus =rootView.findViewById(R.id.minus);

        plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                prevText.setVisibility(View.INVISIBLE);
                plus.setVisibility(View.INVISIBLE);
                descText.setVisibility(View.VISIBLE);
                minus.setVisibility(View.VISIBLE);
                descText.setMaxLines(Integer.MAX_VALUE);

            }
        });

        minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                descText.setVisibility(View.INVISIBLE);
                minus.setVisibility(View.INVISIBLE);
                prevText.setVisibility(View.VISIBLE);
                plus.setVisibility(View.VISIBLE);
                descText.setMaxLines(5);

            }
        });
        return rootView;

    }

}
