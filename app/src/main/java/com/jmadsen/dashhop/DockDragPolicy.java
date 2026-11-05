package com.jmadsen.dashhop;
final class DockDragPolicy {
 static String edge(float x,float y,int width,int height){
  if(width<=0||height<=0||Float.isNaN(x)||Float.isNaN(y))return "bottom";
  x=Math.max(0,Math.min(width,x));y=Math.max(0,Math.min(height,y));
  float nearest=y;String edge="top";
  if(height-y<nearest){nearest=height-y;edge="bottom";}
  if(x<nearest){nearest=x;edge="left";}
  if(width-x<nearest)edge="right";
  return edge;
 }
}
