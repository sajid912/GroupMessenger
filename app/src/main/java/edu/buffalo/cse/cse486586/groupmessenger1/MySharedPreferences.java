package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sajidkhan on 2/14/18.
 */

public class MySharedPreferences {

    public static final String SEQUENCE_NUMBER = "sequenceNumber";
    Context mContext;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    public MySharedPreferences(Context context) {
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = sharedPreferences.edit();
    }

    protected int getSequenceNumber() {
        return sharedPreferences.getInt(SEQUENCE_NUMBER, 0);
    }

    protected void setSequenceNumber(int sequenceNumber) {
        editor.putInt(SEQUENCE_NUMBER, sequenceNumber);
        editor.commit();
    }

    protected void clearPreferences() {
        editor.clear();
        editor.commit();
    }
}
