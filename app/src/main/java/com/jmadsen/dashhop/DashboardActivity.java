package com.jmadsen.dashhop;
import android.app.Activity;import android.os.Bundle;import android.content.Intent;
/** Legacy cached shortcut compatibility; ordinary activation is silent. */
public final class DashboardActivity extends Activity {
 public void onCreate(Bundle saved){super.onCreate(saved);activate(getIntent());}
 protected void onNewIntent(Intent i){super.onNewIntent(i);setIntent(i);activate(i);}
 void activate(Intent i){DockStartupPolicy.Action action=DockStartup.decide(this,i);if(action==DockStartupPolicy.Action.SHOW_SETUP||action==DockStartupPolicy.Action.SHOW_SETTINGS)AppLaunchManager.settings(this,"");else DockStartup.activate(this,action);finish();}
}
