package com.example.karanc.buttler.interfaces;

import android.view.View;

public interface ClickListener {
    void onClick(View view, int pos);

    void onClick(View view);

    void onLongClick(View view, int pos);
}
