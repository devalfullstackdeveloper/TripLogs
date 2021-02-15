package com.triplogs.customeUI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;


public class MySupportMapFragment extends SupportMapFragment {
    public View mOriginalContentView;
    public TouchableWrapper mTouchView;

    public interface TouchtMapLitsner {
        public void onToch();

        public void leaveToch();
    }

    TouchtMapLitsner touchLitsner;

    public void setOnMapTouchListner(TouchtMapLitsner touchLitsner) {
        this.touchLitsner = touchLitsner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.setOnTouchListner(new TouchableWrapper.TouchLitsner() {
            @Override
            public void onToch() {
                if (touchLitsner != null) {
                    touchLitsner.onToch();
                }

            }

            @Override
            public void leaveToch() {
                if (touchLitsner != null) {
                    touchLitsner.leaveToch();
                }
            }
        });
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }
}
