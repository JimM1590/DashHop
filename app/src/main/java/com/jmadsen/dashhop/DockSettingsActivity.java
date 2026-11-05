package com.jmadsen.dashhop;
import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.util.*;
public final class DockSettingsActivity extends Activity {
 WeatherSetup weatherSetup;ShortcutRepository repo; LinearLayout body; TextView status,hvacTestReport;ScrollView settingsScroll;boolean resumed,pendingLocation,showingSettings;
 int dp(int n){return (int)(getResources().getDisplayMetrics().density*n+.5f);}
 boolean isShowingSettings(){return showingSettings;}
 public void onCreate(Bundle b){DockStartupPolicy.Action action=DockStartup.decide(this,getIntent());showingSettings=action==DockStartupPolicy.Action.SHOW_SETTINGS||action==DockStartupPolicy.Action.SHOW_SETUP;setTheme(showingSettings?R.style.DashHopperSettingsTheme:android.R.style.Theme_NoDisplay);super.onCreate(b);
  if(!showingSettings){DockStartup.activate(this,action);finish();return;}
  getIntent().putExtra(DockStartup.SHOW_SETTINGS,true);repo=new ShortcutRepository(this);repo.set("setupSeen",true);build();
 }
 protected void onNewIntent(Intent i){super.onNewIntent(i);setIntent(i);DockStartupPolicy.Action action=DockStartup.decide(this,i);
  if(action!=DockStartupPolicy.Action.SHOW_SETTINGS&&action!=DockStartupPolicy.Action.SHOW_SETUP){DockStartup.activate(this,action);finish();return;}
  showingSettings=true;getIntent().putExtra(DockStartup.SHOW_SETTINGS,true);build();showMode();
 }
 protected void onResume(){super.onResume();if(!showingSettings||isFinishing())return;resumed=true;ProcessExitDiagnostics.record(this);DockController.ensure(this);HvacAvailability.get(this).refresh("settings_resumed");AudioSessionRepository.get(this).refresh();build();showMode();runPendingLocation();}
 protected void onPause(){super.onPause();resumed=false;}
 boolean running(){return DockOverlayService.running();}
 void showMode(){String mode=getIntent().getStringExtra("mode");getIntent().removeExtra("mode");
 if("fullscreen".equals(mode))AppPicker.show(this,"Open installed app",id->AppLaunchManager.launch(this,id));
 else if("hvac".equals(mode))chooseHvac();
 else if("calendar".equals(mode))Toast.makeText(this,"Calendar access is configured below",Toast.LENGTH_LONG).show();
 else if("weather".equals(mode))Toast.makeText(this,"Weather setup is below auto-collapse",Toast.LENGTH_LONG).show();
 else if("add".equals(mode))picker(false);else if("edit".equals(mode))edit();}
 TextView label(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(0xffedf5f7);t.setPadding(0,dp(6),0,dp(6));return t;}
 void utilityToggle(String name,String key){button(name+": "+(repo.flag(key,true)?"ON":"OFF"),()->{repo.set(key,!repo.flag(key,true));build();});}
 void section(String s){body.addView(label(s,21));}
 void button(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setMinHeight(dp(48));b.setOnClickListener(v->r.run());body.addView(b,new LinearLayout.LayoutParams(-1,-2));}
 void build(){int previousY=settingsScroll==null?0:settingsScroll.getScrollY();ScrollView scroll=new ScrollView(this);settingsScroll=scroll;scroll.setFillViewport(true);body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(24),dp(16),dp(24),dp(32));body.setBackgroundColor(0xff0d1822);scroll.addView(body);setContentView(scroll);
 ImageView branding=new ImageView(this);branding.setImageDrawable(getApplicationInfo().loadIcon(getPackageManager()));branding.setContentDescription("DashHopper app icon");body.addView(branding,new LinearLayout.LayoutParams(dp(64),dp(64)));body.addView(label("DashHopper",32));body.addView(label("Your apps, just a hop away.  •  Development 2.1.2",16));
 status=label((Settings.canDrawOverlays(this)?"Overlay permission granted":"Overlay permission needed")+"  •  "+(running()?"Dock enabled (paused inside Settings)":"Dock stopped")+"\n"+repo.str("error",""),14);body.addView(status);
 button("1  Grant / review display-over-apps permission",()->{DashHopApplication.permissionFlow=true;DockOverlayService.refresh();try{startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName())));}catch(Exception e){try{startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+getPackageName())));}catch(Exception ignored){Toast.makeText(this,"Open Android Settings → Apps → DashHopper",Toast.LENGTH_LONG).show();}}});
 button("Done — return to previous app",this::finish);
 button("Enable dock",()->{DockController.start(this);build();});
 button("Stop dock",()->{DockController.stop(this);build();});
 button("Reveal dock / recover handle",()->{repo.set("hidden",false);build();});
 button("Show dock over Home",()->{DockController.start(this);repo.set("hidden",false);AppLaunchManager.home(this);});
 button("Restore enabled dock after boot: "+(repo.flag("restoreDockOnBoot",true)?"ON":"OFF"),()->{repo.set("restoreDockOnBoot",!repo.flag("restoreDockOnBoot",true));build();});
 section("Home button");
 body.addView(label("Destination: "+(DockRoutingPolicy.useSystemHome(repo.str("home",""))?"Android's selected Home app":appLabel(repo.str("home",""))),17));
 button("Choose Home destination app",()->picker(true));
 button("Use Android's selected Home app",()->{repo.set("home","@system_home");build();});
 button("Open Android Home settings",()->{try{startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));}catch(Exception e){Toast.makeText(this,"Select Home in Android Settings",Toast.LENGTH_LONG).show();}});
 body.addView(label("DashHopper is a standalone dock. It does not replace or require a particular launcher. Existing Home app assignments are retained; old DashHopper dashboard assignments open Android Home.",14));
 section("Dock Utilities");
 utilityToggle("HVAC Quick Control",DockUtilities.HVAC);
 utilityToggle("Weather",DockUtilities.WEATHER);
 utilityToggle("Clock / Calendar",DockUtilities.CLOCK);
 utilityToggle("Audio Mini",DockUtilities.AUDIO);utilityToggle("Notepad Mini",DockUtilities.NOTES);
 body.addView(label("Hidden utilities keep their settings. Weather starts no dock refresh work while hidden. HVAC is shown only when enabled here and a selected provider can be launched.",14));
 section("HVAC provider");
 body.addView(label("HVAC app: "+HvacIntegration.selectionLabel(this,repo.str("hvacApp","")),17));
 button("Choose HVAC app",this::chooseHvac);
 body.addView(label(HvacAvailability.get(this).state().reason+"\nThe selected app’s public quick-control Activity opens when available; otherwise its normal app opens. The HVAC developer owns its interface and permissions. A dispatched request is not proof that its interface appeared.",14));
 button("Clear HVAC assignment",()->{repo.set("hvacApp","");build();});
 section("Audio Mini");
 body.addView(label(AudioSessionRepository.get(this).authorized()?"Notification access enabled — local media-session capabilities require device testing.":"Notification access needed to discover compatible media sessions.",15));
 button("Enable / review notification access",()->{try{startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));}catch(RuntimeException e){Toast.makeText(this,"Open Android Settings → Notification access",Toast.LENGTH_LONG).show();}});
 button("Open Audio Mini",()->UtilityPopupManager.open(this,"audio"));
 body.addView(label("Last Played is saved locally across stops and restarts. Saved information is labeled; without a live session, open the remembered app to resume there. No automatic playback or exact-position restoration is promised. Autoplay is not included in this release.",14));
 body.addView(label("Radio target: "+appLabel(repo.str("radioApp",""))+"\nA configured radio APK is a launch target, not proof of active playback.",15));
 button("Choose Radio target APK",()->AppPicker.show(this,"Choose installed Radio app",id->{repo.set("radioApp",id);build();}));
 button("Clear Radio target",()->{repo.set("radioApp","");build();});
 button("Artwork wallpaper: "+(repo.flag("audioWallpaper",true)?"ON":"OFF"),()->{repo.set("audioWallpaper",!repo.flag("audioWallpaper",true));build();});
 body.addView(label("Artwork transitions are disabled in this build. Artwork loads at bounded size off the UI thread. Phone/ZLink sessions are not a target; unidentified bridge sessions cannot be reliably classified on all firmware.",14));
 body.addView(label("When wireless Android Auto is active, your head unit's Wi-Fi internet connection may become unavailable. Streaming apps installed on the head unit may need an internet connection.",15));
 section("Notepad Mini");
 button("Open Notepad Mini",()->UtilityPopupManager.open(this,"notes"));
 body.addView(label("Notepad uses your selected Android keyboard. Confirm parked, tap the note field, then type or use the keyboard's microphone if available. Keyboard voice input, permissions, internet and offline support are managed by the keyboard/provider. DashHopper does not record audio or manage playback for dictation. Tap Save to keep your note locally.",14));
 section("Calendar access");
 button("Enable calendar event reading",()->requestPermissions(new String[]{android.Manifest.permission.READ_CALENDAR},704));
 button("Enable Add Event (read/write)",()->requestPermissions(new String[]{android.Manifest.permission.READ_CALENDAR,android.Manifest.permission.WRITE_CALENDAR},705));
 button("Open calendar popup",()->UtilityPopupManager.open(this,"calendar"));
 button("Open weather popup",()->UtilityPopupManager.open(this,"weather"));
 body.addView(label("Local calendar works without permission/accounts. Event sync comes from Android's configured calendar providers; Add Event needs a writable calendar. No event text is logged.",14));
 section("Automatic dock collapse");
 choice("Dock auto-hide","autoCollapseChoice",new String[]{"on","off"},new String[]{"Auto-hide after idle delay","Stay expanded until collapsed manually"},repo.flag("autoCollapse",true)?"on":"off");
 slider("Idle delay (seconds; 0 disables timed collapse)","autoDelay",0,15,3);
 body.addView(label("Auto-hide applies over all apps. Touch, scrolling and open dock menus pause the timer. Set zero seconds or disable auto-hide to keep the dock expanded. The collapse button always works; the draggable handle restores it.",14));
 if(weatherSetup!=null)weatherSetup.close();weatherSetup=new WeatherSetup(this);weatherSetup.add();
 section("Dock layout");choice("Edge","edge",new String[]{"bottom","top","left","right"},new String[]{"Bottom","Top","Left","Right"},"bottom");
 slider("Top / bottom thickness (dp)","horizontalDp",88,160,96);slider("Left / right thickness (dp)","verticalDp",120,220,144);
 body.addView(label("Thickness is also capped at one third of the current screen dimension. System bars may reduce the available area.",14));
 choice("Handle appearance","handleStyle",new String[]{"arrow","bar"},new String[]{"Arrow","Bar"},"arrow");
 slider("Handle opacity (%)","handleOpacity",55,100,90);
 choice("Handle color","handlePalette",new String[]{"teal","dark","blue"},new String[]{"Teal","Dark","Blue"},"teal");
 section("Color and transparency");
 LinearLayout palette=new LinearLayout(this);int[] colors={0xff132331,0xff000000,0xfff0f4f5,0xff164b60,0xff38305b,0xff5a2929};for(int col:colors){Button b=new Button(this);b.setText("●");b.setTextColor(col);b.setContentDescription(String.format("Use background #%06X",col&0xffffff));b.setOnClickListener(w->{repo.set("color",col);build();});palette.addView(b,new LinearLayout.LayoutParams(0,dp(48),1));}body.addView(palette);
 EditText hex=new EditText(this);hex.setSingleLine(true);hex.setTextColor(Color.WHITE);hex.setText(String.format("#%06X",repo.num("color",0xff132331)&0xffffff));hex.setHint("#RRGGBB");body.addView(hex);
 button("Apply custom background color",()->{try{String s=hex.getText().toString().trim();if(!s.startsWith("#"))s="#"+s;if(s.length()!=7&&s.length()!=9)throw new IllegalArgumentException();int col=Color.parseColor(s);repo.set("color",col|0xff000000);if(s.length()==9)repo.set("opacity",Math.max(55,Color.alpha(col)*100/255));build();}catch(Exception e){hex.setError("Use #RRGGBB or #AARRGGBB");}});
 slider("Background opacity (%) — minimum 55 for visibility","opacity",55,100,92);
 TextView preview=label("Preview  ⌂ Home        ⚙        10:15",18);int col=repo.num("color",0xff132331);preview.setBackgroundColor((repo.num("opacity",92)*255/100<<24)|(col&0xffffff));String fg=repo.str("foreground","auto");preview.setTextColor(fg.equals("dark")?Color.BLACK:fg.equals("light")?Color.WHITE:(Color.red(col)*.299+Color.green(col)*.587+Color.blue(col)*.114<155?Color.WHITE:Color.BLACK));body.addView(preview);
 choice("Foreground","foreground",new String[]{"auto","light","dark"},new String[]{"Automatic","Light","Dark"},"auto");
 choice("Clock format","clock",new String[]{"system","12","24"},new String[]{"Follow Android","12 hour","24 hour"},"system");
 section("App shortcuts");body.addView(label(repo.shortcuts().size()+" shortcuts • changes save immediately. Selecting an app here only pins it; it does not launch it.",15));button("Add installed app",()->picker(false));button("Edit shortcut order / remove",this::edit);button("Save changes and return",this::finish);
 section("Recovery and about");button("Reset to defaults",()->new AlertDialog.Builder(this).setTitle("Reset DashHopper?").setMessage("Clears dock preferences, Home/HVAC assignments, shortcuts, weather setup and appearance; stops the dock. Diagnostic installation ID is retained.").setNegativeButton("Cancel",null).setPositiveButton("Reset",(d,w)->{DockController.stop(this);repo.reset();build();}).show());
 section("About DashHopper");ImageView aboutIcon=new ImageView(this);aboutIcon.setImageDrawable(getApplicationInfo().loadIcon(getPackageManager()));aboutIcon.setContentDescription("DashHopper app icon");body.addView(aboutIcon,new LinearLayout.LayoutParams(dp(64),dp(64)));
 body.addView(label("DashHopper 2.1.2 • com.jmadsen.dashhop\nCreated by Jim Madsen\nJIM — Jim’s Infotainment Management\nTempTango (formerly JHS) remains a separate app.",15));
 section("Diagnostics");
 android.content.pm.ResolveInfo resolved=getPackageManager().resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
 body.addView(label("Diagnostic device ID: "+Diagnostics.id,14));
 button("Test HVAC Quick Control",()->{HvacIntegration.open(this);if(hvacTestReport!=null)hvacTestReport.setText(repo.str("hvacLastReport","No request recorded."));});
 hvacTestReport=label(repo.str("hvacLastReport","No HVAC quick-control test recorded yet."),14);hvacTestReport.setTextIsSelectable(true);body.addView(hvacTestReport);
 button("Export diagnostic log",this::exportLog);

 button("Set parked-only app classification",this::classifyApp);
 body.addView(label("Default Home: "+(resolved==null?"Unknown":resolved.activityInfo.packageName)+"\nOverlay: "+(Settings.canDrawOverlays(this)?"Allowed":"Not allowed")+"\nService: "+(running()?"Running":"Stopped")+"\nMode: standalone floating dock\nCamera / call UI: handled by the vehicle system\nPhone / account integration: not connected",14));
 body.addView(label("DashHopper launches apps normally. Its dock overlays apps and does not reserve display space or resize app windows. Hold Home for app launching, Settings, HVAC setup or Stop.\n\nTEST BUILD: camera/call priority, protected apps and ACC recovery require device testing. This build has no Accessibility service and does not inspect other app windows. System Home remains the fallback if overlays are blocked. DashHopper never changes vehicle services or CANBUS settings. No phone link or AI assistant is included.\n\nCalendar and weather are independent native utility popups. HVAC quick UI uses the public Intent contract with a compatible provider; the selected app remains independently installed.",14));
 scroll.post(()->scroll.scrollTo(0,previousY));
 }
 String appLabel(String id){AppCatalogRepository.App a=new AppCatalogRepository(this).get(id);return a==null?"Not assigned / unavailable":a.label;}
 void chooseHvac(){AppPicker.showHvac(this,"Choose HVAC application",id->{repo.set("hvacApp",id);build();});}

 void exportLog(){Intent create=new Intent(Intent.ACTION_CREATE_DOCUMENT).setType("text/plain").addCategory(Intent.CATEGORY_OPENABLE).putExtra(Intent.EXTRA_TITLE,"DashHopper_"+Diagnostics.id+"_"+System.currentTimeMillis()+".log");try{startActivityForResult(create,703);}catch(Exception e){Diagnostics.error("log_export_picker_unavailable",e);Toast.makeText(this,"No document saver available on this device",Toast.LENGTH_LONG).show();}}
 protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==703&&result==RESULT_OK&&data!=null&&data.getData()!=null){android.net.Uri uri=data.getData();new Thread(()->{try(java.io.OutputStream out=getContentResolver().openOutputStream(uri)){if(out==null)throw new java.io.IOException("No output stream");Diagnostics.export(out);runOnUiThread(()->Toast.makeText(this,"Diagnostic log saved",Toast.LENGTH_LONG).show());}catch(Exception e){Diagnostics.error("log_export_failed",e);runOnUiThread(()->Toast.makeText(this,"Unable to save log",Toast.LENGTH_LONG).show());}},"DashHopper-log-export").start();}}
 public void onRequestPermissionsResult(int request,String[] permissions,int[] results){super.onRequestPermissionsResult(request,permissions,results);if(request==704||request==705){build();return;}if(request==701){if(checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)==android.content.pm.PackageManager.PERMISSION_GRANTED||checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)==android.content.pm.PackageManager.PERMISSION_GRANTED){pendingLocation=true;getWindow().getDecorView().post(this::runPendingLocation);}else Toast.makeText(this,"Location denied; choose city or coordinates",Toast.LENGTH_LONG).show();}}
 void runPendingLocation(){if(pendingLocation&&resumed){pendingLocation=false;if(weatherSetup==null)weatherSetup=new WeatherSetup(this);weatherSetup.deviceLocation();}}

 protected void onDestroy(){if(weatherSetup!=null)weatherSetup.close();super.onDestroy();}
 void classifyApp(){AppPicker.show(this,"Choose app classification",id->new AlertDialog.Builder(this).setTitle("DashHopper launch restriction").setMessage("Parked-only apps remain visible. This build has no verified parked-state provider, so assigning Parked Only blocks DashHopper launches until you remove the designation. Other apps' independent launches are not controlled.").setNegativeButton("Cancel",null).setNeutralButton("Ordinary app",(d,w)->repo.set("parkedOnly."+id,false)).setPositiveButton("Parked Only",(d,w)->repo.set("parkedOnly."+id,true)).show());}
 void choice(String name,String key,String[] values,String[] labels,String def){Button b=new Button(this);int n=Arrays.asList(values).indexOf(repo.str(key,def));b.setText(name+": "+labels[Math.max(0,n)]);b.setAllCaps(false);b.setOnClickListener(w->new AlertDialog.Builder(this).setTitle(name).setSingleChoiceItems(labels,Math.max(0,Arrays.asList(values).indexOf(repo.str(key,def))),(d,which)->{repo.set(key,values[which]);if(key.equals("autoCollapseChoice"))repo.set("autoCollapse",values[which].equals("on"));if(key.equals("handlePalette"))repo.set("handleColor",which==0?0xff23495b:which==1?0xff222222:0xff1e5b9b);d.dismiss();build();}).setNegativeButton("Cancel",null).show());body.addView(b);}
 void slider(String title,String key,int min,int max,int def){TextView l=label(title+": "+repo.num(key,def),15);body.addView(l);SeekBar s=new SeekBar(this);s.setMax(max-min);s.setProgress(Math.max(0,repo.num(key,def)-min));body.addView(s);s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int v,boolean user){l.setText(title+": "+(v+min));if(user)repo.set(key,v+min);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){build();}});}
 void picker(boolean home){LinearLayout layout=new LinearLayout(this);layout.setPadding(dp(12),0,dp(12),0);layout.setOrientation(1);EditText search=new EditText(this);search.setSingleLine(true);search.setHint("Search installed launchable apps");layout.addView(search);ListView list=new ListView(this);layout.addView(list,new LinearLayout.LayoutParams(-1,dp(290)));List<AppCatalogRepository.App> all=new AppCatalogRepository(this).all();List<AppCatalogRepository.App> filtered=new ArrayList<>(all);
 BaseAdapter adapter=new BaseAdapter(){public int getCount(){return filtered.size();}public Object getItem(int i){return filtered.get(i);}public long getItemId(int i){return i;}public View getView(int i,View old,android.view.ViewGroup parent){AppCatalogRepository.App a=filtered.get(i);LinearLayout row=new LinearLayout(DockSettingsActivity.this);row.setPadding(dp(8),dp(6),dp(8),dp(6));row.setGravity(Gravity.CENTER_VERTICAL);ImageView icon=new ImageView(DockSettingsActivity.this);icon.setImageDrawable(a.icon);row.addView(icon,new LinearLayout.LayoutParams(dp(38),dp(38)));TextView t=new TextView(DockSettingsActivity.this);t.setText(a.label+"\n"+a.id);t.setTextSize(14);t.setPadding(dp(10),0,0,0);row.addView(t,new LinearLayout.LayoutParams(0,-2,1));return row;}};list.setAdapter(adapter);
 search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int f){}public void onTextChanged(CharSequence s,int a,int before,int count){filtered.clear();String q=s.toString().toLowerCase(Locale.ROOT);for(AppCatalogRepository.App x:all)if((x.label+" "+x.id).toLowerCase(Locale.ROOT).contains(q))filtered.add(x);adapter.notifyDataSetChanged();}public void afterTextChanged(Editable e){}});
 AlertDialog dialog=new AlertDialog.Builder(this).setTitle(home?"Choose explicit Home destination":"Add app shortcut").setView(layout).setNegativeButton("Cancel",null).create();list.setOnItemClickListener((parent,v,pos,id)->{AppCatalogRepository.App a=filtered.get(pos);if(home){repo.set("home",a.id);repo.set("homeChoicePending",false);}else if(!repo.add(a.id))Toast.makeText(this,"Already on dock",Toast.LENGTH_SHORT).show();dialog.dismiss();build();});dialog.show();
 }
 void edit(){List<String> apps=repo.shortcuts();if(apps.isEmpty()){Toast.makeText(this,"No shortcuts yet. Add an app first.",Toast.LENGTH_LONG).show();return;}String[] labels=new String[apps.size()];for(int n=0;n<apps.size();n++){AppCatalogRepository.App a=new AppCatalogRepository(this).get(apps.get(n));labels[n]=(n+1)+". "+(a==null?"Missing: "+apps.get(n):a.label);}new AlertDialog.Builder(this).setTitle("Select shortcut to edit").setItems(labels,(d,n)->new AlertDialog.Builder(this).setTitle(labels[n]).setItems(new String[]{"Move earlier","Move later","Remove"},(x,op)->{List<String> current=repo.shortcuts();String id=apps.get(n);int at=current.indexOf(id);if(at<0)return;if(op==0&&at>0)Collections.swap(current,at,at-1);if(op==1&&at<current.size()-1)Collections.swap(current,at,at+1);if(op==2)current.remove(at);repo.save(current);build();edit();}).setNegativeButton("Cancel",null).show()).setNegativeButton("Done",null).show();}
}
