package com.jmadsen.dashhop;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.TextView;
final class RevealHandle extends TextView {
 interface Move {void accept(float delta,boolean done);}
 float x,y;boolean moved;
 RevealHandle(Context c,ShortcutRepository r,boolean vertical,Move move,Runnable reveal){super(c);setText("bar".equals(r.str("handleStyle","arrow"))?"━":DockArrows.glyph(r.str("edge","bottom"),false));setGravity(Gravity.CENTER);setTextSize(22);setTextColor(Color.WHITE);GradientDrawable bg=new GradientDrawable();bg.setColor(r.num("handleColor",0xff23495b));bg.setCornerRadius(DockController.px(c,12));setBackground(bg);setAlpha(Math.max(.55f,r.num("handleOpacity",90)/100f));setContentDescription("Reveal DashHopper dock; drag along this edge to move");setOnClickListener(v->reveal.run());
 int slop=ViewConfiguration.get(c).getScaledTouchSlop();setOnTouchListener((v,e)->{switch(e.getActionMasked()){case MotionEvent.ACTION_DOWN:x=e.getRawX();y=e.getRawY();moved=false;return true;case MotionEvent.ACTION_MOVE:float dx=e.getRawX()-x,dy=e.getRawY()-y;if(Math.hypot(dx,dy)>slop)moved=true;if(moved)move.accept(vertical?dy:dx,false);return true;case MotionEvent.ACTION_UP:if(moved)move.accept(vertical?e.getRawY()-y:e.getRawX()-x,true);else performClick();return true;case MotionEvent.ACTION_CANCEL:return true;}return false;});
 }
}
