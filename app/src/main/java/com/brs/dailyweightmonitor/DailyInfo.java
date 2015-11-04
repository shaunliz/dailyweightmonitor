package com.brs.dailyweightmonitor;

/**
 * Created by ikban on 2015-10-26.
 */
public class DailyInfo {

    private String strDate;
    private String strWeight;
    private boolean bExercise;
    private boolean bDrink;
    private String strMemo;

    public DailyInfo(){
        strDate = "";
        strWeight = "";
        bExercise = false;
        bDrink = false;
        strMemo = "";
    }

    public DailyInfo(String _date, String _weight, boolean _exercise, boolean _drink, String _memo){
        strDate = _date;
        strWeight = _weight;
        bExercise = _exercise;
        bDrink = _drink;
        strMemo = _memo;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getStrWeight() {
        return strWeight;
    }

    public void setStrWeight(String strWeight) {
        this.strWeight = strWeight;
    }

    public boolean isbExercise() {
        return bExercise;
    }

    public void setbExercise(boolean bExercise) {
        this.bExercise = bExercise;
    }

    public boolean isbDrink() {
        return bDrink;
    }

    public void setbDrink(boolean bDrink) {
        this.bDrink = bDrink;
    }

    public String getStrMemo() {
        return strMemo;
    }

    public void setStrMemo(String strMemo) {
        this.strMemo = strMemo;
    }
}
