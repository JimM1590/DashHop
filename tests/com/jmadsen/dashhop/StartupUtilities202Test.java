package com.jmadsen.dashhop;
/** Startup request routing and upgrade preference invariants; not Android UI simulation. */
public class StartupUtilities202Test {
 static int checks;static void check(boolean b,String why){checks++;if(!b)throw new AssertionError(why);}
 public static void main(String[] args){
  for(boolean explicit:new boolean[]{false,true})for(boolean permission:new boolean[]{false,true})for(boolean initialized:new boolean[]{false,true})for(boolean running:new boolean[]{false,true}){
   DockStartupPolicy.Action action=DockStartupPolicy.decide(explicit,permission,initialized,running);
   check(action==(explicit?DockStartupPolicy.Action.SHOW_SETTINGS:!permission||!initialized?DockStartupPolicy.Action.SHOW_SETUP:running?DockStartupPolicy.Action.KEEP_RUNNING:DockStartupPolicy.Action.START_DOCK),"Automatic launcher requests and explicit Settings have distinct outcomes");
  }
  check(DockStartupPolicy.decide(false,true,true,true)==DockStartupPolicy.Action.KEEP_RUNNING,"Repeated ordinary activation does not request another start or Settings");
  check(DockStartupPolicy.decide(false,false,true,false)==DockStartupPolicy.Action.SHOW_SETUP,"Missing permission cannot silently strand setup");
  check(DockStartupPolicy.decide(false,true,false,false)==DockStartupPolicy.Action.SHOW_SETUP,"First installation retains setup even with pregranted permission");
  check(DockRecoveryPolicy.decide(false,true,true,true)==DockRecoveryPolicy.Action.SKIP_DISABLED,"Boot recovery respects Stop even though ordinary explicit app activation may start the dock");
  check(DockStartupPolicy.decide(true,true,true,true)==DockStartupPolicy.Action.SHOW_SETTINGS,"Gear remains an explicit Settings route with an active dock");
  for(boolean enabled:new boolean[]{false,true})for(boolean available:new boolean[]{false,true})check(DockUtilities.hvac(enabled,available)==(enabled&&available),"HVAC visibility requires both user opt-in and a usable selected provider");
  RepositoryPolicyTest.Device device=new RepositoryPolicyTest.Device();ShortcutRepository original=new ShortcutRepository(device);
  original.set("apps","[\"org.example/.Navigation\",\"org.example/.Music\"]");original.set("hvacApp","@package:org.example.quickonly");original.set("weatherLat","41.0");original.set("weatherPlace","Saved test place");original.set("clock","24");original.set("edge","right");original.fraction("right",.73f);original.set("opacity",81);original.set("overlayEnabled",true);
  ShortcutRepository upgraded=new ShortcutRepository(device);
  check(upgraded.flag(DockUtilities.HVAC,true)&&upgraded.flag(DockUtilities.WEATHER,true)&&upgraded.flag(DockUtilities.CLOCK,true),"Absent new visibility settings inherit visible defaults on upgrade");
  check(upgraded.str("apps","").equals("[\"org.example/.Navigation\",\"org.example/.Music\"]"),"Upgrade does not rebuild or reorder saved shortcut identities");
  upgraded.set(DockUtilities.HVAC,false);upgraded.set(DockUtilities.WEATHER,false);upgraded.set(DockUtilities.CLOCK,false);
  ShortcutRepository reopened=new ShortcutRepository(device);
  check(!reopened.flag(DockUtilities.HVAC,true)&&!reopened.flag(DockUtilities.WEATHER,true)&&!reopened.flag(DockUtilities.CLOCK,true),"Independent utility preferences survive repository recreation");
  check(reopened.str("hvacApp","").equals("@package:org.example.quickonly")&&reopened.str("weatherLat","").equals("41.0")&&reopened.str("clock","").equals("24"),"Hiding utilities preserves provider/weather/clock configuration");
  check(reopened.num("opacity",0)==81&&reopened.str("edge","").equals("right")&&Math.abs(reopened.fraction("right")-.73)<.001,"Visibility changes do not reset dock appearance or position");
  reopened.set(DockUtilities.WEATHER,true);check(reopened.flag(DockUtilities.WEATHER,false)&&!reopened.flag(DockUtilities.CLOCK,true)&&!reopened.flag(DockUtilities.HVAC,true),"Enabling weather does not change other utilities");
  check(!DockUtilities.hvac(reopened.flag(DockUtilities.HVAC,true),true),"Provider reinstallation cannot override a user's disabled HVAC preference");
  System.out.println("PASS: "+checks+" startup/explicit navigation, utility opt-in and upgrade preservation assertions. Android/AGAMA behavior requires device testing.");
 }
}
