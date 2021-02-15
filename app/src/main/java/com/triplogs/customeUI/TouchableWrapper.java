package com.triplogs.customeUI;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class TouchableWrapper extends FrameLayout {

    interface TouchLitsner {
        public void onToch();
        public void leaveToch();
    }

    TouchLitsner touchLitsner;

    public void setOnTouchListner(TouchLitsner touchLitsner) {
        this.touchLitsner = touchLitsner;
    }

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        switch (event.getAction()) {

//            case MotionEvent.AXIS_HSCROLL:
//                touchLitsner.onToch();
//                break;
//
//            case MotionEvent.ACTION_SCROLL:
//                touchLitsner.onToch();
//                break;

            case MotionEvent.ACTION_DOWN:
                //    MainActivity.mMapIsTouched = true;
                //     LogClass.e("dispatchTouchEvent","down");
                touchLitsner.onToch();
                break;

            case MotionEvent.ACTION_UP:
                //  LogClass.e("dispatchTouchEvent","up");
                //  MainActivity.mMapIsTouched = false;

                touchLitsner.leaveToch();
                break;


        }
        return super.dispatchTouchEvent(event);
    }
}