package com.jmadsen.dashhop;
import android.app.Activity;import android.content.Intent;import android.provider.Settings;
final class DockStartup {
 static final String SHOW_SETTINGS="dashhopper.show_settings";
 static boolean explicit(Intent i){return i!=null&&(i.getBooleanExtra(SHOW_SETTINGS,false)||(i.getAction()==null&&i.hasExtra("mode")));}
 static DockStartupPolicy.Action decide(Activity c,Intent i){ShortcutRepository r=new ShortcutRepository(c);boolean initialized=r.flag("setupSeen",false)||r.prefs.contains("overlayEnabled")||r.prefs.contains("apps");boolean running=DockOverlayService.running();boolean recoveryNeeded=!running&&!r.str("error","").isEmpty();return DockStartupPolicy.decide(explicit(i),Settings.canDrawOverlays(c),initialized&&!recoveryNeeded,running);}
 static void activate(Activity c,DockStartupPolicy.Action action){
  Diagnostics.operation("dock_app_activation",java.util.Collections.singletonMap("startup_decision",action.name()));
  if(action==DockStartupPolicy.Action.START_DOCK)DockController.start(c);
  else if(action==DockStartupPolicy.Action.KEEP_RUNNING)Diagnostics.info("dock_activation_already_running_no_restart");
 }
}
