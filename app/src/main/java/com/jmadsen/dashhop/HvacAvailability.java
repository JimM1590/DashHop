package com.jmadsen.dashhop;
import android.content.*;import android.content.pm.*;
/** Cached software availability. Never launches an app and never changes the protected HVAC path. */
final class HvacAvailability implements SharedPreferences.OnSharedPreferenceChangeListener {
 static HvacAvailability instance;
 static HvacAvailability get(Context c){if(instance==null)instance=new HvacAvailability(c);return instance;}
 static final class State {final String selectedPackage,reason;final boolean available;State(String p,boolean a,String r){selectedPackage=p;available=a;reason=r;}}
 final Context c;final ShortcutRepository repo;State state=new State("",false,"No HVAC provider selected.");
 HvacAvailability(Context c){this.c=c.getApplicationContext();repo=new ShortcutRepository(this.c);repo.prefs.registerOnSharedPreferenceChangeListener(this);refresh("initial");}
 State state(){return state;}
 void packageChanged(String pkg){String selected=HvacSelectionPolicy.packageName(repo.str("hvacApp",""));if(!selected.isEmpty()&&selected.equals(pkg))refresh("selected_package_event");}
 public void onSharedPreferenceChanged(SharedPreferences p,String key){if(key==null||key.equals("hvacApp"))refresh("selection_changed");}
 void refresh(String reason){String stored=repo.str("hvacApp","");String pkg=HvacSelectionPolicy.packageName(stored);boolean available=false;String detail="No HVAC provider selected. Choose an app below.";
  if(!pkg.isEmpty())try{
   PackageManager pm=c.getPackageManager();ApplicationInfo app=pm.getApplicationInfo(pkg,0);
   if(!app.enabled)detail="Selected HVAC app is disabled. Its shortcut is hidden; the selection is saved.";
   else{HvacIntegration.Capability cap=HvacIntegration.capability(c,stored);
    if(cap.endpoint!=null){available=true;detail="Quick-control provider available.";}
    else{String normal=HvacIntegration.normalComponent(c,stored,pkg);
     if(!normal.isEmpty()){ActivityInfo activity=pm.getActivityInfo(ComponentName.unflattenFromString(normal),0);available=activity.enabled&&activity.exported&&activity.applicationInfo!=null&&activity.applicationInfo.enabled&&(activity.permission==null||activity.permission.isEmpty()||pm.checkPermission(activity.permission,c.getPackageName())==PackageManager.PERMISSION_GRANTED);}
     detail=available?"Normal HVAC app is available as fallback; no accessible quick-control Activity was found.":"No launchable quick controls or normal app are available. The shortcut is hidden; the selection is saved.";
    }
   }
  }catch(PackageManager.NameNotFoundException e){detail="Selected HVAC app is not installed or its component is unavailable. The shortcut is hidden; the selection is saved.";}
   catch(RuntimeException e){detail="HVAC availability could not be checked. Choose/review the provider below.";Diagnostics.error("hvac_availability_failed",e);}
  state=new State(pkg,available,detail);java.util.Map<String,Object> fields=new java.util.LinkedHashMap<>();fields.put("hvac_provider_available",available);fields.put("hvac_provider_package",pkg);fields.put("hvac_revalidation_reason",reason);Diagnostics.operation("hvac_provider_revalidated",fields);DockOverlayService.refresh();
 }
}
