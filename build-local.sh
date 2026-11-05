#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_JAR:?Set ANDROID_JAR to a compile SDK android.jar (35 tested)}"
: "${BUILD_TOOLS:?Set BUILD_TOOLS to Android build-tools directory (35.0.0 tested)}"
python3 - <<'PYCLEAN'
from pathlib import Path
import shutil
p=Path('build')
if p.exists(): shutil.rmtree(p)
for child in ['classes','dex','gen']: (p/child).mkdir(parents=True,exist_ok=True)
PYCLEAN
"$BUILD_TOOLS/aapt2" compile --dir app/src/main/res -o build/resources.zip
python3 - <<'PYMANIFEST'
from pathlib import Path
s=Path('app/src/main/AndroidManifest.xml').read_text().replace('<manifest ', '<manifest package="com.jmadsen.dashhop" ',1)
Path('build/AndroidManifest.xml').write_text(s)
PYMANIFEST
"$BUILD_TOOLS/aapt2" link -o build/unsigned.apk --manifest build/AndroidManifest.xml -I "$ANDROID_JAR" --java build/gen build/resources.zip
find app/src/main/java build/gen -name '*.java' > build/sources.txt
java -m jdk.compiler/com.sun.tools.javac.Main -source 8 -target 8 -bootclasspath "$ANDROID_JAR:$BUILD_TOOLS/core-lambda-stubs.jar" -d build/classes @build/sources.txt
java -m jdk.jartool/sun.tools.jar.Main cf build/classes.jar -C build/classes .
"$BUILD_TOOLS/d8" --lib "$ANDROID_JAR" --min-api 26 --output build/dex build/classes.jar
(cd build/dex && zip -q ../unsigned.apk classes.dex)
"$BUILD_TOOLS/zipalign" -f 4 build/unsigned.apk build/DashHopper_2.1.2-unsigned.apk
"$BUILD_TOOLS/zipalign" -c 4 build/DashHopper_2.1.2-unsigned.apk
printf '%s\n' 'Built build/DashHopper_2.1.2-unsigned.apk'
