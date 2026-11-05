package com.jmadsen.dashhop;
import android.view.*;
final class OverlayDockSurfaceHost implements DockSurfaceHost {
 private final WindowManager wm; private View view;
 OverlayDockSurfaceHost(WindowManager wm){this.wm=wm;}
 public void show(View v,WindowManager.LayoutParams p){clear();wm.addView(v,p);view=v;}
 public void clear(){if(view!=null){try{wm.removeViewImmediate(view);}catch(IllegalArgumentException ignored){}view=null;}}
}
