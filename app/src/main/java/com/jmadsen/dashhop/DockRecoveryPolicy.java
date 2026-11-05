package com.jmadsen.dashhop;
/** One decision per allowed system broadcast; no timer/relaunch loop. */
final class DockRecoveryPolicy {
 enum Action {SKIP_DISABLED,WAIT_FOR_UNLOCK,PERMISSION_REQUIRED,START_DOCK}
 static Action decide(boolean enabled,boolean restore,boolean unlocked,boolean permission){
  if(!enabled||!restore)return Action.SKIP_DISABLED;
  if(!unlocked)return Action.WAIT_FOR_UNLOCK;
  return permission?Action.START_DOCK:Action.PERMISSION_REQUIRED;
 }
}
