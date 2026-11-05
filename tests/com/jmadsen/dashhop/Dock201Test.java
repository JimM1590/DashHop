package com.jmadsen.dashhop;
/** Tests routing and fallback contracts, not Android window behavior. */
public class Dock201Test {
 static int checks;static void check(boolean b,String why){checks++;if(!b)throw new AssertionError(why);}
 public static void main(String[] args){
  for(String x:new String[]{null,"","@dashboard","@system_home"})check(DockRoutingPolicy.useSystemHome(x),"System Home and upgrade tokens must not reopen Dashboard");
  check(!DockRoutingPolicy.useSystemHome("com.example/.Home"),"Explicit installed Home component retained");
  check(!DockRoutingPolicy.useSystemHome("missing/.Home"),"Missing explicit app must be reported, not silently replaced");
  RepositoryPolicyTest.Device device=new RepositoryPolicyTest.Device();ShortcutRepository old=new ShortcutRepository(device);
  old.set("home","com.example/.Home");old.set("hvacApp","com.hvac/.Main");old.set("hvacMode","overlay");old.set("autoCollapse",false);old.set("autoDelay",12);old.set("color",0xff123456);old.fraction("right",.7f);
  ShortcutRepository reopened=new ShortcutRepository(device);
  check(reopened.str("hvacApp","").equals("com.hvac/.Main")&&reopened.str("hvacMode","").equals("overlay"),"HVAC component and action survive repository recreation");
  check(reopened.str("home","").equals("com.example/.Home")&&!reopened.flag("autoCollapse",true)&&reopened.num("autoDelay",0)==12&&reopened.num("color",0)==0xff123456,"Existing Home, auto-hide and appearance preserved");
  check(Math.abs(reopened.fraction("right")-.7)<.001,"Edge handle position preserved");
  AutoCollapsePolicy timer=new AutoCollapsePolicy();timer.configure(false,3,0);timer.visible(true,true,0);check(!timer.due(999999),"Persistent expanded setting applies to ordinary apps without workspace state");timer.visible(true,false,1000);check(timer.waitMillis(1000)==-1,"Manual collapse leaves accessible handle without a timer");
  timer.configure(true,3,2000);timer.visible(true,true,2000);check(timer.due(5000),"Re-enabled auto-hide works independently of app identity");
  System.out.println("PASS: "+checks+" standalone Home/HVAC routing, fallback, preference preservation and dock mode assertions. No Android runtime tests.");
 }
}
