package com.jmadsen.dashhop;
import java.util.*;import java.nio.file.*;import org.json.*;
public class DashHopperContractTest {
 static int checks;static void check(boolean b,String why){checks++;if(!b)throw new AssertionError(why);}
 static HvacEndpointPolicy.Candidate endpoint(String p,String c,boolean exported,boolean enabled,boolean app,boolean permission){return new HvacEndpointPolicy.Candidate(p,c,exported,enabled,app,permission);}
 public static void main(String[] args)throws Exception {
  String target="org.example.climate";
  List<HvacEndpointPolicy.Candidate> all=new ArrayList<>();
  all.add(endpoint("unrelated","unrelated/.A",true,true,true,true));
  all.add(endpoint(target,target+"/.ZMini",true,true,true,true));
  all.add(endpoint(target,target+"/.AMini",true,true,true,true));
  check(HvacEndpointPolicy.choose(target,all).equals(target+"/.AMini"),"Public endpoints require no metadata and sort deterministically within selected package");
  Collections.reverse(all);check(HvacEndpointPolicy.choose(target,all).equals(target+"/.AMini"),"Manifest/PackageManager ordering does not change selection");
  for(boolean exported:new boolean[]{false,true})for(boolean enabled:new boolean[]{false,true})for(boolean app:new boolean[]{false,true})for(boolean permission:new boolean[]{false,true}){
   List<HvacEndpointPolicy.Candidate> candidate=Arrays.asList(endpoint(target,target+"/.Mini",exported,enabled,app,permission));
   check((HvacEndpointPolicy.choose(target,candidate)!=null)==(exported&&enabled&&app&&permission),"Nonexported, disabled and permission-protected endpoints cannot be selected");
  }
  check(HvacEndpointPolicy.choose("another",all)==null,"No unrelated-package implicit handler");
  check(HvacEndpointPolicy.choose(null,all)==null&&HvacEndpointPolicy.choose("",all)==null,"Missing package is recoverable");
  check(HvacEndpointPolicy.choose(target,Collections.emptyList())==null,"Uninstalled/updated provider falls back");
  check(HvacEndpointPolicy.choose(target,Arrays.asList(endpoint(target,"",true,true,true,true),endpoint(target,null,true,true,true,true)))==null,"Malformed endpoints excluded");
  for(boolean enabled:new boolean[]{false,true})for(boolean restore:new boolean[]{false,true})for(boolean unlocked:new boolean[]{false,true})for(boolean permission:new boolean[]{false,true}){
   DockRecoveryPolicy.Action action=DockRecoveryPolicy.decide(enabled,restore,unlocked,permission);
   check(action==(!enabled||!restore?DockRecoveryPolicy.Action.SKIP_DISABLED:!unlocked?DockRecoveryPolicy.Action.WAIT_FOR_UNLOCK:!permission?DockRecoveryPolicy.Action.PERMISSION_REQUIRED:DockRecoveryPolicy.Action.START_DOCK),"Recovery requires user intent, unlock and overlay permission");
  }
  AutoCollapsePolicy timer=new AutoCollapsePolicy();timer.configure(true,0,0);timer.visible(true,true,0);timer.touch(true,10);timer.touch(false,20);timer.scroll(30);timer.menu(true,40);timer.menu(false,50);timer.boundary(60);check(timer.waitMillis(999999)==-1&&!timer.due(999999),"Zero remains disabled through all interaction boundaries");
  timer.configure(true,3,100);check(!timer.due(3099)&&timer.due(3100),"Switching from zero restores positive-delay behavior");
  check(DockDragPolicy.edge(600,0,1280,720).equals("top"),"Drag to top");check(DockDragPolicy.edge(600,720,1280,720).equals("bottom"),"Drag to bottom");check(DockDragPolicy.edge(0,300,1280,720).equals("left"),"Drag to left");check(DockDragPolicy.edge(1280,300,1280,720).equals("right"),"Drag to right");check(DockDragPolicy.edge(Float.NaN,0,1280,720).equals("bottom")&&DockDragPolicy.edge(0,0,0,0).equals("bottom"),"Invalid drag safely defaults");
  WeatherData data=new WeatherData(new JSONObject(new String(Files.readAllBytes(Paths.get(args[0])),java.nio.charset.StandardCharsets.UTF_8)),"Fixture",1000);
  check(WeatherIndicator.icon(null,true,false,1001).equals("?"),"No fabricated condition without data");
  check(WeatherIndicator.icon(data,false,false,1001).equals("?"),"Old location/source observation not presented as current");
  check(WeatherIndicator.icon(data,true,false,1001).equals(WeatherData.icon(data.code)),"Fresh matching data shows real condition");
  check(WeatherIndicator.icon(data,true,false,1801000).startsWith("◷"),"Stale cache explicitly marked");
  check(WeatherIndicator.icon(data,true,true,1001).startsWith("!"),"Cached condition with failed refresh explicitly marked");
  check(WeatherIndicator.status(data,true,false,1801000).startsWith("Stale cached"),"Stale status exposed to accessibility");
  check(WeatherIndicator.status(null,true,false,1001).startsWith("Weather unavailable"),"Unavailable setup described");
  System.out.println("PASS: "+checks+" public HVAC endpoint filtering, recovery, zero-delay, drag placement and truthful weather assertions. Host logic only.");
 }
}
