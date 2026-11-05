package com.jmadsen.dashhop;
import org.json.*;import java.util.*;
/** Only explicit allow-listed profile/status fields are supplied by Diagnostics. */
final class DiagnosticEnvelope {
 static JSONObject header(String id,String session,String timestamp,String timezone,Map<String,Object> profile,Map<String,Object> status)throws JSONException{
 JSONObject o=new JSONObject();o.put("type","dashhop_log_header");o.put("log_format",1);o.put("device_id",id);o.put("identity_scope","installation; retained on updates, reset on app-data removal");o.put("session_id",session);o.put("timestamp",timestamp);o.put("timezone",timezone);o.put("app_version","2.1.2");o.put("version_code",20102);o.put("head_unit",new JSONObject(profile));o.put("status",new JSONObject(status));return o;}
}
