package util;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/*
 * gerenciador central de áudio do jogo
 *
 * suporta dois tipos de som:
 *  - música de fundo (loop contínuo, uma por vez)
 *  - efeitos sonoros (disparados pontualmente, sobreposição permitida)
 *
 * uso:
 *   GerenciadorAudio.tocarMusica("/sons/musica/tema_menu.wav");
 *   GerenciadorAudio.tocarEfeito("/sons/efeitos/soco.wav");
 *   GerenciadorAudio.setVolumeMusicaPorcento(70);
 */

public class GerenciadorAudio {

    // cache de clips de efeito
    private static final Map<String, byte[]> cacheEfeitos = new HashMap<>();

    // música atual
    private static Clip clipMusica;
    private static String caminhoMusicaAtual = "";

    // volumes (0.0 a 1.0)
    private static float volumeMusica  = 0.7f;
    private static float volumeEfeitos = 1.0f;

    // flag global de mudo
    private static boolean mudo = false;

    // --------------------------------------------------------
    //  música de fundo

    // toca uma música em loop. Se já estiver tocando a mesma, não reinicia
    // @param caminho Ex: "/sons/musica/tema_luta.wav"
    
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

    // para a música atual imediatamente
    public static void pararMusica() {
        if (clipMusica != null && clipMusica.isRunning()) {
            clipMusica.stop();
        }
        if (clipMusica != null) {
            clipMusica.close();
        }
        clipMusica = null;
        caminhoMusicaAtual = "";
    }

    // pausa a música atual sem descartá-la
    public static void pausarMusica() {
        if (clipMusica != null && clipMusica.isRunning()) {
            clipMusica.stop();
        }
    }

    // retoma a música pausada
    public static void retomarMusica() {
        if (mudo) return;
        if (clipMusica != null && !clipMusica.isRunning()) {
            clipMusica.start();
        }
    }

    // ------------------------------------------------------------
    //  efeitos sonoros

    // toca um efeito sonoro uma vez
    // múltiplas chamadas sobrepostas são permitidas (cada uma cria seu próprio Clip)
    // @param caminho Ex: "/sons/efeitos/soco.wav"

    public static void tocarEfeito(String caminho) {
        if (mudo) return;

        // carrega os bytes em cache para evitar I/O repetido
        byte[] dados = cacheEfeitos.computeIfAbsent(caminho, k -> carregarBytes(k));
        if (dados == null) return;

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    new java.io.ByteArrayInputStream(dados));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            aplicarVolumeEfeito(clip);

            // libera o Clip automaticamente quando terminar
            clip.addLineListener(evento -> {
                if (evento.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("[Audio] Erro ao tocar efeito: " + caminho);
        }
    }

    // pré-carrega um efeito para evitar atraso no primeiro disparo
    public static void preCarregarEfeito(String caminho) {
        cacheEfeitos.computeIfAbsent(caminho, k -> carregarBytes(k));
    }

    // ----------------------------------------------------------------------
    //  controle de volume

    // define volume da música (0 a 100)
    public static void setVolumeMusicaPorcento(int porcento) {
        volumeMusica = Math.max(0, Math.min(100, porcento)) / 100f;
        aplicarVolumeMusica();
    }

    // define volume dos efeitos (0 a 100)
    public static void setVolumeEfetosPorcento(int porcento) {
        volumeEfeitos = Math.max(0, Math.min(100, porcento)) / 100f;
    }

    // ativa ou desativa o mudo global
    public static void setMudo(boolean estado) {
        mudo = estado;
        if (mudo) pausarMusica();
        else      retomarMusica();
    }

    public static boolean isMudo()           { return mudo; }
    public static int getVolumeMusicaPorcento()  { return (int)(volumeMusica  * 100); }
    public static int getVolumeEfetosPorcento()  { return (int)(volumeEfeitos * 100); }

    // ---------------------------------------------------------------
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
            // converte de 0..1 linear para dB (escala logarítmica)
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