package com.jmadsen.dashhop;
import java.util.*;
/** Public contract requires no metadata. Choose lexicographically smallest eligible component. */
final class HvacEndpointPolicy {
 static final class Candidate {
  final String packageName,component;final boolean exported,enabled,appEnabled,permissionAllowed;
  Candidate(String p,String c,boolean x,boolean e,boolean a,boolean permission){packageName=p;component=c;exported=x;enabled=e;appEnabled=a;permissionAllowed=permission;}
 }
 static String choose(String selectedPackage,List<Candidate> candidates){
  if(selectedPackage==null||selectedPackage.isEmpty())return null;
  String selected=null;
  for(Candidate c:candidates)if(selectedPackage.equals(c.packageName)&&c.exported&&c.enabled&&c.appEnabled&&c.permissionAllowed&&c.component!=null&&!c.component.isEmpty()&&(selected==null||c.component.compareTo(selected)<0))selected=c.component;
  return selected;
 }
}
