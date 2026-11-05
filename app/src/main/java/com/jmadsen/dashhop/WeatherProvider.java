package com.jmadsen.dashhop;
interface WeatherProvider {String source();boolean available();WeatherData fetch(double lat,double lon,String place)throws Exception;}
