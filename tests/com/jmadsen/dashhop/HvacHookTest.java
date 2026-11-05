package com.jmadsen.dashhop;
public class HvacHookTest {
 static int checks;static void check(boolean b,String why){checks++;if(!b)throw new AssertionError(why);}
 public static void main(String[] args){
  check(HvacSelectionPolicy.packageName("org.example/.Launcher").equals("org.example"),"Legacy component selections preserve package");
  check(HvacSelectionPolicy.packageName("@package:org.example.quickonly").equals("org.example.quickonly"),"Quick-only provider selection needs no launcher component");
  check(HvacSelectionPolicy.packageName("org.example.quickonly").equals("org.example.quickonly"),"Plain package identities are supported");
  for(String invalid:new String[]{null,"","@package:","@package:bad package","../../outside","org.foo:bar"})check(HvacSelectionPolicy.packageName(invalid).isEmpty(),"Malformed package rejected");
  check(HvacSelectionPolicy.packageName(HvacSelectionPolicy.identity("org.example.quickonly")).equals("org.example.quickonly"),"Package-only picker identity round trips");
  HvacLaunchReport r=new HvacLaunchReport();r.selectedPackage="org.example.quickonly";
  check(r.text().contains("Not attempted"),"Unattempted requests distinct from rejected requests");
  check(r.text().contains("Popup appearance: UNVERIFIED"),"Default report cannot claim UI visibility");
  r.discovery="FOUND";r.resolvedComponent="org.example.quickonly/.AnyName";r.quickAttempted=true;r.quickAccepted=true;r.result="QUICK_REQUEST_ACCEPTED_UNVERIFIED";
  check(r.text().contains("Compatible quick Activity: Yes")&&r.text().contains(r.resolvedComponent),"Report includes actual discovery and explicit component");
  check(r.text().contains("Android accepted quick request: Yes (request only)"),"Accepted request clearly labelled as request only");
  check(Boolean.FALSE.equals(r.fields().get("hvac_popup_verified")),"Even accepted dispatch is never stored as verified popup");
  r.quickAccepted=false;r.quickException="SecurityException";r.fallback="Quick request rejected: SecurityException";r.normalComponent="org.example.quickonly/.Launcher";r.normalAccepted=true;r.result="NORMAL_REQUEST_ACCEPTED_UNVERIFIED";
  check(r.text().contains("Android accepted quick request: No")&&r.text().contains("SecurityException"),"Rejected quick launch and exception remain visible");
  check(r.text().contains("Android accepted normal request: Yes (request only)")&&r.text().contains("Fallback condition: Quick request rejected"),"Normal fallback does not overwrite quick failure");
  check(Boolean.FALSE.equals(r.fields().get("hvac_popup_verified")),"Normal fallback acceptance also remains UI unverified");
  check(r.fields().containsKey("hvac_selected_package")&&r.fields().containsKey("hvac_quick_attempted")&&r.fields().containsKey("hvac_resolved_component"),"Exportable diagnostic fields include required hook evidence");
  r.discovery="FAILED";r.discoveryException="IllegalStateException";check(r.text().contains("Compatible quick Activity: FAILED")&&r.text().contains("Discovery exception: IllegalStateException"),"Discovery failure is not presented as missing or success");
  System.out.println("PASS: "+checks+" launcher-free HVAC selection and truthful accepted/rejected/fallback diagnostic report assertions. Host logic only.");
 }
}
