package com.sourav.dogesan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;

public class MyVideoMotionLayout extends MotionLayout {
    public MyVideoMotionLayout(@NonNull Context context) {
        super(context);
    }

    public MyVideoMotionLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoMotionLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
       return super.onInterceptTouchEvent(event);
//        onTouchEvent(event);
//        return false;

//        if (onTouchEvent(event)) {
//            return false;
//        } else {
//            return true;
//        }
   }
}
