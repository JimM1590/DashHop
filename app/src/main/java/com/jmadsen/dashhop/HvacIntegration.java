package com.jmadsen.dashhop;
import android.content.*;import android.content.pm.*;import android.widget.Toast;import java.util.*;
/** The dock and diagnostic button both call open(); no tester/provider-specific branches. */
final class HvacIntegration {
 static final String ACTION="org.dashhop.intent.action.HVAC_QUICK_CONTROL";
 static final class Capability {final ComponentName endpoint;final String reason,exception;Capability(ComponentName e,String r,String x){endpoint=e;reason=r;exception=x;}}
 static Capability capability(Context c,String selected){
  String pkg=HvacSelectionPolicy.packageName(selected);if(pkg.isEmpty())return new Capability(null,"Choose an HVAC app first.","");
  try{
   PackageManager pm=c.getPackageManager();List<HvacEndpointPolicy.Candidate> candidates=new ArrayList<>();
   for(ResolveInfo match:pm.queryIntentActivities(new Intent(ACTION).setPackage(pkg),PackageManager.MATCH_DEFAULT_ONLY)){
    ActivityInfo a=match.activityInfo;if(a==null)continue;
    boolean permission=a.permission==null||a.permission.isEmpty()||pm.checkPermission(a.permission,c.getPackageName())==PackageManager.PERMISSION_GRANTED;
    candidates.add(new HvacEndpointPolicy.Candidate(a.packageName,new ComponentName(a.packageName,a.name).flattenToString(),a.exported,a.enabled,a.applicationInfo!=null&&a.applicationInfo.enabled,permission));
   }
   String chosen=HvacEndpointPolicy.choose(pkg,candidates);
   return chosen==null?new Capability(null,"No exported, enabled, permission-accessible quick Activity found.",""):new Capability(ComponentName.unflattenFromString(chosen),"Public quick-control Activity found; display unverified.","");
  }catch(RuntimeException e){Diagnostics.error("hvac_quick_discovery_failed",e);return new Capability(null,"Discovery failed; normal launch will be attempted.",e.getClass().getSimpleName());}
 }
 static String supportDescription(Context c){return capability(c,new ShortcutRepository(c).str("hvacApp","")).reason;}
 static String selectionLabel(Context c,String selected){String pkg=HvacSelectionPolicy.packageName(selected);try{return c.getPackageManager().getApplicationInfo(pkg,0).loadLabel(c.getPackageManager())+"\n"+pkg;}catch(Exception e){return "Not assigned / unavailable";}}
 static String normalComponent(Context c,String stored,String pkg){
  // Preserve a selected legacy launcher component, but never treat a quick-only Activity as normal Home.
  for(ResolveInfo match:c.getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(pkg),0)){
   ActivityInfo a=match.activityInfo;if(a!=null&&a.exported&&a.enabled&&a.applicationInfo.enabled){String id=new ComponentName(a.packageName,a.name).flattenToString();if(id.equals(stored))return id;}
  }
  Intent normal=c.getPackageManager().getLaunchIntentForPackage(pkg);ComponentName n=normal==null?null:normal.getComponent();
  return n!=null&&pkg.equals(n.getPackageName())&&new AppCatalogRepository(c).get(n.flattenToString())!=null?n.flattenToString():"";
 }
 static HvacLaunchReport save(Context c,HvacLaunchReport report){try{new ShortcutRepository(c).set("hvacLastReport",report.text());}catch(RuntimeException e){Diagnostics.error("hvac_report_save_failed",e);}Diagnostics.operation("hvac_launch_report",report.fields());return report;}
 static HvacLaunchReport open(Context c){
  String stored=new ShortcutRepository(c).str("hvacApp","");HvacLaunchReport report=new HvacLaunchReport();report.selectedPackage=HvacSelectionPolicy.packageName(stored);
  Diagnostics.operation("hvac_open_requested",report.fields());
  try{ApplicationInfo app=c.getPackageManager().getApplicationInfo(report.selectedPackage,0);if(!app.enabled)throw new PackageManager.NameNotFoundException();}
  catch(PackageManager.NameNotFoundException|RuntimeException e){report.discoveryException=e.getClass().getSimpleName();report.result="SELECTION_REQUIRED";report.fallback="Selected package is missing, disabled or not assigned.";save(c,report);Toast.makeText(c,"Choose an installed HVAC app",Toast.LENGTH_LONG).show();AppLaunchManager.settings(c,"hvac");return report;}
  if(!AppLaunchManager.allowed(c,stored)){report.result="LAUNCH_RESTRICTED";report.fallback="Parked-only launch restriction; vehicle state not verified.";return save(c,report);}
  UtilityPopupManager.dismiss();Capability cap=capability(c,stored);report.resolvedComponent=cap.endpoint==null?"":cap.endpoint.flattenToString();report.discovery=cap.exception.isEmpty()?(cap.endpoint==null?"NOT_FOUND":"FOUND"):"FAILED";report.discoveryException=cap.exception;
  Diagnostics.operation("hvac_quick_discovery",report.fields());
  if(cap.endpoint!=null){report.quickAttempted=true;Diagnostics.operation("hvac_quick_launch_attempt",report.fields());
   try{c.startActivity(new Intent(ACTION).setComponent(cap.endpoint).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));report.quickAccepted=true;report.result="QUICK_REQUEST_ACCEPTED_UNVERIFIED";Diagnostics.operation("hvac_quick_request_dispatched_display_unverified",report.fields());DockOverlayService.launchBoundary();return save(c,report);}
   catch(ActivityNotFoundException|SecurityException e){report.quickAccepted=false;report.quickException=e.getClass().getSimpleName();Diagnostics.error("hvac_quick_launch_rejected",e);}
   catch(RuntimeException e){report.quickAccepted=false;report.quickException=e.getClass().getSimpleName();Diagnostics.error("hvac_quick_launch_failed",e);}
  }
  report.fallback=cap.endpoint==null?cap.reason:"Quick request rejected: "+report.quickException;
  Diagnostics.operation("hvac_normal_app_fallback",report.fields());
  try{report.normalComponent=normalComponent(c,stored,report.selectedPackage);
   if(report.normalComponent.isEmpty()){report.result="NO_NORMAL_LAUNCHER";report.fallback+=" No normal launcher Activity is available.";Toast.makeText(c,"No usable quick controls or normal app launcher. Choose another HVAC app.",Toast.LENGTH_LONG).show();return save(c,report);}
   Toast.makeText(c,"Quick controls unavailable; opening HVAC app",Toast.LENGTH_LONG).show();AppLaunchManager.Attempt attempt=AppLaunchManager.attempt(c,report.normalComponent);report.normalAccepted=attempt.accepted;report.normalException=attempt.exception;report.result=attempt.accepted?"NORMAL_REQUEST_ACCEPTED_UNVERIFIED":"NORMAL_REQUEST_FAILED";if(!attempt.reason.isEmpty())report.fallback+=" "+attempt.reason;
  }catch(RuntimeException e){report.normalAccepted=false;report.normalException=e.getClass().getSimpleName();report.result="NORMAL_REQUEST_FAILED";Diagnostics.error("hvac_normal_launch_failed",e);}
  return save(c,report);
 }
}
