package com.jmadsen.dashhop;
import android.app.Activity;import android.os.Bundle;
/** The ordinary MAIN/LAUNCHER entry: no UI, no HOME launch, no retained task. */
public final class DockStartActivity extends Activity {
 public void onCreate(Bundle saved){super.onCreate(saved);DockStartupPolicy.Action action=DockStartup.decide(this,getIntent());
  if(action==DockStartupPolicy.Action.SHOW_SETUP||action==DockStartupPolicy.Action.SHOW_SETTINGS)AppLaunchManager.settings(this,"");else DockStartup.activate(this,action);
  finish();
 }
}
