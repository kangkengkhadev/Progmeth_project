package logic;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;
    private Media audio;
    private boolean replay;

    public AudioPlayer(String audioPath, boolean replay) {
        this.replay = replay;
        audio = new Media(ClassLoader.getSystemResource(audioPath).toString());
        mediaPlayer = new MediaPlayer(audio);
    }

    public void playAudio() {
        mediaPlayer.play();
        if (replay) {
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(mediaPlayer.getStartTime());
                mediaPlayer.play();
            });
        } else {
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
            });
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public MediaPlayer.Status getStatus() {
        return mediaPlayer.getStatus();
    }
}
