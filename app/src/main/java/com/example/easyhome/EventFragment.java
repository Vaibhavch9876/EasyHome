package com.example.easyhome;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class EventFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager eventFragmentManager;
    private FragmentTransaction eventFragmentTransaction;


    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        final BottomNavigationView mBottomNavigationView = view.findViewById(R.id.event_bottom_navigation_view);

        eventFragmentManager = getChildFragmentManager();
        eventFragmentTransaction = eventFragmentManager.beginTransaction();
        eventFragmentTransaction.add(R.id.event_fragment_container, new AllEventsFragment());
        eventFragmentTransaction.commit();


        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.all_events:
                        mBottomNavigationView.getMenu().getItem(0).setChecked(true);
                        eventFragmentManager = getChildFragmentManager();
                        eventFragmentTransaction = eventFragmentManager.beginTransaction();
                        eventFragmentTransaction.replace(R.id.event_fragment_container, new AllEventsFragment());
                        eventFragmentTransaction.commit();
                        break;
                    case R.id.add_events:
                        mBottomNavigationView.getMenu().getItem(1).setChecked(true);
                        eventFragmentManager = getChildFragmentManager();
                        eventFragmentTransaction = eventFragmentManager.beginTransaction();
                        eventFragmentTransaction.replace(R.id.event_fragment_container, new AddEventFragment());
                        eventFragmentTransaction.commit();
                        break;
                    case R.id.clear_all_events:
                        new AlertDialog.Builder(getContext())
                                .setTitle("Warning ! ")
                                .setMessage("Are you sure you want to delete all your events..")
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Proceed", null)
                                .show();
                        break;
                }
                return false;
            }
        });




        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}
