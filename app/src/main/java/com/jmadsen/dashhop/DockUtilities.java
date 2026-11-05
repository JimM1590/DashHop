package com.jmadsen.dashhop;
/** Missing visibility preferences preserve 2.0.1 defaults without rewriting configuration. */
final class DockUtilities {
 static final String HVAC="utility.hvac.enabled",WEATHER="utility.weather.enabled",CLOCK="utility.clock.enabled";
 static final String AUDIO="utility.audio.enabled",NOTES="utility.notes.enabled";
 static boolean hvac(boolean enabled,boolean providerAvailable){return enabled&&providerAvailable;}
}
