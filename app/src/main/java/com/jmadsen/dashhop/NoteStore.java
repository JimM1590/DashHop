package com.jmadsen.dashhop;
import android.content.*;import android.database.*;import android.database.sqlite.*;import java.util.*;
/** Private SQLite text only. No raw recordings, external storage or content logging. */
final class NoteStore extends SQLiteOpenHelper {
 static final class Note {final String id,text;final long created,modified;Note(String i,String t,long c,long m){id=i;text=t;created=c;modified=m;}}
 NoteStore(Context c){super(c.getApplicationContext(),"notes.db",null,1);}
 public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE notes(id TEXT PRIMARY KEY, text TEXT NOT NULL, created INTEGER NOT NULL, modified INTEGER NOT NULL)");}
 public void onUpgrade(SQLiteDatabase db,int old,int next){throw new IllegalStateException("No destructive note migration defined");}
 List<Note> list(){List<Note> notes=new ArrayList<>();try(Cursor c=getReadableDatabase().query("notes",new String[]{"id","text","created","modified"},null,null,null,null,"modified DESC")){while(c.moveToNext())notes.add(new Note(c.getString(0),c.getString(1),c.getLong(2),c.getLong(3)));}return notes;}
 Note save(String id,String text){String clean=text==null?"":text.trim();if(clean.isEmpty())return null;SQLiteDatabase db=getWritableDatabase();long now=System.currentTimeMillis(),created=now;if(id!=null)try(Cursor c=db.query("notes",new String[]{"created"},"id=?",new String[]{id},null,null,null)){if(c.moveToFirst())created=c.getLong(0);}String key=id==null?UUID.randomUUID().toString():id;ContentValues v=new ContentValues();v.put("id",key);v.put("text",clean);v.put("created",created);v.put("modified",now);if(db.insertWithOnConflict("notes",null,v,SQLiteDatabase.CONFLICT_REPLACE)<0)throw new SQLiteException("Note save rejected");return new Note(key,clean,created,now);}
 void delete(String id){getWritableDatabase().delete("notes","id=?",new String[]{id});}
}
