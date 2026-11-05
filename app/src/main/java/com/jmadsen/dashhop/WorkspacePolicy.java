package com.jmadsen.dashhop;
/** Pure layout policy. No privileged capability is inferred from these values. */
final class WorkspacePolicy {
 static final String DASHBOARD="@dashboard";
 static int count(String preset){return "single".equals(preset)?1:"side".equals(preset)?2:3;}
 static float fraction(float f){return Float.isNaN(f)||Float.isInfinite(f)?.5f:Math.max(0,Math.min(1,f));}
 static int handleOffset(float fraction,int available){return Math.round(fraction(fraction)*Math.max(0,available));}
 static float handleFraction(int offset,int available){return available<=0?.5f:fraction((float)offset/available);}
 static String panelKey(String preset,int index){return "panel."+preset+"."+index;}
 static boolean vertical(String edge){return "left".equals(edge)||"right".equals(edge);}
}
