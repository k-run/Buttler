package com.example.karanc.buttler.appUtils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.karanc.buttler.interfaces.ClickListener;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private com.example.karanc.buttler.interfaces.ClickListener ClickListener;
    private GestureDetector gestureDetector;

    public RecyclerTouchListener(RecyclerView recyclerView, Context context, ClickListener ClickListener) {
        this.ClickListener = ClickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null & ClickListener != null) {
                    ClickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                }
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && ClickListener != null && gestureDetector.onTouchEvent(e)) {
            ClickListener.onClick(child, rv.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
