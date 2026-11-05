package com.jmadsen.dashhop;
import android.content.*;
import android.util.DisplayMetrics;
import org.json.*;
import java.util.*;
public final class ShortcutRepository {
 final SharedPreferences prefs;
 ShortcutRepository(Context c){prefs=c.getSharedPreferences("dock",0);migrate(c);}
 private void migrate(Context c){if(prefs.getInt("schema",0)>=2)return;
  DisplayMetrics d=c.getResources().getDisplayMetrics();float density=Math.max(.5f,d.density);String edge=str("edge","bottom");
  boolean old=prefs.contains("apps")||prefs.contains("home")||prefs.contains("horizontalPx");
  SharedPreferences.Editor e=prefs.edit().putInt("schema",2).putString("preset","two").putBoolean("mainRight",false);
  if(!prefs.contains("home")||str("home","").isEmpty())e.putString("home",WorkspacePolicy.DASHBOARD);else if(old)e.putBoolean("homeChoicePending",true);
  e.putInt("horizontalDp",Math.max(88,Math.round(num("horizontalPx",90)/density))).putInt("verticalDp",Math.max(120,Math.round(num("verticalPx",160)/density)));
  if(prefs.contains("handle")){int available=(WorkspacePolicy.vertical(edge)?d.heightPixels:d.widthPixels)-Math.round(96*density);e.putFloat("handle."+edge,WorkspacePolicy.handleFraction(num("handle",0),available));}
  e.apply();
 }
 String str(String k,String d){return prefs.getString(k,d);}
 int num(String k,int d){return prefs.getInt(k,d);}
 boolean flag(String k,boolean d){return prefs.getBoolean(k,d);}
 float fraction(String edge){return WorkspacePolicy.fraction(prefs.getFloat("handle."+edge,.5f));}
 void fraction(String edge,float f){prefs.edit().putFloat("handle."+edge,WorkspacePolicy.fraction(f)).apply();}
 void set(String k,String v){prefs.edit().putString(k,v).apply();}
 void set(String k,int v){prefs.edit().putInt(k,v).apply();}
 void set(String k,boolean v){prefs.edit().putBoolean(k,v).apply();}
 String preset(){return str("preset","two");}
 String panel(int i){return str(WorkspacePolicy.panelKey(preset(),i),"empty");}
 void panel(int i,String value){set(WorkspacePolicy.panelKey(preset(),i),value);}
 List<String> shortcuts(){List<String> a=new ArrayList<>();try{JSONArray j=new JSONArray(str("apps","[]"));for(int i=0;i<j.length();i++)a.add(j.getString(i));}catch(JSONException ignored){}return a;}
 void save(List<String> a){prefs.edit().putString("apps",new JSONArray(a).toString()).apply();}
 boolean add(String id){List<String>a=shortcuts();if(a.contains(id))return false;a.add(id);save(a);return true;}
 void reset(){prefs.edit().clear().putInt("schema",2).putString("home",WorkspacePolicy.DASHBOARD).apply();}
}
