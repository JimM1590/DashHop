package com.jmadsen.dashhop;
import android.content.*;import android.content.pm.*;import android.widget.Toast;
/** Standard Android launches only. No split actions, bounds, task removal or embedding. */
public final class AppLaunchManager {
 static boolean launch(Context c,String id){return fullscreen(c,id);}
 static boolean allowed(Context c,String id){
 if(LaunchSafety.allowed(new ShortcutRepository(c).flag("parkedOnly."+id,false),LaunchSafety.provider.state()))return true;
 Toast.makeText(c,"Parked Only — Parked state is not verified. Change this app's classification in DashHopper Settings if needed.",Toast.LENGTH_LONG).show();Diagnostics.info("parked_only_launch_blocked");return false;}
 static final class Attempt {final boolean accepted;final String reason,exception;Attempt(boolean a,String r,String e){accepted=a;reason=r;exception=e;}}
 static boolean fullscreen(Context c,String id){return attempt(c,id).accepted;}
 static Attempt attempt(Context c,String id){
 if(WorkspacePolicy.DASHBOARD.equals(id)){settings(c,"");return new Attempt(true,"Opened settings","");}
 if(id==null||id.isEmpty()||new AppCatalogRepository(c).get(id)==null){Toast.makeText(c,"App unavailable. Choose another app in Settings.",Toast.LENGTH_LONG).show();settings(c,"");return new Attempt(false,"App component unavailable","");}
 if(!allowed(c,id))return new Attempt(false,"Parked-only launch restricted","");
 try{UtilityPopupManager.dismiss();Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setComponent(ComponentName.unflattenFromString(id)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
 Diagnostics.operation("app_launch_requested",java.util.Collections.singletonMap("component",id));c.startActivity(i);Diagnostics.info("app_launch_dispatched_state_unverified");DockOverlayService.launchBoundary();return new Attempt(true,"","");
 }catch(RuntimeException e){Diagnostics.error("launch_failed",e);Toast.makeText(c,"Unable to open app",Toast.LENGTH_LONG).show();return new Attempt(false,"Android rejected normal launch",e.getClass().getSimpleName());}}
 static void home(Context c){ShortcutRepository r=new ShortcutRepository(c);String id=r.str("home","");
 if(!DockRoutingPolicy.useSystemHome(id)){fullscreen(c,id);return;}
 try{UtilityPopupManager.dismiss();Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
 ResolveInfo resolved=c.getPackageManager().resolveActivity(i,PackageManager.MATCH_DEFAULT_ONLY);
 if(resolved!=null&&resolved.activityInfo!=null&&c.getPackageName().equals(resolved.activityInfo.packageName)){Toast.makeText(c,"Choose your Home app in Android Settings",Toast.LENGTH_LONG).show();settings(c,"");return;}
 c.startActivity(i);Diagnostics.info("system_home_launch_dispatched_state_unverified");DockOverlayService.launchBoundary();
 }catch(Exception e){Diagnostics.error("home_launch_failed",e);settings(c,"");}}
 static void recovery(Context c){settings(c,"");}
 static void dashboard(Context c){settings(c,"");}
 static void settings(Context c,String mode){UtilityPopupManager.dismiss();try{c.startActivity(new Intent(c,DockSettingsActivity.class).putExtra(DockStartup.SHOW_SETTINGS,true).putExtra("mode",mode).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));}catch(Exception e){Diagnostics.error("settings_launch_failed",e);}}
}
