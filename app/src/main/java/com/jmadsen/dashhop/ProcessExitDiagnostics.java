package com.jmadsen.dashhop;
import android.app.*;import android.content.*;import android.os.*;import java.util.*;
/** Android 11 own-process exit history, not system log access or a crash diagnosis by guesswork. */
final class ProcessExitDiagnostics {
 static void record(Context c){if(Build.VERSION.SDK_INT<30)return;try{ActivityManager am=c.getSystemService(ActivityManager.class);if(am==null)return;SharedPreferences prefs=c.getSharedPreferences("process_exit_diagnostics",0);long seen=prefs.getLong("last_timestamp",0),latest=seen;for(ApplicationExitInfo e:am.getHistoricalProcessExitReasons(c.getPackageName(),0,5)){if(e.getTimestamp()<=seen)continue;Map<String,Object> fields=new LinkedHashMap<>();fields.put("exit_timestamp_ms",e.getTimestamp());fields.put("exit_reason_code",e.getReason());fields.put("exit_status",e.getStatus());fields.put("exit_importance",e.getImportance());fields.put("exit_pss_kb",e.getPss());fields.put("exit_rss_kb",e.getRss());Diagnostics.operation("previous_process_exit",fields);latest=Math.max(latest,e.getTimestamp());}prefs.edit().putLong("last_timestamp",latest).apply();}catch(RuntimeException e){Diagnostics.error("process_exit_history_unavailable",e);}}

}
