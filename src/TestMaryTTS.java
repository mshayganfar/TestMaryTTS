import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.UnsupportedAudioFileException;

import marytts.util.data.audio.AudioPlayer;
import marytts.client.MaryClient;
import marytts.util.http.Address;

public class TestMaryTTS {

    public static void main(String[] args)
    throws IOException, UnknownHostException, UnsupportedAudioFileException,
        InterruptedException
    {
        String serverHost = System.getProperty("server.host", "localhost");
        int serverPort = Integer.getInteger("server.port", 59125).intValue();
        MaryClient mary = MaryClient.getMaryClient(new Address(serverHost, serverPort));
        String text = "Oh my God! It made me so sad!";
        
        // If the given locale is not supported by the server, it returns
        // an ambigous exception: "Problem processing the data."
        
        String locale = "en_US"; // or US English (en-US), Telugu (te), Turkish (tr), ...
        String inputType = "TEXT";
        String outputType = "AUDIO";
        String audioType = "WAVE";
        String defaultVoiceName = "cmu-rms-hsmm";
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        mary.process(text, inputType, outputType, locale, audioType, defaultVoiceName, baos);
        
        // The byte array constitutes a full wave file, including the headers.
        // And now, play the audio data:
        AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(baos.toByteArray()));
        
        Vector<MaryClient.Voice> voices = mary.getVoices(); 
        if (voices != null) { 
         for (MaryClient.Voice v : voices) { 
          System.out.println(v.name());
         } 
        }
        
        LineListener lineListener = new LineListener() {
            public void update(LineEvent event) {
                if (event.getType() == LineEvent.Type.START) {
                    System.err.println("Audio started playing.");
                } else if (event.getType() == LineEvent.Type.STOP) {
                    System.err.println("Audio stopped playing.");
                } else if (event.getType() == LineEvent.Type.OPEN) {
                    System.err.println("Audio line opened.");
                } else if (event.getType() == LineEvent.Type.CLOSE) {
                    System.err.println("Audio line closed.");
                }
            }
        };

        AudioPlayer ap = new AudioPlayer(ais, lineListener);
        ap.start();
    }

}
