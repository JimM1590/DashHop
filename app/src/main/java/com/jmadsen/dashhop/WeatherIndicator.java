package com.jmadsen.dashhop;
/** Never substitutes a real-looking condition when no matching observation exists. */
final class WeatherIndicator {
 static String icon(WeatherData data,boolean matchesLocation,boolean failed,long now){
  if(data==null||!matchesLocation)return "?";
  return (failed?"!":data.stale(now)?"◷":"")+WeatherData.icon(data.code);
 }
 static String status(WeatherData data,boolean matchesLocation,boolean failed,long now){
  if(data==null||!matchesLocation)return "Weather unavailable. Open weather for setup or status.";
  return (failed?"Cached weather; refresh failed. ":data.stale(now)?"Stale cached weather. ":"Current weather. ")+WeatherData.condition(data.code)+". Open weather for update time and details.";
 }
}
