package util;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorAudio {

    private static final Map<String, byte[]> cacheEfeitos = new HashMap<>();

    private static Clip clipMusica;
    private static String caminhoMusicaAtual = "";

    // clip dedicado para sons em loop (ex: carga de transformação)
    private static Clip clipLoop;

    private static float volumeMusica  = 0.2f;
    private static float volumeEfeitos = 2.0f;
    private static boolean mudo = false;

    // --------------------------------------------------------
    //  música de fundo

    public static void tocarMusica(String caminho) {
        if (mudo) return;
        if (caminho.equals(caminhoMusicaAtual) && clipMusica != null && clipMusica.isRunning()) return;

        pararMusica();

        try (InputStream is = GerenciadorAudio.class.getResourceAsStream(caminho)) {
            if (is == null) {
                System.err.println("[Audio] Música não encontrada: " + caminho);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    new java.io.BufferedInputStream(is));
            clipMusica = AudioSystem.getClip();
            clipMusica.open(audioStream);
            aplicarVolumeMusica();
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
            clipMusica.start();
            caminhoMusicaAtual = caminho;
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("[Audio] Erro ao tocar música: " + caminho);
        }
    }

    public static void pararMusica() {
        if (clipMusica != null && clipMusica.isRunning()) clipMusica.stop();
        if (clipMusica != null) clipMusica.close();
        clipMusica = null;
        caminhoMusicaAtual = "";
    }

    public static void pausarMusica() {
        if (clipMusica != null && clipMusica.isRunning()) clipMusica.stop();
    }

    public static void retomarMusica() {
        if (mudo) return;
        if (clipMusica != null && !clipMusica.isRunning()) clipMusica.start();
    }

    // --------------------------------------------------------
    //  efeitos sonoros pontuais

    public static void tocarEfeito(String caminho) {
        if (mudo) return;

        byte[] dados = cacheEfeitos.computeIfAbsent(caminho, k -> carregarBytes(k));
        if (dados == null) return;

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    new java.io.ByteArrayInputStream(dados));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            aplicarVolumeEfeito(clip);
            clip.addLineListener(evento -> {
                if (evento.getType() == LineEvent.Type.STOP) clip.close();
            });
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("[Audio] Erro ao tocar efeito: " + caminho);
        }
    }

    public static void preCarregarEfeito(String caminho) {
        cacheEfeitos.computeIfAbsent(caminho, k -> carregarBytes(k));
    }

    // --------------------------------------------------------
    //  som em loop controlado (ex: carga de transformação)

    // inicia um som em loop. Se o mesmo caminho já estiver tocando, não reinicia.
    public static void iniciarLoop(String caminho) {
        if (mudo) return;

        // já está tocando esse mesmo loop, não faz nada
        if (clipLoop != null && clipLoop.isRunning()) return;

        pararLoop();

        byte[] dados = cacheEfeitos.computeIfAbsent(caminho, k -> carregarBytes(k));
        if (dados == null) return;

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    new java.io.ByteArrayInputStream(dados));
            clipLoop = AudioSystem.getClip();
            clipLoop.open(audioStream);
            aplicarVolumeEfeito(clipLoop);
            clipLoop.loop(Clip.LOOP_CONTINUOUSLY);
            clipLoop.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("[Audio] Erro ao iniciar loop: " + caminho);
        }
    }

    // para o som em loop imediatamente
    public static void pararLoop() {
        if (clipLoop != null) {
            clipLoop.stop();
            clipLoop.close();
            clipLoop = null;
        }
    }

    // --------------------------------------------------------
    //  controle de volume

    public static void setVolumeMusicaPorcento(int porcento) {
        volumeMusica = Math.max(0, Math.min(100, porcento)) / 100f;
        aplicarVolumeMusica();
    }

    public static void setVolumeEfetosPorcento(int porcento) {
        volumeEfeitos = Math.max(0, Math.min(100, porcento)) / 100f;
    }

    public static void setMudo(boolean estado) {
        mudo = estado;
        if (mudo) { pausarMusica(); pararLoop(); }
        else      retomarMusica();
    }

    public static boolean isMudo()               { return mudo; }
    public static int getVolumeMusicaPorcento()  { return (int)(volumeMusica  * 100); }
    public static int getVolumeEfetosPorcento()  { return (int)(volumeEfeitos * 100); }

    // --------------------------------------------------------
    //  privados

    private static void aplicarVolumeMusica() {
        if (clipMusica == null) return;
        aplicarVolume(clipMusica, volumeMusica);
    }

    private static void aplicarVolumeEfeito(Clip clip) {
        aplicarVolume(clip, volumeEfeitos);
    }

    private static void aplicarVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl ganho = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = volume == 0 ? ganho.getMinimum() : (float)(Math.log10(volume) * 20);
            dB = Math.max(ganho.getMinimum(), Math.min(ganho.getMaximum(), dB));
            ganho.setValue(dB);
        }
    }

    private static byte[] carregarBytes(String caminho) {
        try (InputStream is = GerenciadorAudio.class.getResourceAsStream(caminho)) {
            if (is == null) {
                System.err.println("[Audio] Efeito não encontrado: " + caminho);
                return null;
            }
            return is.readAllBytes();
        } catch (IOException e) {
            System.err.println("[Audio] Erro ao carregar: " + caminho);
            return null;
        }
    }
}
