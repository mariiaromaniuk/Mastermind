package audio;
/*
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import audio.Phrase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

// Logic to create & delete audio files using Google Cloud Text-to-Speech's Java API
public class AudioFileBuilder {

    // The directory that contains all of the Phrases in the program
    private final File phraseDirectory;

    // Creates a new AudioFileBuilder. Creates the phrase directory if it doesn't already exist.
    private AudioFileBuilder() {
        this.phraseDirectory = new File(Phrase.PHRASE_FILES_DIRECTORY.toString());
        if (!this.phraseDirectory.exists()) {
            if (!this.phraseDirectory.mkdirs()) {
                throw new IllegalArgumentException("Could not create the directory to store the audio phrases!");
            }
        }
    }

    // The entry point to the audio file builder
    // args - command line arguments that were passed by the user

    public static void main(String[] args) {
        System.out.println("Generating & deleting audio files...");

        AudioFileBuilder audioFileBuilder = new AudioFileBuilder();
        audioFileBuilder.createPhraseAudioFiles();
        audioFileBuilder.deleteOldPhraseAudioFiles();

        System.out.println("Completed audio file generation & deletion!");
    }

    // Deletes old Phrase audio files that are no longer needed in the program
    private void deleteOldPhraseAudioFiles() {
        File[] filesInDirectory = this.phraseDirectory.listFiles();
        if (filesInDirectory == null) {
            return;
        }
        ArrayList<File> audioFiles = new ArrayList<>(Arrays.asList(filesInDirectory));

        // If a given Phrase uses an audio file, I don't want to delete it
        // (so remove it from the list of audio files to delete)
        for (Phrase phrase : Phrase.values()) {
            audioFiles.remove(phrase.getPhraseAudioFile());
        }

        // All of the remaining audio files in the list need to be deleted
        for (File oldPhraseAudioFile : audioFiles) {
            if (!oldPhraseAudioFile.isDirectory()) {
                String oldAudioFilePath = oldPhraseAudioFile.getPath();
                if (oldPhraseAudioFile.delete()) {
                    System.out.println("Deleted audio file: " + oldAudioFilePath);
                } else {
                    System.err.println("Could not delete audio file: " + oldAudioFilePath);
                }
            }
        }
    }

    // Creates Phrase audio files by calling into the Google Cloud Text-to-Speech Java API
    private void createPhraseAudioFiles() {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            for (Phrase phrase : Phrase.values()) {
                File phraseFile = phrase.getPhraseAudioFile();

                // Don't recreate any phrase audio file that already exists (conserve Google Cloud quota)
                if (phraseFile.exists()) {
                    continue;
                }

                // Set the text of the constructed audio file to the current phrase value
                SynthesisInput input = SynthesisInput.newBuilder()
                        .setText(phrase.getPhraseValue())
                        .build();

                // Neutral gender & US-based voice
                VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                        .setLanguageCode("en-US")
                        .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                        .build();

                // Java only natively plays WAV audio files
                AudioConfig audioConfig = AudioConfig.newBuilder()
                        .setAudioEncoding(AudioEncoding.LINEAR16)
                        .build();

                SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
                ByteString audioContents = response.getAudioContent();

                // Write the phrase audio file & print the saved phrase name to stdout
                try (OutputStream out = new FileOutputStream(phraseFile)) {
                    out.write(audioContents.toByteArray());
                }
                System.out.println("Phrase audio file successfully saved: " + phrase);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/