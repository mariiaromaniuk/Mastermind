package audio;
/*
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

// Audio player for the program. Run on a separate thread to prevent audio from blocking the main program thread
public class AudioPlayer implements Runnable {
    // A Map between a Phrase in the game & an OPENED audio Clip. This prevents pauses in the
    // game that previously occurred due to File IO taking a considerable amount of time.
    private final HashMap<Phrase, Clip> phraseToClip;

    // When this is set to false, the audio player is terminated
    private boolean isActive = true;

    // The Clip that is currently playing or was the last Clip that played in the game
    private Clip activeClip;

    // The remaining phrases that need to be played in the game
    private ArrayList<Phrase> phrasesToPlay = new ArrayList<>();

    // Creates a new AudioPlayer
    // throws LineUnavailableException Thrown when the {@link Clip} in the audio player cannot be started
    public AudioPlayer() throws LineUnavailableException {
        // Initialize the active Clip to an empty Clip object (prevents the need to check for null)
        this.activeClip = AudioSystem.getClip();
        this.phraseToClip = this.getInitializedClipHashMap();
    }

    // Get the Clip associated with a particular Phrase. If an error occurs, return Optional.empty()
    // phrase - the Phrase to get the associated Clip from
    // return: Optional containing the Clip if no error occurred (otherwise, empty())
    private Optional<Clip> getPhraseClip(@NotNull Phrase phrase) {
        try {
            Clip phraseClip = AudioSystem.getClip();
            Optional<InputStream> maybePhraseAudioStream = phrase.getPhraseInputStream();

            if (maybePhraseAudioStream.isEmpty()) {
                System.err.println(String.format("Failed to find phrase audio file: %s!", phrase));
                return Optional.empty();
            }

            BufferedInputStream bufferedInputStream = new BufferedInputStream(maybePhraseAudioStream.get());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);

            // The Clip should be opened so that it can be immediately played.
            phraseClip.open(audioInputStream);

            return Optional.of(phraseClip);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // Loops over all of the stored Phrases and gets an opened Clip to play with each Phrase
    // return: HashMap between a Phrase in the game and an opened audio Clip

    private HashMap<Phrase, Clip> getInitializedClipHashMap() {
        HashMap<Phrase, Clip> phraseToClip = new HashMap<>();
        for (Phrase phrase : Phrase.values()) {
            // Only store in the HashMap if Optional.empty() wasn't returned
            // (meaning an error occurred during initialization).
            this.getPhraseClip(phrase).ifPresent(clip -> phraseToClip.put(phrase, clip));
        }
        return phraseToClip;
    }

    // Plays the first remaining Phrase in the phrasesToPlay instance variable
    private void playPhrase() {
        if (this.phrasesToPlay.isEmpty()) {
            return;
        }

        // Resets the audio stream to the first remaining Phrase & start the clip
        synchronized (this) {
            Phrase phraseToPlay = this.phrasesToPlay.remove(0);
            Clip clipToPlay = this.phraseToClip.get(phraseToPlay);
            if (clipToPlay != null) {
                this.activeClip = clipToPlay;
                clipToPlay.setMicrosecondPosition(0);
                clipToPlay.start();
            }
        }
    }

    // Replaces the phrases to play with a single Phrase
    // phrase The {@link Phrase} to replace the phrases to play with

    public void replacePhraseToPlay(@NotNull Phrase phrase) {
        // Since I'm REPLACING the phrases to play, I need to first stop the running clip
        if (this.activeClip.isRunning()) {
            this.activeClip.stop();
        }
        synchronized (this) {
            this.phrasesToPlay = new ArrayList<>(Collections.singletonList(phrase));
        }
    }

    // Replaces the phrases to play with a list of Phrases
    // phrases - the {@link ArrayList} of {@link Phrase}s to replace the phrases to play with

    public void replacePhraseToPlay(@NotNull ArrayList<Phrase> phrases) {
        // Since I'm REPLACING the phrases to play, I need to first stop the running clip
        if (this.activeClip.isRunning()) {
            this.activeClip.stop();
        }
        synchronized (this) {
            this.phrasesToPlay = phrases;
        }
    }

    // The entry point to the AudioPlayer thread (loops until isActive is set to false)
    @Override
    public void run() {
        // Checking for empty phrases to play makes sure the goodbye message plays
        while (this.isActive || !this.phrasesToPlay.isEmpty()) {
            if (!this.activeClip.isRunning()) {
                this.activeClip.stop();

                synchronized (this) {
                    this.playPhrase();
                }
            }
            // Sleep momentarily to prevent the audio player from skipping over phrases
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Ensures that the goodbye message plays in its entirety
        // noinspection StatementWithEmptyBody
        while (this.activeClip.isRunning()) ;
    }

    // Terminate the audio player (sets the isActive flag to false)
    public void terminateAudioPlayer() {
        this.isActive = false;
    }
}
*/