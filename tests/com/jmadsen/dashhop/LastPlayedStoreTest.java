package com.jmadsen.dashhop;
import java.io.*;import java.nio.file.*;import java.util.*;
public final class LastPlayedStoreTest {
 static int checks;static void check(boolean value){checks++;if(!value)throw new AssertionError("check "+checks);}
 public static void main(String[] args)throws Exception{File dir=Files.createTempDirectory("dashhop-last-played").toFile();LastPlayedStore store=new LastPlayedStore(dir);
 try{check(LastPlayedStore.shouldRemember(true,false,"","audio"));check(!LastPlayedStore.shouldRemember(false,false,"","old-paused"));check(LastPlayedStore.shouldRemember(false,true,"","paused"));check(!LastPlayedStore.shouldRemember(false,true,"radio","old-paused"));check(LastPlayedStore.shouldRemember(false,true,"radio","radio"));check(LastPlayedStore.shouldRemember(true,true,"radio","books"));check(!LastPlayedStore.shouldRemember(false,false,"radio","books"));check(store.read()==null);byte[] art=new byte[]{1,2,3,4};store.write(new LastPlayedStore.Record("com.example.audio","Radio","Station \"One\"","Artist\nAlbum",art));LastPlayedStore.Record r=new LastPlayedStore(dir).read();check(r.pkg.equals("com.example.audio"));check(r.source.equals("Radio"));check(r.title.equals("Station \"One\""));check(r.detail.equals("Artist\nAlbum"));check(Arrays.equals(r.art,art));
 store.write(new LastPlayedStore.Record("com.example.book","Books","Chapter 2","Author",null));r=new LastPlayedStore(dir).read();check(r.pkg.equals("com.example.book"));check(r.art==null);check(r.title.equals("Chapter 2"));check(!new File(dir,"audio-last-played-v1.json.tmp").exists());
 store.write(new LastPlayedStore.Record("","","","",null));check(store.read().pkg.equals("com.example.book"));
 String huge=String.join("",Collections.nCopies(10000,"x"));r=new LastPlayedStore.Record(huge,huge,huge,huge,new byte[LastPlayedStore.MAX_ART+1]);check(r.pkg.length()==512);check(r.source.length()==1024);check(r.title.length()==4096);check(r.detail.length()==4096);check(r.art==null);
 Files.write(store.file.toPath(),"{broken".getBytes("UTF-8"));try{store.read();throw new AssertionError("corruption accepted");}catch(IOException expected){checks++;}
 Files.write(store.file.toPath(),"{\"schema\":2,\"package\":\"future\"}".getBytes("UTF-8"));check(store.read()==null);
 Files.write(store.file.toPath(),"{\"schema\":1,\"package\":\"audio\",\"art\":\"!!!\"}".getBytes("UTF-8"));r=store.read();check(r.pkg.equals("audio"));check(r.art==null);
 Files.write(store.file.toPath(),new byte[LastPlayedStore.MAX_FILE+1]);try{store.read();throw new AssertionError("oversize accepted");}catch(IOException expected){checks++;}
 store.write(new LastPlayedStore.Record("recovered","App","Title","",null));check(new LastPlayedStore(dir).read().title.equals("Title"));
 byte[] maxArt=new byte[LastPlayedStore.MAX_ART];maxArt[maxArt.length-1]=42;store.write(new LastPlayedStore.Record("max-art","App","Title","",maxArt));check(Arrays.equals(new LastPlayedStore(dir).read().art,maxArt));
 System.out.println("PASS: "+checks+" Last Played disk roundtrip, replacement, artwork, missing/corrupt/oversize recovery and bounds assertions. Real host file I/O; no Android runtime.");
 }finally{for(File f:dir.listFiles())f.delete();dir.delete();}}
}
