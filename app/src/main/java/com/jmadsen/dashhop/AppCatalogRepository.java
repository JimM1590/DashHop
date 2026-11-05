package com.jmadsen.dashhop;
import android.content.*;
import android.content.pm.*;
import android.graphics.drawable.Drawable;
import java.util.*;
public final class AppCatalogRepository {
 static final class App {final String id,label;final Drawable icon; App(String i,String l,Drawable d){id=i;label=l;icon=d;} }
 final Context c; final PackageManager pm;
 AppCatalogRepository(Context c){this.c=c;pm=c.getPackageManager();}
 List<App> all(){List<App>a=new ArrayList<>();Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
 for(ResolveInfo r:pm.queryIntentActivities(i,0)){ActivityInfo x=r.activityInfo;if(!x.exported||!x.enabled||x.packageName.equals(c.getPackageName()))continue;
 a.add(new App(new ComponentName(x.packageName,x.name).flattenToString(),r.loadLabel(pm).toString(),r.loadIcon(pm)));}
 a.sort((x,y)->x.label.compareToIgnoreCase(y.label));return a;}
 List<App> hvacApps(){
  Map<String,App> packages=new TreeMap<>();for(App a:all()){String pkg=HvacSelectionPolicy.packageName(a.id);if(!packages.containsKey(pkg))packages.put(pkg,new App(HvacSelectionPolicy.identity(pkg),a.label,a.icon));}
  for(ResolveInfo match:pm.queryIntentActivities(new Intent(HvacIntegration.ACTION),PackageManager.MATCH_DEFAULT_ONLY)){
   ActivityInfo a=match.activityInfo;if(a==null||!a.exported||!a.enabled||a.applicationInfo==null||!a.applicationInfo.enabled||a.packageName.equals(c.getPackageName())||packages.containsKey(a.packageName))continue;
   packages.put(a.packageName,new App(HvacSelectionPolicy.identity(a.packageName),a.applicationInfo.loadLabel(pm).toString()+" (quick controls)",a.applicationInfo.loadIcon(pm)));
  }
  List<App> result=new ArrayList<>(packages.values());result.sort((a,b)->a.label.compareToIgnoreCase(b.label));return result;
 }
 App get(String id){try{ComponentName n=ComponentName.unflattenFromString(id);if(n==null)return null;ActivityInfo a=pm.getActivityInfo(n,0);if(!a.enabled||!a.exported||!a.applicationInfo.enabled)return null;return new App(id,a.loadLabel(pm).toString(),a.loadIcon(pm));}catch(Exception e){return null;}}
}
