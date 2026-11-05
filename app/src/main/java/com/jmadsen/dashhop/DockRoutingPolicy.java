package com.jmadsen.dashhop;
/** Legacy dashboard Home assignments become system Home without changing stored preferences. */
final class DockRoutingPolicy {
 static boolean useSystemHome(String stored){return stored==null||stored.isEmpty()||"@dashboard".equals(stored)||"@system_home".equals(stored);}
}
