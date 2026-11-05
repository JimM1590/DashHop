package com.jmadsen.dashhop;
final class PopupBoundsPolicy {
 static int[] fit(int x,int y,int w,int h,int screenW,int screenH,int minW,int minH){screenW=Math.max(1,screenW);screenH=Math.max(1,screenH);w=Math.max(Math.min(minW,screenW),Math.min(w,screenW));h=Math.max(Math.min(minH,screenH),Math.min(h,screenH));return new int[]{Math.max(0,Math.min(x,screenW-w)),Math.max(0,Math.min(y,screenH-h)),w,h};}
}
