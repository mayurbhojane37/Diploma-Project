package com.hfad.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class Home extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlipperLayout flipperLayout = Objects.requireNonNull(getView()).findViewById(R.id.flipper);
        ImageView v1 = getView().findViewById(R.id.view1);
        ImageView v2 = getView().findViewById(R.id.view3);
        int[] imageDrawable={R.drawable.teacher,R.drawable.teaching,R.drawable.test,R.drawable.tutorial,R.drawable.analyze,R.drawable.quiz, R.drawable.scrumboard};
        String[] imageDescription={"Teacher","Teaching","Test","Tutorial","Analyze","Quiz","ScrumBoard"};
        for(int i=0;i<imageDrawable.length;i++)
        {
            FlipperView flipperView=new FlipperView(getContext());
            flipperView.setImageDrawable(imageDrawable[i]);
            flipperView.setDescription(imageDescription[i]);
            flipperLayout.addFlipperView(flipperView);
        }
    }

}

