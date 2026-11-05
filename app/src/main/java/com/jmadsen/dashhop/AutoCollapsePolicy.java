package com.jmadsen.dashhop;
/** Monotonic, host-testable policy. Outside DashHop is a visibility heuristic, not a task identity. */
final class AutoCollapsePolicy {
 boolean enabled=true,eligible=false,touch=false,menu=false;long delay=3000,deadline=-1;
 void configure(boolean on,int seconds,long now){delay=Math.max(0,Math.min(15,seconds))*1000L;enabled=on&&delay>0;boundary(now);}
 void visible(boolean outside,boolean expanded,long now){boolean next=outside&&expanded;if(next!=eligible){eligible=next;touch=false;menu=false;boundary(now);}if(!next)deadline=-1;}
 void boundary(long now){deadline=enabled&&eligible&&!touch&&!menu?now+Math.max(250,delay):-1;}
 void touch(boolean active,long now){touch=active;boundary(now);}
 void menu(boolean active,long now){menu=active;boundary(now);}
 void scroll(long now){boundary(now);}
 void sleep(){eligible=false;touch=false;menu=false;deadline=-1;}
 long waitMillis(long now){return deadline<0?-1:Math.max(0,deadline-now);}
 boolean due(long now){return enabled&&eligible&&!touch&&!menu&&deadline>=0&&now>=deadline;}
}
