package com.triplogs.helper;

public class AverageAngle {
    private double[] mValues;
    private int mCurrentIndex;
    private int mNumberOfFrames;
    private boolean mIsFull;
    private double mAverageValue = Double.NaN;


    public AverageAngle(int frames,ChangeAvgListener changeAvgListener) {
        this.mNumberOfFrames = frames;
        this.mCurrentIndex = 0;
        this.mValues = new double[frames];
        this.changeAvgListener = changeAvgListener;
    }

    public AverageAngle(ChangeAvgListener changeAvgListener) {
        this.changeAvgListener = changeAvgListener;
    }

        public  interface ChangeAvgListener {

        void onBearingChanged(double bearing);
    }

    ChangeAvgListener changeAvgListener;



    public void putValue(double d) {
        mValues[mCurrentIndex] = d;
        if (mCurrentIndex == mNumberOfFrames - 1) {
            mCurrentIndex = 0;
            mIsFull = true;
        } else {
            mCurrentIndex++;
        }
        updateAverageValue();
    }

    public double getAverage() {
        return this.mAverageValue;
    }

    private void updateAverageValue() {
        int numberOfElementsToConsider = mNumberOfFrames;
        if (!mIsFull) {
            numberOfElementsToConsider = mCurrentIndex + 1;
        }

        if (numberOfElementsToConsider == 1) {
            this.mAverageValue = mValues[0];
            return;
        }


        double sumSin = 0.0;
        double sumCos = 0.0;
        for (int i = 0; i < numberOfElementsToConsider; i++) {
            double v = mValues[i];
            sumSin += Math.sin(v);
            sumCos += Math.cos(v);
        }
        this.mAverageValue = Math.atan2(sumSin, sumCos);
       // LogClass.e("changeAvgListener","changeAvgListener: "+mAverageValue);
        if(changeAvgListener!=null){
            changeAvgListener.onBearingChanged(mAverageValue);
        }
    }



}
