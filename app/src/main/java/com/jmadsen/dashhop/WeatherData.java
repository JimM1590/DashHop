package com.jmadsen.dashhop;
import org.json.*;import java.time.*;
final class WeatherData {
 final JSONObject json;final String place,timezone;final long fetched;final double temperature;final int code;
 WeatherData(JSONObject j,String p,long fetched)throws JSONException{if(j.optBoolean("error",false))throw new JSONException("Provider rejected request");JSONObject current=j.getJSONObject("current");temperature=current.getDouble("temperature_2m");if(Double.isNaN(temperature)||Double.isInfinite(temperature))throw new JSONException("Invalid temperature");code=current.getInt("weather_code");j.getJSONObject("hourly").getJSONArray("time");j.getJSONObject("daily").getJSONArray("time");json=j;place=p;this.fetched=fetched;timezone=j.optString("timezone","UTC");}
 boolean stale(long now){return now<fetched||now-fetched>=1800000L;}
 static long time(Object value,String zone){if(value instanceof Number)return ((Number)value).longValue()*1000;String s=value.toString();ZoneId z;try{z=ZoneId.of(zone);}catch(Exception e){z=ZoneOffset.UTC;}try{return LocalDateTime.parse(s).atZone(z).toInstant().toEpochMilli();}catch(Exception ignored){return LocalDate.parse(s).atStartOfDay(z).toInstant().toEpochMilli();}}
 static String degrees(double c,boolean fahrenheit){return Double.isNaN(c)?"—":Math.round(fahrenheit?c*9/5+32:c)+"°"+(fahrenheit?"F":"C");}
 static String condition(int code){if(code==0)return "Clear";if(code<=3)return "Cloudy";if(code==45||code==48)return "Fog";if(code>=51&&code<=67)return "Rain / drizzle";if(code>=71&&code<=77)return "Snow";if(code>=80&&code<=82)return "Rain showers";if(code==85||code==86)return "Snow showers";if(code>=95)return "Thunderstorms";return "Unknown";}
 static String icon(int code){return code==0?"☀":code<=3?"☁":code>=71&&code<=77||code==85||code==86?"❄":code>=95?"ϟ":"☂";}
}
