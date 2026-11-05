package com.jmadsen.dashhop;
/** Future transport-neutral source. No companion or pairing is implemented in 0.3. */
final class PhoneWeatherProvider implements WeatherProvider {
 public String source(){return "Phone companion";}public boolean available(){return false;}
 public WeatherData fetch(double lat,double lon,String place){throw new UnsupportedOperationException("Connect / pair phone source not implemented");}
}
