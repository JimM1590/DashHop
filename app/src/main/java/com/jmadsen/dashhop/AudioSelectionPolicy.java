package com.jmadsen.dashhop;
import java.util.*;
/** Input order is Android's session priority; IDs break no ties by app labels. */
final class AudioSelectionPolicy {
 static final class Entry {final String id;final boolean playing,eligible;Entry(String i,boolean p,boolean e){id=i;playing=p;eligible=e;}}
 static String select(String current,List<Entry> list,String newlyPlaying){
  for(Entry e:list)if(e.eligible&&e.playing&&e.id.equals(newlyPlaying))return e.id;
  Entry retained=null;for(Entry e:list)if(e.eligible&&e.id.equals(current))retained=e;
  if(retained!=null&&retained.playing)return retained.id;
  for(Entry e:list)if(e.eligible&&e.playing)return e.id;
  if(retained!=null)return retained.id;
  for(Entry e:list)if(e.eligible)return e.id;
  return "";
 }
}
