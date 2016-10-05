package constant;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class MyPreferences {
    private static MyPreferences preferences = null;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;
    private String flag = "flag";
    private String cardInfo = "cardInfo";
    private String isLogedIn = "isLogedIn";
    private String cabId = "cabId";
    private String Cardno = "cardno";
    private String Cvvno = "cvvno";
    private String Expyear = "expyear";
    private String Expmonth = "expmonth";
    private String Notification = "notification";
    private String DriverId = "driverId";
    private String  latitude="latitude";
    private String langtitude="langtitude";
    private String ImageUrl = "imageurl";
    private String RoleId = "roleId";


    public String getImageUrl() {
        return mPreferences.getString(this.ImageUrl, "");
        //return mPreferences.getString(this.ASMID,"");
    }

    public void setImageUrl(String ImageUrl) {
        editor = mPreferences.edit();
        editor.putString(this.ImageUrl, ImageUrl);
        //editor.putString(this.ASMID, ASMID);
        editor.commit();
    }



    public MyPreferences(Context context) {
        setmPreferences(PreferenceManager.getDefaultSharedPreferences(context));
    }


    public SharedPreferences getmPreferences() {
        return mPreferences;
    }

    public void setmPreferences(SharedPreferences mPreferences) {
        this.mPreferences = mPreferences;
    }


    public static MyPreferences getActiveInstance(Context context) {
        if (preferences == null) {
            preferences = new MyPreferences(context);
        }
        return preferences;
    }

    public boolean getIsLoggedIn() {
        return mPreferences.getBoolean(this.isLogedIn, false);
    }

    public void setIsLoggedIn(boolean isLoggedin) {
        editor = mPreferences.edit();
        editor.putBoolean(this.isLogedIn, isLoggedin);
        editor.commit();
    }
    public boolean getflag() {
        return mPreferences.getBoolean(this.flag, false);
    }

    public void setflag(boolean flag) {
        editor = mPreferences.edit();
        editor.putBoolean(this.flag, flag);
        editor.commit();
    }
    public boolean getcardInfo() {
        return mPreferences.getBoolean(this.cardInfo, false);
    }

    public void setcardInfo(boolean cardInfo) {
        editor = mPreferences.edit();
        editor.putBoolean(this.cardInfo, cardInfo);
        editor.commit();
    }
    public boolean getNotification() {
        return mPreferences.getBoolean(this.Notification, false);
    }

    public void setNotification(boolean Notification) {
        editor = mPreferences.edit();
        editor.putBoolean(this.Notification, Notification);
        editor.commit();
    }
    public String getExpmonth() {
        return mPreferences.getString(this.Expmonth, "");
        //return mPreferences.getString(this.ASMID,"");
    }

    public void setExpmonth(String Expmonth) {
        editor = mPreferences.edit();
        editor.putString(this.Expmonth, Expmonth);
        //editor.putString(this.ASMID, ASMID);
        editor.commit();
    } public String getlatitude() {
        return mPreferences.getString(this.latitude, "");
        //return mPreferences.getString(this.ASMID,"");
    }

    public void setlatitude(String latitude) {
        editor = mPreferences.edit();
        editor.putString(this.latitude, latitude);
        editor.commit();
    }
    public String getlangtitude() {
        return mPreferences.getString(this.langtitude, "");
        //return mPreferences.getString(this.ASMID,"");
    }
 public void setRoleId(String RoleId) {
        editor = mPreferences.edit();
        editor.putString(this.RoleId, RoleId);
        editor.commit();
    }
    public String getRoleId() {
        return mPreferences.getString(this.RoleId, "");

    }

    public void setlangtitude(String langtitude) {
        editor = mPreferences.edit();
        editor.putString(this.langtitude, langtitude);
        editor.commit();
    }

    public String getExpyear() {
        return mPreferences.getString(this.Expyear, "");
        //return mPreferences.getString(this.ASMID,"");
    }

    public void setExpyear(String Expyear) {
        editor = mPreferences.edit();
        editor.putString(this.Expyear, Expyear);
        //editor.putString(this.ASMID, ASMID);
        editor.commit();
    }


    public String getCvvno() {
        return mPreferences.getString(this.Cvvno, "");
        //return mPreferences.getString(this.ASMID,"");
    }

    public void setCvvno(String Cvvno) {
        editor = mPreferences.edit();
        editor.putString(this.Cvvno, Cvvno);
        //editor.putString(this.ASMID, ASMID);
        editor.commit();
    }

    public String getCardno() {
        return mPreferences.getString(this.Cardno, "");
        //return mPreferences.getString(this.ASMID,"");
    }

    public void setCardno(String Cardno) {
        editor = mPreferences.edit();
        editor.putString(this.Cardno, Cardno);
        //editor.putString(this.ASMID, ASMID);
        editor.commit();
    }

    public String getcabId() {
        return mPreferences.getString(this.cabId, "");
        //return mPreferences.getString(this.ASMID,"");
    }

    public void setcabId(String cabId) {
        editor = mPreferences.edit();
        editor.putString(this.cabId, cabId);
        //editor.putString(this.ASMID, ASMID);
        editor.commit();
    }
  public String getDriverId() {
        return mPreferences.getString(this.DriverId, "");
        //return mPreferences.getString(this.ASMID,"");
    }

    public void setDriverId(String DriverId) {
        editor = mPreferences.edit();
        editor.putString(this.DriverId, DriverId);
        //editor.putString(this.ASMID, ASMID);
        editor.commit();
    }

}
