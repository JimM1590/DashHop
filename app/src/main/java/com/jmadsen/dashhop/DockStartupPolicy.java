package com.jmadsen.dashhop;
/** Ordinary app activation and explicit Settings access are different requests. */
final class DockStartupPolicy {
 enum Action {SHOW_SETTINGS,SHOW_SETUP,KEEP_RUNNING,START_DOCK}
 static Action decide(boolean explicitSettings,boolean permission,boolean initialized,boolean running){
  if(explicitSettings)return Action.SHOW_SETTINGS;
  if(!permission||!initialized)return Action.SHOW_SETUP;
  return running?Action.KEEP_RUNNING:Action.START_DOCK;
 }
}
