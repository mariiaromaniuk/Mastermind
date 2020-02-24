package audio;
/*
import org.jetbrains.annotations.NotNull;
import audio.Phrase;

import java.util.ArrayList;

// Class to call into the threaded audio player
public class AudioPlayerExecutor {

    // The program's audio player
    private final AudioPlayer audioPlayer;

    // Creates a dummy AudioPlayerExecutor for testing purposes
    public AudioPlayerExecutor() {
        this.audioPlayer = null;
    }

    // Creates a new AudioPlayerExecutor
    // audioPlayer - the program's audio player

    public AudioPlayerExecutor(@NotNull AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    // Replaces the Phrases to play for the audio player w/ a single Phrase & prints the Phrase text to stdout
    // phrase - the Phrase to replace in the program's audio player

    public void replacePhraseAndPrint(@NotNull Phrase phrase) {
        System.out.println(phrase.getPhraseValue());
        this.audioPlayer.replacePhraseToPlay(phrase);
    }

    // Replaces the Phrases to play for the audio player w/ a list of Phrases & prints their text to stdout
    // phrases The list of Phrases to replace in the program's audio player

    public void replacePhraseAndPrint(@NotNull ArrayList<Phrase> phrases) {

        // Prints all of the phrases on a single line, separated with a space (" ")
        ArrayList<String> phraseStringList = new ArrayList<>();
        phrases.forEach(phrase -> phraseStringList.add(phrase.getPhraseValue()));
        System.out.println(String.join(" ", phraseStringList));

        this.audioPlayer.replacePhraseToPlay(phrases);
    }

    //Terminates the audio player by just calling into the same method in the audio player instance variable
    public void terminateAudioPlayer() {
        this.audioPlayer.terminateAudioPlayer();
    }
}
*/