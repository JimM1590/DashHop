package com.jmadsen.dashhop;
final class LaunchSafety {
 static boolean allowed(boolean parkedOnly,DrivingStateProvider.State state){return !parkedOnly||state==DrivingStateProvider.State.PARKED_VERIFIED;}
 static final DrivingStateProvider provider=()->DrivingStateProvider.State.UNKNOWN;
}
