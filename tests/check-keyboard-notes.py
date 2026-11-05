from pathlib import Path
import xml.etree.ElementTree as E
p=Path(__file__).resolve().parents[1];s=p/'app/src/main/java/com/jmadsen/dashhop'
read=lambda name:(s/name).read_text()
a='{http://schemas.android.com/apk/res/android}'
m=E.parse(p/'app/src/main/AndroidManifest.xml').getroot()
assert 'android.permission.RECORD_AUDIO' not in {x.get(a+'name') for x in m.findall('uses-permission')}
assert not (s/'SpeechNoteController.java').exists()
notes=read('NotepadPanel.java');settings=read('DockSettingsActivity.java')
assert 'new EditText' in notes and 'TYPE_TEXT_FLAG_MULTI_LINE' in notes
assert 'store.save(id,value)' in notes and 'hideSoftInputFromWindow' in notes
assert 'SpeechRecognizer' not in notes+settings and 'notesOffline' not in settings
assert 'AudioFocusCoordinator' not in notes and 'RECORD_AUDIO' not in settings
assert 'cancel();editor=null' in notes and 'if(explicit)' in notes
assert 'audio.displayable()' in read('AudioDockItem.java')
assert 'audio::openSaved' in read('AudioPanel.java')
assert 'savedComponent()' in read('AudioSessionRepository.java')
print('PASS: keyboard-only note input, removed recording permission/setup, saved-source dock and Open action wiring. Static only.')
