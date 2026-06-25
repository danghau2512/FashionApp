package com.example.fashionshopmobile.activity;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private boolean firstCall = true;
    private final Runnable action;

    public SimpleItemSelectedListener(Runnable action) {
        this.action = action;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (firstCall) {
            firstCall = false;
            return;
        }
        action.run();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
