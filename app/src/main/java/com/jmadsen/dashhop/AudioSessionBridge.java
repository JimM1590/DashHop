package com.jmadsen.dashhop;
import android.service.notification.NotificationListenerService;
/** System-bound permission bridge, not a competing foreground service. No notification text is read/logged. */
public final class AudioSessionBridge extends NotificationListenerService {
 public void onListenerConnected(){super.onListenerConnected();AudioSessionRepository.get(this).refresh();Diagnostics.info("media_listener_connected");}
 public void onListenerDisconnected(){AudioSessionRepository repo=AudioSessionRepository.get(this);repo.disconnect();repo.notice="Notification listener disconnected. Review notification access in Settings.";repo.emit();Diagnostics.info("media_listener_disconnected");super.onListenerDisconnected();}
}
