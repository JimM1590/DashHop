package com.jmadsen.dashhop;
import java.util.*;
public final class Feature210Test {
 static int checks;static void check(boolean b,String why){checks++;if(!b)throw new AssertionError(why);}
 static AudioSelectionPolicy.Entry e(String id,boolean playing,boolean eligible){return new AudioSelectionPolicy.Entry(id,playing,eligible);}
 public static void main(String[] args){
  List<AudioSelectionPolicy.Entry> sessions=Arrays.asList(e("a",true,true),e("b",true,true),e("bridge",true,false));
  check(AudioSelectionPolicy.select("",sessions,"").equals("a"),"Discover already playing priority session at startup");
  check(AudioSelectionPolicy.select("a",sessions,"").equals("a"),"Two playing sessions do not oscillate");
  check(AudioSelectionPolicy.select("a",sessions,"b").equals("b"),"Different session starts playing and takes selection");
  check(AudioSelectionPolicy.select("b",sessions,"").equals("b"),"Keep new source despite Android priority list remaining ordered");
  check(AudioSelectionPolicy.select("b",Arrays.asList(e("a",false,true),e("b",false,true)),"").equals("b"),"Retain selected paused source");
  check(AudioSelectionPolicy.select("b",Arrays.asList(e("a",true,true),e("b",false,true)),"").equals("a"),"Playing source wins over paused source");
  check(AudioSelectionPolicy.select("gone",Arrays.asList(e("bridge",true,false)),"").isEmpty(),"Destroyed/no eligible session clears selection");
  check(AudioSelectionPolicy.select("gone",Arrays.asList(e("paused",false,true)),"").equals("paused"),"Remaining paused eligible session can be retained on recovery");
  check(AudioSelectionPolicy.select("",Collections.emptyList(),"").isEmpty(),"Empty list yields empty state");
  for(int mask=0;mask<128;mask++){boolean was=(mask&1)!=0,ours=(mask&2)!=0,ack=(mask&4)!=0,same=(mask&8)!=0,paused=(mask&16)!=0,replaced=(mask&32)!=0,lost=(mask&64)!=0;boolean expected=was&&ours&&ack&&same&&paused&&!replaced&&!lost;check(AudioRestorePolicy.restore(was,ours,ack,same,paused,replaced,lost)==expected,"Restore only safe prior interrupted/acknowledged session; combinations "+mask);}
  DictationPolicy p=new DictationPolicy();long first=p.begin(100);check(!p.due(first,10099),"Do not stop before ten second ceiling without silence");check(p.due(first,10100),"Ten second ceiling");p.ended(first,1000);check(!p.due(first,3999),"Silence target not early");check(p.due(first,4000),"Three second supported silence");p.speech(first);check(!p.due(first,4000),"Resumed speech resets silence target");check(p.stop(first,true),"Save enters finalization immediately");check(!p.stop(first,true),"No second stop/restart during finalization");p.cancel();check(!p.finish(first,true),"Canceled final callback cannot autosave");check(!p.active(first),"Canceled timer invalidated");long second=p.begin(20000);check(!p.finish(first,true),"Old callback cannot affect a new attempt");check(p.finish(second,true),"Native shorter completion accepted");check(!p.finish(second,true),"Exactly one final completion");long error=p.begin(30000);check(p.finish(error,false),"Provider failure ends attempt");check(!p.active(error),"Error cannot retain microphone state");
  for(int i=0;i<100;i++){long t=p.begin(i*20000L);p.ended(t,i*20000L+100);p.cancel();check(!p.due(t,i*20000L+10000),"Canceled timers never fire");check(!p.finish(t,true),"Canceled speech callbacks never save");}
  check(DictationPolicy.finalOutput("old note","final words","partial",false).equals("old note\nfinal words"),"Final recognition appends exactly once to original draft");
  check(DictationPolicy.finalOutput("old note","","unreviewed partial",false)==null,"No final text: do not autosave interim words or a preexisting note");
  check(DictationPolicy.finalOutput("old note","","reviewed partial",true).equals("reviewed partial"),"Explicit Save can retain displayed draft after provider timeout/error");
  check(DictationPolicy.finalOutput("","","",true)==null,"Manual blank save creates no empty note");
  RepositoryPolicyTest.Device device=new RepositoryPolicyTest.Device();ShortcutRepository prefs=new ShortcutRepository(device);prefs.set("apps","[\"org.example/.Nav\"]");prefs.set("hvacApp","@package:example.quick");
  check(prefs.flag(DockUtilities.AUDIO,true)&&prefs.flag(DockUtilities.NOTES,true),"New utilities have explicit visible defaults without resetting old prefs");
  prefs.set(DockUtilities.AUDIO,false);check(!prefs.flag(DockUtilities.AUDIO,true)&&prefs.flag(DockUtilities.NOTES,true),"Notes independent of Audio toggle");
  prefs.set(DockUtilities.NOTES,false);prefs.set(DockUtilities.AUDIO,true);check(prefs.flag(DockUtilities.AUDIO,false)&&!prefs.flag(DockUtilities.NOTES,true),"Audio independent of Notes toggle");
  check(prefs.str("hvacApp","").equals("@package:example.quick")&&prefs.str("apps","").equals("[\"org.example/.Nav\"]"),"Feature enablement preserves legacy provider and ordered app identities");
  String[] edges={"top","bottom","left","right"};String[] expand={"▾","▴","›","‹"},collapse={"▴","▾","‹","›"};for(int i=0;i<4;i++){check(DockArrows.glyph(edges[i],false).equals(expand[i]),"Expand next-action arrow "+edges[i]);check(DockArrows.glyph(edges[i],true).equals(collapse[i]),"Collapse next-action arrow "+edges[i]);}
  for(int sw:new int[]{320,720,1280})for(int sh:new int[]{240,720})for(int x:new int[]{-200,0,1500})for(int y:new int[]{-200,0,1500}){int[] b=PopupBoundsPolicy.fit(x,y,2000,1000,sw,sh,340,260);check(b[0]>=0&&b[1]>=0&&b[0]+b[2]<=sw&&b[1]+b[3]<=sh,"Restored bounds clamp to display, including portrait/small geometry");}
  System.out.println("PASS: "+checks+" Audio Mini selection, conditional restore, dictation cancel/timing and popup/arrow assertions. Host policies only, not Android hardware.");
 }
}
