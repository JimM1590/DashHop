package com.jmadsen.dashhop;
/** One attempt, generation-guarded. Only final text is autosaved; Cancel invalidates every callback. */
final class DictationPolicy {
 enum State{IDLE,LISTENING,FINALIZING,CANCELED,DONE,ERROR}
 State state=State.IDLE;long epoch,started,silenceAt=-1;boolean manual;
 long begin(long now){epoch++;state=State.LISTENING;started=now;silenceAt=-1;manual=false;return epoch;}
 boolean active(long id){return id==epoch&&(state==State.LISTENING||state==State.FINALIZING);}
 void speech(long id){if(active(id)&&state==State.LISTENING)silenceAt=-1;}
 void ended(long id,long now){if(active(id)&&state==State.LISTENING)silenceAt=now;}
 boolean due(long id,long now){return active(id)&&state==State.LISTENING&&(now-started>=10000||silenceAt>=0&&now-silenceAt>=3000);}
 boolean stop(long id,boolean user){if(!active(id)||state!=State.LISTENING)return false;manual=user;state=State.FINALIZING;return true;}
 boolean finish(long id,boolean ok){if(!active(id))return false;state=ok?State.DONE:State.ERROR;return true;}
 static String finalOutput(String prefix,String recognized,String manualDraft,boolean userSave){String text=recognized==null?"":recognized.trim();if(!text.isEmpty())return prefix==null||prefix.isEmpty()?text:prefix+"\n"+text;return userSave&&manualDraft!=null&&!manualDraft.trim().isEmpty()?manualDraft:null;}
 void cancel(){epoch++;state=State.CANCELED;silenceAt=-1;}
}
