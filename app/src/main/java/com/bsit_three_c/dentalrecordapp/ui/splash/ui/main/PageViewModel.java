package com.bsit_three_c.dentalrecordapp.ui.splash.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

//    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
//    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
//        @Override
//        public String apply(Integer input) {
//            return "Hello world from section: " + input;
//        }
//    });

    private final MutableLiveData<Integer> mDrawable = new MutableLiveData<>();
    private final MutableLiveData<String> mText = new MutableLiveData<>();
    private final MutableLiveData<Integer> mRadio = new MutableLiveData<>();

//    public void setIndex(int index) {
//        mIndex.setValue(index);
//    }

    public void setmDrawable(int drawable) {
        mDrawable.setValue(drawable);
    }

    public LiveData<Integer> getmDrawable() {
        return mDrawable;
    }

    public void setmText(String txt) {
        mText.setValue(txt);
    }

    public LiveData<String> getmText() {
        return mText;
    }

    public void setmRadio(int radio) {
        mRadio.setValue(radio);
    }

    public LiveData<Integer> getmRadio() {
        return mRadio;
    }
}