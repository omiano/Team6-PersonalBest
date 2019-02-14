package com.example.personalbest;

import android.app.Activity;
import android.content.SharedPreferences;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;


public class SaveLocal {

    private SharedPreferences exercisePreferences;
    private SharedPreferences.Editor editor;

    public SaveLocal(Activity activity){
        exercisePreferences= activity.getSharedPreferences("exercise", Context.MODE_PRIVATE);
        editor=exercisePreferences.edit();
    }

    public boolean isLastSessionActive(){
        return exercisePreferences.getBoolean("exerciseActive",false  );
    }
    public void saveStartSessionStepCount(long stepCount){
        editor.putLong("startSessionStepCount",stepCount);
        editor.apply();
    }

    public void saveHeight(int feet, int inches) {
        editor.putInt("height_feet", feet);
        editor.putInt("height_inches", inches);
        editor.apply();
    }

    public boolean containsHeight() {
        return exercisePreferences.contains("height_feet");
    }

    public void clearHeight() {
        editor.remove("height_feet");
        editor.remove("height_inches");
        editor.commit();
    }

    public long getStartSessionStepCount(){
        return exercisePreferences.getLong("startSessionStepCount",0);
    }

    public void setStartSession(Calendar calendar){
        editor.putBoolean("exerciseActive", true);
        editor.putLong("sessionStartTime",calendar.getTimeInMillis());
        editor.apply();
    }
    public void setStopSession(){
        editor.putBoolean("exerciseActive", false);
        editor.apply();
    }
    public long getLastSessionStartTime(){
        return exercisePreferences.getLong("sessionStartTime",0);
    }


    //Sets the background step count of the day (dayBefore) before current day
    public void setBackgroundStepCount(long stepCount, int daysBefore){
        editor.putLong(""+daysBefore+"DaysBeforeBackgroundStepCount",stepCount);
        editor.apply();
    }
    //Returns the background step count of the day (dayBefore) before current day
    public long getBackgroundStepCount(int daysBefore){
        return exercisePreferences.getLong(""+daysBefore+"DaysBeforeBackgroundStepCount",0);
    }
    //Sets the exercise step count of the day (dayBefore) before current day
    public void setExerciseStepCount(long stepCount, int daysBefore){
        editor.putLong(""+daysBefore+"DaysBeforeExerciseStepCount",stepCount);
        editor.apply();
    }
    //Returns the exercise step count of the day (dayBefore) before current day
    public long getExerciseStepCount(int daysBefore){
        return exercisePreferences.getLong(""+daysBefore+"DaysBeforeExerciseStepCount",0);
    }
    //Method that shifts the last 7 days data when a new day begins
    public void newDayShift(){
        for(int i=7; i>0;i--){
            Log.d("Save Local", "Shifting " + i + " and " + (i - 1));
            setExerciseStepCount(getExerciseStepCount(i-1),i);
            setBackgroundStepCount(getBackgroundStepCount(i-1),i);
        }
        setExerciseStepCount(0,0);
        setBackgroundStepCount(0,0);

    }
    public void setCurrSubGoal(int subGoal){
        editor.putInt("currsubGoal", subGoal);
        editor.apply();

    }
    public void setOldSubGoal(int old){
        editor.putInt("oldSubGoal", old);
        editor.apply();
    }
    public void setSpeed(long speed){
        editor.putLong("speed", speed);
        editor.apply();
    }
    public void setSteps(int steps){
        editor.putInt("steps", steps);
        editor.apply();
    }
    public void setTime(long time){
        editor.putLong("time", time);
        editor.apply();
    }
    public int getCurrSubGoal(){
        return exercisePreferences.getInt("currsubGoal", 500);

    }
    public int getOldSubGoal(){
        return exercisePreferences.getInt("oldSubGoal", 0);
    }
    public long getSpeed(){
        return exercisePreferences.getLong("speed", 0);
    }
    public int getSteps(){
        return exercisePreferences.getInt("steps", 0);
    }
    public long getTime(long time){
        return exercisePreferences.getLong("time", 0);
    }
    public void setGoal(int goal) {
        editor.putInt("goal", goal);
        editor.apply();
        setAchieved(false);
    }
    public int getGoal(){
        return exercisePreferences.getInt("goal", 500);
    }

    public  boolean isAchieved(){
        return exercisePreferences.getBoolean("goalAchieved", false);
    }

    public void setAchieved(boolean isAchieved){
        editor.putBoolean("goalAchieved", isAchieved);
        editor.apply();
    }


}
