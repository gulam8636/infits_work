package com.example.infits.customDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

    public class StepCounterViewModel extends ViewModel {
        private MutableLiveData<Integer> stepCountLiveData = new MutableLiveData<>();

        public MutableLiveData<Integer> getStepCountLiveData() {
            return stepCountLiveData;
        }

        public void setStepCount(int stepCount) {
            stepCountLiveData.setValue(stepCount);
        }
    }


