package com.jmadsen.dashhop;
import android.app.*;import android.content.*;import android.os.*;import android.provider.Settings;
/** System boot/update entry point. Never starts Settings over another application. */
public final class DockRecoveryReceiver extends BroadcastReceiver {
 public void onReceive(Context c,Intent i){String action=i==null?null:i.getAction();if(!Intent.ACTION_BOOT_COMPLETED.equals(action)&&!Intent.ACTION_MY_PACKAGE_REPLACED.equals(action))return;
  UserManager users=c.getSystemService(UserManager.class);boolean unlocked=users==null||users.isUserUnlocked();
  if(!unlocked){Diagnostics.info("dock_recovery_waiting_for_unlock");return;}
  ShortcutRepository r=new ShortcutRepository(c);DockRecoveryPolicy.Action decision=DockRecoveryPolicy.decide(r.flag("overlayEnabled",false),r.flag("restoreDockOnBoot",true),unlocked,Settings.canDrawOverlays(c));
  Diagnostics.operation("dock_recovery_broadcast",java.util.Collections.singletonMap("dock_recovery_action",action+":"+decision.name()));
  if(decision==DockRecoveryPolicy.Action.START_DOCK){try{if(!DockOverlayService.running())c.startForegroundService(new Intent(c,DockOverlayService.class));Diagnostics.info("dock_recovery_start_dispatched_unverified");}catch(RuntimeException e){Diagnostics.error("dock_recovery_start_rejected",e);r.set("error","Android blocked dock recovery. Open DashHopper and enable the dock.");notifyRecovery(c);}}
  else if(decision==DockRecoveryPolicy.Action.PERMISSION_REQUIRED){r.set("error","Display-over-apps permission is needed. Open DashHopper to recover the dock.");notifyRecovery(c);}
 }
 static void notifyRecovery(Context c){try{NotificationManager nm=c.getSystemService(NotificationManager.class);nm.createNotificationChannel(new NotificationChannel("dock_recovery","DashHopper recovery",NotificationManager.IMPORTANCE_DEFAULT));PendingIntent settings=PendingIntent.getActivity(c,201,new Intent(c,DockSettingsActivity.class).putExtra(DockStartup.SHOW_SETTINGS,true),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);nm.notify(201,new Notification.Builder(c,"dock_recovery").setSmallIcon(R.drawable.ic_dashhop).setContentTitle("DashHopper needs attention").setContentText("Open settings to recover your dock").setContentIntent(settings).setAutoCancel(true).build());}catch(RuntimeException e){Diagnostics.error("dock_recovery_notice_unavailable",e);}}
}
