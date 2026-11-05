package com.jmadsen.dashhop;
import java.io.*;import java.nio.charset.StandardCharsets;import java.nio.file.*;import java.util.*;import org.json.*;
/** One bounded atomic record. Called only from the repository's serial storage worker. */
final class LastPlayedStore {
 static boolean shouldRemember(boolean playing,boolean loaded,String savedPackage,String currentPackage){return playing||(!savedPackage.isEmpty()?savedPackage.equals(currentPackage):loaded);}
 static final int MAX_ART=1024*1024,MAX_FILE=1500000;
 static final class Record {
  final String pkg,source,title,detail;final byte[] art;
  Record(String p,String s,String t,String d,byte[] a){pkg=bounded(p,512);source=bounded(s,1024);title=bounded(t,4096);detail=bounded(d,4096);art=a!=null&&a.length<=MAX_ART?a.clone():null;}
  static String bounded(String s,int max){return s==null?"":s.substring(0,Math.min(max,s.length()));}
 }
 final File file;
 LastPlayedStore(File directory){file=new File(directory,"audio-last-played-v1.json");}
 Record read() throws IOException {if(!file.exists())return null;if(file.length()>MAX_FILE)throw new IOException("Saved audio record exceeds limit");try{JSONObject j=new JSONObject(new String(Files.readAllBytes(file.toPath()),StandardCharsets.UTF_8));if(j.optInt("schema",0)!=1)return null;String pkg=j.optString("package","");if(pkg.isEmpty())return null;byte[] art=null;try{String encoded=j.optString("art","");if(!encoded.isEmpty())art=Base64.getDecoder().decode(encoded);}catch(IllegalArgumentException ignored){}return new Record(pkg,j.optString("source",pkg),j.optString("title",""),j.optString("detail",""),art);}catch(JSONException e){throw new IOException("Invalid saved audio record",e);}}
 void write(Record r) throws IOException {if(r==null||r.pkg.isEmpty())return;File parent=file.getParentFile();if(!parent.exists()&&!parent.mkdirs())throw new IOException("Cannot create audio directory");File temp=new File(parent,file.getName()+".tmp");try{JSONObject j=new JSONObject();j.put("schema",1);j.put("package",r.pkg);j.put("source",r.source);j.put("title",r.title);j.put("detail",r.detail);j.put("art",r.art==null?"":Base64.getEncoder().encodeToString(r.art));byte[] bytes=j.toString().getBytes(StandardCharsets.UTF_8);if(bytes.length>MAX_FILE)throw new IOException("Saved audio record exceeds limit");try(FileOutputStream out=new FileOutputStream(temp)){out.write(bytes);out.getFD().sync();}try{Files.move(temp.toPath(),file.toPath(),StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}catch(AtomicMoveNotSupportedException e){Files.move(temp.toPath(),file.toPath(),StandardCopyOption.REPLACE_EXISTING);}}catch(JSONException e){throw new IOException(e);}finally{temp.delete();}}
}
