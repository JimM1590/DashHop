package com.jmadsen.dashhop;
import android.app.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.util.*;
final class AppPicker {
 interface Chosen {void accept(String id);}
 static void show(Activity c,String title,Chosen chosen){show(c,title,chosen,false);}
 static void showHvac(Activity c,String title,Chosen chosen){show(c,title,chosen,true);}
 static void show(Activity c,String title,Chosen chosen,boolean hvac){
 LinearLayout layout=new LinearLayout(c);layout.setOrientation(1);layout.setPadding(16,0,16,0);EditText search=new EditText(c);search.setSingleLine(true);search.setHint("Search apps");layout.addView(search);
 ListView list=new ListView(c);layout.addView(list,new LinearLayout.LayoutParams(-1,DockController.px(c,260)));
 List<AppCatalogRepository.App> all=hvac?new AppCatalogRepository(c).hvacApps():new AppCatalogRepository(c).all(), filtered=new ArrayList<>(all);
 BaseAdapter adapter=new BaseAdapter(){public int getCount(){return filtered.size();}public Object getItem(int p){return filtered.get(p);}public long getItemId(int p){return p;}public View getView(int p,View old,ViewGroup parent){AppCatalogRepository.App a=filtered.get(p);LinearLayout row=new LinearLayout(c);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(8,8,8,8);ImageView icon=new ImageView(c);icon.setImageDrawable(a.icon);row.addView(icon,new LinearLayout.LayoutParams(DockController.px(c,40),DockController.px(c,40)));TextView text=new TextView(c);text.setText(a.label+"\n"+a.id);text.setTextSize(15);text.setPadding(12,0,0,0);row.addView(text,new LinearLayout.LayoutParams(0,-2,1));return row;}};list.setAdapter(adapter);
 search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int n,int f){}public void afterTextChanged(Editable e){}public void onTextChanged(CharSequence s,int a,int b,int n){String q=s.toString().toLowerCase(Locale.ROOT);filtered.clear();for(AppCatalogRepository.App x:all)if((x.label+" "+x.id).toLowerCase(Locale.ROOT).contains(q))filtered.add(x);adapter.notifyDataSetChanged();}});
 AlertDialog dialog=new AlertDialog.Builder(c).setTitle(title).setView(layout).setNegativeButton("Cancel",null).create();list.setOnItemClickListener((parent,v,pos,id)->{String component=filtered.get(pos).id;dialog.dismiss();chosen.accept(component);});dialog.show();
 }
}
