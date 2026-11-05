package com.jmadsen.dashhop;
import android.app.*;
import android.content.*;
import android.provider.Settings;
import android.widget.Toast;
final class DockController {
 static void start(Activity c){if(!Settings.canDrawOverlays(c)){Toast.makeText(c,"Allow display over other apps first",Toast.LENGTH_LONG).show();return;}new ShortcutRepository(c).set("overlayEnabled",true);requestStart(c);}
 static void requestStart(Activity c){if(DockOverlayService.running()){Diagnostics.info("dock_start_already_running_no_restart");return;}try{c.startForegroundService(new Intent(c,DockOverlayService.class));}catch(RuntimeException e){Diagnostics.error("dock_start_rejected",e);new ShortcutRepository(c).set("error","Android blocked dock startup. Review permissions and try Enable dock again.");Toast.makeText(c,"Android blocked dock startup. Review permissions and try again.",Toast.LENGTH_LONG).show();DockRecoveryReceiver.notifyRecovery(c);}}
 static void ensure(Activity c){if(new ShortcutRepository(c).flag("overlayEnabled",false)&&Settings.canDrawOverlays(c))requestStart(c);}
 static void stop(Context c){new ShortcutRepository(c).set("overlayEnabled",false);c.stopService(new Intent(c,DockOverlayService.class));}
 static int px(Context c,int dp){return Math.round(dp*c.getResources().getDisplayMetrics().density);}
 static int thickness(Context c,ShortcutRepository r,boolean vertical,int cross){int preferred=px(c,r.num(vertical?"verticalDp":"horizontalDp",vertical?144:96));return Math.max(px(c,vertical?104:72),Math.min(preferred,Math.max(1,cross/3)));}
}
