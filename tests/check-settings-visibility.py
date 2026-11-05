#!/usr/bin/env python3
"""Regression guard for invisible Settings. Static resource/entry checks, not Android UI tests."""
from pathlib import Path
import xml.etree.ElementTree as E
root=Path(__file__).resolve().parents[1]
a='{http://schemas.android.com/apk/res/android}'
m=E.parse(root/'app/src/main/AndroidManifest.xml').getroot()
activities={x.get(a+'name'):x for x in m.findall('application/activity')}
assert activities['.DockSettingsActivity'].get(a+'theme')=='@style/DashHopperSettingsTheme','Settings must not inherit a no-display manifest theme'
style=next(x for x in E.parse(root/'app/src/main/res/values/styles.xml').getroot() if x.get('name')=='DashHopperSettingsTheme')
assert style.get('parent')=='android:style/Theme.Material.NoActionBar'
values={x.get('name'):x.text for x in style}
assert values['android:windowNoDisplay']=='false','Visible theme must explicitly clear no-display state'
assert values['android:windowIsTranslucent']=='false'
assert values['android:windowDisablePreview']=='true'
source=root/'app/src/main/java/com/jmadsen/dashhop'
s=(source/'DockSettingsActivity.java').read_text()
assert 'showingSettings?R.style.DashHopperSettingsTheme:android.R.style.Theme_NoDisplay' in s
assert s.index('setTheme(')<s.index('super.onCreate(b)'),'Choose visible theme before Activity initialization'
assert activities['.DockStartActivity'].get(a+'theme')=='@android:style/Theme.NoDisplay'
assert activities['.DockStartActivity'].get(a+'noHistory')=='true'
assert [x.get(a+'name') for x in m.findall('application/service')]==['.AudioSessionBridge','.DockOverlayService']
for file in ['AppLaunchManager.java','DockOverlayService.java','DockRecoveryReceiver.java']:
 assert 'DockStartup.SHOW_SETTINGS,true' in (source/file).read_text(),file+' must explicitly request visible Settings'
assert 'AppLaunchManager.settings(c,"")' in (source/'DockView.java').read_text(),'Gear must request Settings'
print('PASS: Settings visibility regression guard; manifest/style/explicit routes and silent launcher verified statically. Android UI appearance requires head-unit validation.')
