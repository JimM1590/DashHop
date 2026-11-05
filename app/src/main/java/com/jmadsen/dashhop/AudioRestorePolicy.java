package com.jmadsen.dashhop;
final class AudioRestorePolicy {
 static boolean restore(boolean previouslyPlaying,boolean pausedByUs,boolean pauseObserved,boolean sameSession,boolean stillPaused,boolean replacedOrStopped,boolean focusInterrupted){return previouslyPlaying&&pausedByUs&&pauseObserved&&sameSession&&stillPaused&&!replacedOrStopped&&!focusInterrupted;}
}
