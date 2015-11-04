package com.brs.utils;

/**
 * Created by ikban on 2015-10-27.
 */
public class Util {

    /* db 와 리스트에 출력시 표시할 날짜형태로 변환하기 위한 유틸리티 함수. */
    public static String convertDateWithDash(int year, int month, int date)
    {
        if(month < 10) {
            if(date < 10)
                return year + "-0" + month + "-0" + date;
            else
                return year + "-0" + month + "-" + date;
        }
        else{
            if(date < 10)
                return year + "-" + month + "-0" + date;
            else
                return year + "-" + month + "-" + date;
        }
    }

    public static int convertDateForOrderColumn(int year, int month, int date)
    {
        String tmpStr;
        int retValue = 0;

        if(month < 10) {
            if(date < 10)
                tmpStr =  year + "0" + month + "0" + date;
            else
                tmpStr =  year + "0" + month + "" + date;
        }
        else{
            if(date < 10)
                tmpStr =  year + "" + month + "0" + date;
            else
                tmpStr =  year + "" + month + "" + date;
        }

        retValue = Integer.parseInt(tmpStr);
        DM.i("convertDateForOrderColumn() / tmpStr:" + tmpStr + " / retValue:" + retValue);

        return retValue;
    }

    public static int convertDateToDateOrder(String date){
        int date_order;
        String tmpStr;

        tmpStr = date.replace("-", "");
        date_order = Integer.parseInt(tmpStr);

        DM.i("converterDateToDateOrder() / tmpStr:" + tmpStr + " / date_order:" + date_order);

        return date_order;
    }

    /* 소숫점 자리 표시하기 위한 유틸리티 함수 */
    //update 1: DB에 null 값을 허용하고 있어, 초기 로딩시 사용하는 경우 예외 발생하는 경우 있어 처리함.
    public static String addFloatingPoint(String strNum){
        String ret = "";
        try {
            float tmp = 0.0f;

            if (strNum.isEmpty() || strNum.equals("") || strNum == null) {
                tmp = 0.00f;
            } else {
                tmp = Float.parseFloat(strNum);
            }

            if(tmp < 10)
                ret = String.format("0%.2f", tmp);
            else
                ret = String.format("%.2f", tmp);

            DM.i("addFloatingPoint() - ret:" + ret);
        }catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}
