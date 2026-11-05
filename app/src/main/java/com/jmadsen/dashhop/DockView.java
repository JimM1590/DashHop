package com.jmadsen.dashhop;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.text.TextUtils;
final class DockView extends LinearLayout {
 DockUtilityLayout utilityHost;
 final ShortcutRepository repo; final boolean vertical; final int fg;final Runnable collapse;
 interface Interaction {void touch(boolean active);void scroll();void menu(boolean active);}
 Interaction interaction;boolean touching;
 DockView interaction(Interaction i){interaction=i;return this;}
 public boolean dispatchTouchEvent(MotionEvent e){if(e.getActionMasked()==MotionEvent.ACTION_DOWN)touching=true;if(interaction!=null&&e.getActionMasked()==MotionEvent.ACTION_DOWN)interaction.touch(true);boolean handled=super.dispatchTouchEvent(e);if(e.getActionMasked()==MotionEvent.ACTION_UP||e.getActionMasked()==MotionEvent.ACTION_CANCEL){touching=false;if(interaction!=null)interaction.touch(false);}return handled;}
 DockView(Context c,ShortcutRepository r,boolean v,int thickness,Runnable hide){super(c);repo=r;vertical=v;collapse=hide;setOrientation(v?VERTICAL:HORIZONTAL);setGravity(Gravity.CENTER_VERTICAL);
 int col=r.num("color",0xff132331);String foreground=r.str("foreground","auto");boolean light=foreground.equals("light")||foreground.equals("auto")&&(Color.red(col)*.299+Color.green(col)*.587+Color.blue(col)*.114<155);fg=light?Color.WHITE:Color.BLACK;
 GradientDrawable bg=new GradientDrawable();bg.setColor((Math.max(55,r.num("opacity",92))*255/100<<24)|(col&0xffffff));setBackground(bg);
 LinearLayout home=new LinearLayout(c);home.setOrientation(v?VERTICAL:HORIZONTAL);TextView h=text("⌂\nHome",16);h.setContentDescription("Open Home destination");h.setOnClickListener(w->AppLaunchManager.home(c));h.setOnLongClickListener(w->{menu(h);return true;});
 home.addView(h,v?new LinearLayout.LayoutParams(-1,0,1):new LinearLayout.LayoutParams(0,-1,1));TextView fold=text(DockArrows.glyph(r.str("edge","bottom"),true),24);fold.setContentDescription("Collapse dock; drag to another edge");fold.setOnClickListener(w->collapse.run());float[] dragStart=new float[2];boolean[] dragging={false};int slop=ViewConfiguration.get(c).getScaledTouchSlop();fold.setOnTouchListener((view,event)->{switch(event.getActionMasked()){case MotionEvent.ACTION_DOWN:dragStart[0]=event.getRawX();dragStart[1]=event.getRawY();dragging[0]=false;return true;case MotionEvent.ACTION_MOVE:if(Math.hypot(event.getRawX()-dragStart[0],event.getRawY()-dragStart[1])>slop)dragging[0]=true;return true;case MotionEvent.ACTION_UP:if(dragging[0]){android.util.DisplayMetrics screen=new android.util.DisplayMetrics();c.getSystemService(WindowManager.class).getDefaultDisplay().getRealMetrics(screen);r.set("edge",DockDragPolicy.edge(event.getRawX(),event.getRawY(),screen.widthPixels,screen.heightPixels));Diagnostics.info("dock_edge_changed_by_drag");}else view.performClick();return true;case MotionEvent.ACTION_CANCEL:dragging[0]=false;return true;}return true;});home.addView(fold,new LinearLayout.LayoutParams(v?-1:dp(48),v?dp(48):-1));fixed(home,dp(v?104:132));
 LinearLayout apps=new LinearLayout(c);apps.setOrientation(v?VERTICAL:HORIZONTAL);AppCatalogRepository catalog=new AppCatalogRepository(c);
 for(String id:r.shortcuts()){AppCatalogRepository.App a=catalog.get(id);LinearLayout tile=new LinearLayout(c);tile.setGravity(Gravity.CENTER);tile.setOrientation(VERTICAL);ImageView icon=new ImageView(c);icon.setImageDrawable(a==null?c.getDrawable(android.R.drawable.ic_menu_help):a.icon);int size=Math.min(dp(40),Math.max(dp(24),thickness-dp(40)));tile.addView(icon,new LinearLayout.LayoutParams(size,size));TextView label=text(a==null?"Missing app":a.label,14);label.setSingleLine();label.setEllipsize(TextUtils.TruncateAt.END);tile.addView(label,new LinearLayout.LayoutParams(-1,-2));tile.setContentDescription(a==null?"Missing app; long press to edit":a.label);tile.setOnClickListener(w->AppLaunchManager.launch(c,id));tile.setOnLongClickListener(w->{AppLaunchManager.settings(c,"edit");return true;});apps.addView(tile,new LinearLayout.LayoutParams(v?-1:dp(100),v?dp(88):-1));}
 ScrollView verticalApps=null;HorizontalScrollView horizontalApps=null;
 if(v){ScrollView s=new ScrollView(c);verticalApps=s;s.setFillViewport(true);s.setOnScrollChangeListener((view,x,y,oldX,oldY)->{if(interaction!=null)interaction.scroll();});s.addView(apps);addView(s,new LinearLayout.LayoutParams(-1,0,1));}else{HorizontalScrollView s=new HorizontalScrollView(c);horizontalApps=s;s.setFillViewport(true);s.setOnScrollChangeListener((view,x,y,oldX,oldY)->{if(interaction!=null)interaction.scroll();});s.addView(apps);addView(s,new LinearLayout.LayoutParams(0,-1,1));}
 TextView gear=text("⚙",28);gear.setContentDescription("Open DashHopper Settings");gear.setOnClickListener(w->AppLaunchManager.settings(c,""));fixed(gear,dp(56));
 android.util.DisplayMetrics display=new android.util.DisplayMetrics();c.getSystemService(WindowManager.class).getDefaultDisplay().getMetrics(display);
 utilityHost=new DockUtilityLayout(c,v,Math.max(dp(48),(v?display.heightPixels:display.widthPixels)-dp(v?104+56+72:132+56+72)),thickness);addView(utilityHost,new LinearLayout.LayoutParams(v?-1:-2,v?-2:-1));
 int other=dp(v?104+56:132+56)+(r.flag(DockUtilities.HVAC,true)&&HvacAvailability.get(c).state().available?dp(64):0)+(r.flag(DockUtilities.WEATHER,true)?dp(56):0)+(r.flag(DockUtilities.CLOCK,true)?dp(v?96:176):0)+(r.flag(DockUtilities.NOTES,true)?dp(48):0);
 if(r.flag(DockUtilities.AUDIO,true)){int available=Math.max(dp(48),(v?display.heightPixels:display.widthPixels)-other-dp(72));AudioDockItem audio=new AudioDockItem(c,fg,v,Math.min(dp(v?80:220),available),horizontalApps,verticalApps,()->touching);fixed(audio,dp(48));}
 if(r.flag(DockUtilities.NOTES,true)){TextView notes=text("✎",24);notes.setContentDescription("Open Notepad Mini");notes.setOnClickListener(w->UtilityPopupManager.toggle(c,"notes"));fixed(notes,dp(48));}
 if(DockUtilities.hvac(r.flag(DockUtilities.HVAC,true),HvacAvailability.get(c).state().available)){
 TextView hvac=text("❄\nHVAC",16);hvac.setContentDescription("Open HVAC; long press to configure");hvac.setOnClickListener(w->HvacIntegration.open(c));hvac.setOnLongClickListener(w->{AppLaunchManager.settings(c,"hvac");return true;});fixed(hvac,dp(64));
 }
 if(r.flag(DockUtilities.WEATHER,true)){
 TextView weather=text("?",24);weather.setContentDescription("Open weather popup");weather.setOnClickListener(w->UtilityPopupManager.open(c,"weather"));fixed(weather,dp(56));WeatherRepository repository=WeatherRepository.get(c);WeatherRepository.Listener listener=()->{WeatherData data=repository.data;boolean matches=repository.iconAvailable(),failed=!repository.error.isEmpty();long now=System.currentTimeMillis();weather.setText(WeatherIndicator.icon(data,matches,failed,now));weather.setContentDescription(WeatherIndicator.status(data,matches,failed,now));weather.setTextColor(data==null||!matches||failed||data.stale(now)?0xffffce73:fg);};addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){public void onViewAttachedToWindow(View view){listener.changed();repository.add(listener);}public void onViewDetachedFromWindow(View view){repository.remove(listener);}});
 }
 if(r.flag(DockUtilities.CLOCK,true)){
 TextClock clock=new TextClock(c);String f=r.str("clock","system");clock.setFormat12Hour("24".equals(f)?"HH:mm":"h:mm a");clock.setFormat24Hour("12".equals(f)?"h:mm a":"HH:mm");clock.setTextColor(fg);clock.setTextSize(v?18:22);clock.setGravity(Gravity.CENTER);clock.setContentDescription("Local time");clock.setAutoSizeTextTypeUniformWithConfiguration(12,v?18:22,1,android.util.TypedValue.COMPLEX_UNIT_SP);LinearLayout calendarControl=new LinearLayout(c);calendarControl.setGravity(Gravity.CENTER);calendarControl.setOrientation(v?VERTICAL:HORIZONTAL);calendarControl.addView(clock,v?new LinearLayout.LayoutParams(-1,0,1):new LinearLayout.LayoutParams(0,-1,1));TextView calendar=text("▦",22);calendarControl.addView(calendar,v?new LinearLayout.LayoutParams(-1,dp(40)):new LinearLayout.LayoutParams(dp(40),-1));calendarControl.setContentDescription("Clock and calendar popup");calendarControl.setOnClickListener(w->UtilityPopupManager.open(c,"calendar"));clock.setOnClickListener(w->calendarControl.performClick());calendar.setOnClickListener(w->calendarControl.performClick());fixed(calendarControl,dp(v?96:176));
 }
 }
 int dp(int n){return DockController.px(getContext(),n);}
 TextView text(String s,int size){TextView t=new TextView(getContext());t.setText(s);t.setTextSize(size);t.setTextColor(fg);t.setGravity(Gravity.CENTER);t.setPadding(dp(4),dp(2),dp(4),dp(2));t.setMinHeight(dp(48));return t;}
 void fixed(View v,int length){if(utilityHost!=null)utilityHost.addView(v,new ViewGroup.LayoutParams(vertical?-1:length,vertical?length:-1));else addView(v,new LinearLayout.LayoutParams(vertical?-1:length,vertical?length:-1));}
 void menu(View anchor){if(interaction!=null)interaction.menu(true);PopupMenu m=new PopupMenu(getContext(),anchor);
 m.getMenu().add("Open Home").setOnMenuItemClickListener(i->{AppLaunchManager.home(getContext());return true;});
 m.getMenu().add("Open installed app").setOnMenuItemClickListener(i->{AppLaunchManager.settings(getContext(),"fullscreen");return true;});
 m.getMenu().add("HVAC settings").setOnMenuItemClickListener(i->{AppLaunchManager.settings(getContext(),"hvac");return true;});
 m.getMenu().add("Collapse dock").setOnMenuItemClickListener(i->{collapse.run();return true;});
 m.getMenu().add("Settings").setOnMenuItemClickListener(i->{AppLaunchManager.settings(getContext(),"");return true;});
 m.getMenu().add("Stop dock").setOnMenuItemClickListener(i->{DockController.stop(getContext());return true;});
 m.setOnDismissListener(menu->{if(interaction!=null)interaction.menu(false);});m.show();}
}
