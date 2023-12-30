package AdminGUI;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class AudioFeedback {
    private static final String VOICENAME_kevin = "kevin";

    public static void speak(String text) {
        Voice voice;
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(VOICENAME_kevin);
        if (voice != null) {
            voice.allocate();
            try {
                voice.speak(text);
            } finally {
                voice.deallocate();
            }
        }
    }

    public static void main(String[] args) {
        AudioFeedback.speak("Hello, this is a test of the text to speech conversion");
    }
}