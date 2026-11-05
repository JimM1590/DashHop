package com.jmadsen.dashhop;
import android.content.SharedPreferences;
import java.util.UUID;
final class DiagnosticIdentity {
 static String get(SharedPreferences p){String value=p.getString("device_id","");try{UUID.fromString(value);return value;}catch(IllegalArgumentException ignored){}
 value=UUID.randomUUID().toString();if(!p.edit().putString("device_id",value).commit())throw new IllegalStateException("Diagnostic ID could not be saved");return value;}
}
