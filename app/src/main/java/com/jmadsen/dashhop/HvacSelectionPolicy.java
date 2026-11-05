package com.jmadsen.dashhop;
/** Accepts legacy launcher components and package-only HVAC selections. */
final class HvacSelectionPolicy {
 static final String PREFIX="@package:";
 static String packageName(String stored){
  if(stored==null)return "";String p=stored.startsWith(PREFIX)?stored.substring(PREFIX.length()):stored;
  int slash=p.indexOf('/');if(slash>=0)p=p.substring(0,slash);
  return p.matches("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)*")?p:"";
 }
 static String identity(String packageName){return PREFIX+packageName;}
}
