#!/usr/bin/env python3
"""Static regression boundaries, not claims of Android runtime behavior."""
from pathlib import Path
import xml.etree.ElementTree as E
r=Path(__file__).resolve().parents[1];src=r/'app/src/main/java/com/jmadsen/dashhop';read=lambda n:(src/n).read_text();a='{http://schemas.android.com/apk/res/android}';m=E.parse(r/'app/src/main/AndroidManifest.xml').getroot()
assert m.get(a+'versionCode')=='20102' and m.get(a+'versionName')=='2.1.2'
services={x.get(a+'name'):x for x in m.findall('application/service')}
assert set(services)=={'.DockOverlayService','.AudioSessionBridge'}
assert services['.AudioSessionBridge'].get(a+'permission')=='android.permission.BIND_NOTIFICATION_LISTENER_SERVICE'
assert services['.AudioSessionBridge'].find('intent-filter/action').get(a+'name')=='android.service.notification.NotificationListenerService'
permissions={x.get(a+'name') for x in m.findall('uses-permission')}
assert 'android.permission.RECORD_AUDIO' not in permissions and 'android.permission.ACCESS_NETWORK_STATE' in permissions
assert 'android.permission.MEDIA_CONTENT_CONTROL' not in permissions
assert 'android.speech.RecognitionService' not in [x.get(a+'name') for x in m.findall('queries/intent/action')]
for x in m.findall('application/activity/intent-filter/category'):assert x.get(a+'name')!='android.intent.category.HOME'
item=read('AudioDockItem.java');assert 'DockOverlayService' not in item and 'AppLaunchManager.launch' not in item
assert 'touched.getAsBoolean()' in item and 'removeCallbacks(retry)' in item
repo=read('AudioSessionRepository.java');assert 'getActiveSessions(' in repo and 'registerCallback' in repo and 'unregisterCallback' in repo and 'ArrayBlockingQueue<Runnable>(1)' in repo
assert 'NET_CAPABILITY_VALIDATED' in repo and '.getStringArrayList' not in repo
focus=read('AudioFocusCoordinator.java');assert 'AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE' in focus and 'AudioRestorePolicy.restore' in focus and 'setStreamMute' not in focus
note=read('NoteStore.java');assert 'notes.db' in note and 'SQLiteOpenHelper' in note and 'UUID.randomUUID' in note
popup=read('UtilityPopupManager.java');assert 'type.equals("audio")' in popup and 'type.equals("notes")' in popup and 'putFloat(key+"w"' in popup
assert 'cleanup=view::close' in popup and 'AudioFocusCoordinator.get(c).end(false)' in popup
print('PASS: 2.1.2 manifest, media observer/update boundaries, keyboard input, persistence schema and native toolbox integration checks. Static only.')
