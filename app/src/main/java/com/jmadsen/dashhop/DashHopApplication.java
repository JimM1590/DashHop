package com.jmadsen.dashhop;
import android.app.*;import android.os.*;import java.util.*;
/** Silent entry Activities never suppress or redraw the service-owned dock. */
public final class DashHopApplication extends Application implements Application.ActivityLifecycleCallbacks {
 static boolean ownVisible=false,permissionFlow=false;private final Set<Activity> settings=Collections.newSetFromMap(new IdentityHashMap<Activity,Boolean>());private final Handler h=new Handler(Looper.getMainLooper());
 public void onCreate(){super.onCreate();Diagnostics.init(this);ProcessExitDiagnostics.record(this);registerActivityLifecycleCallbacks(this);new ShortcutRepository(this);}
 public void onActivityStarted(Activity a){if(a instanceof DockSettingsActivity&&((DockSettingsActivity)a).isShowingSettings()){settings.add(a);ownVisible=true;permissionFlow=false;DockOverlayService.refresh();}}
 public void onActivityStopped(Activity a){Diagnostics.operation("activity_stopped",Collections.singletonMap("activity_component",a.getClass().getSimpleName()));if(settings.remove(a))h.postDelayed(()->{ownVisible=!settings.isEmpty();DockOverlayService.refresh();},150);}
 public void onActivityResumed(Activity a){if(settings.contains(a)){permissionFlow=false;DockOverlayService.refresh();}}
 public void onActivityDestroyed(Activity a){if(settings.remove(a)){ownVisible=!settings.isEmpty();DockOverlayService.refresh();}}
 public void onActivityCreated(Activity a,Bundle s){}public void onActivityPaused(Activity a){}public void onActivitySaveInstanceState(Activity a,Bundle s){}
}
