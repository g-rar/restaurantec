package com.example.restauranttec.ui.main;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            Log.i("on"," 2");
            return "Hello world from section: " + input;
        }
    });

    public void setIndex(int index) {
        Log.i("on"," 3");
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}
