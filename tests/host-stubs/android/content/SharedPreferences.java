package android.content;
public interface SharedPreferences {
 String getString(String k,String d);int getInt(String k,int d);boolean getBoolean(String k,boolean d);float getFloat(String k,float d);long getLong(String k,long d);boolean contains(String k);Editor edit();
 interface Editor {Editor putString(String k,String v);Editor putInt(String k,int v);Editor putBoolean(String k,boolean v);Editor putFloat(String k,float v);Editor putLong(String k,long v);Editor clear();void apply();boolean commit();}
}