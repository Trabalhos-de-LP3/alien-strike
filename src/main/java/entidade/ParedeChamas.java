package entidade;

import util.Configuracoes;
import util.GerenciadorAudio;
import util.Sons;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.sound.sampled.*;
import java.io.IOException;

public class ParedeChamas {

    private float x, y;
    private int largura = 180;
    private int altura  = 200;
    private int dano    = 8;
    private int vida    = 180; // dura 3 segundos (180 frames)
    private boolean ativa = true;

    private int cooldownDano = 0;

    private BufferedImage[] frames;
    private int frameAtual     = 0;
    private int contadorFrames = 0;

    // clip dedicado ao som de chamas desta parede
    private Clip clipChamas = null;

    public ParedeChamas(float x, float y, BufferedImage[] frames) {
        this.x = x - 40;
        this.y = Configuracoes.ALTURA_CHAO - altura;
        this.frames = frames;
        iniciarSomChamas();
    }

    private void iniciarSomChamas() {
        try {
            var is = getClass().getResourceAsStream(Sons.EFEITO_KREE_CHAMAS_ATIVAS);
            if (is == null) {
                System.err.println("[Audio] Efeito não encontrado: " + Sons.EFEITO_KREE_CHAMAS_ATIVAS);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                new java.io.BufferedInputStream(is));
            clipChamas = AudioSystem.getClip();
            clipChamas.open(audioStream);
            clipChamas.loop(Clip.LOOP_CONTINUOUSLY);
            clipChamas.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("[Audio] Erro ao iniciar som das chamas: " + e.getMessage());
        }
    }

    private void pararSomChamas() {
        if (clipChamas != null) {
            clipChamas.stop();
            clipChamas.close();
            clipChamas = null;
        }
    }

    public void atualizar() {
        if (!ativa) return;

        vida--;
        if (vida <= 0) {
            ativa = false;
            pararSomChamas();
            return;
        }

        if (cooldownDano > 0) cooldownDano--;

        if (frames != null && frames.length > 0) {
            contadorFrames++;
            if (contadorFrames >= 6) {
                contadorFrames = 0;
                frameAtual = (frameAtual + 1) % frames.length;
            }
        }
    }

    public void checarDano(Lutador alvo) {
        if (!ativa || cooldownDano > 0) return;
        if (getHitbox().intersects(alvo.getHitbox())) {
            alvo.receberDano(dano);
            cooldownDano = 30;
            // som de queima ao tocar no adversário (pontual, separado do loop)
            GerenciadorAudio.tocarEfeito(Sons.EFEITO_KREE_CHOQUE);
        }
    }

    public void checarProjetil(Projetil p) {
        if (!ativa || !p.isAtivo()) return;
        if (getHitbox().intersects(p.getHitbox())) {
            p.setAtivo(false);
        }
    }

    public void desenhar(Graphics2D g2) {
        if (!ativa) return;

        if (frames != null && frames.length > 0 && frames[frameAtual] != null) {
            g2.drawImage(frames[frameAtual], (int) x, (int) y, largura, altura, null);
        } else {
            g2.setColor(new Color(255, 80, 0, 200));
            g2.fillRect((int) x, (int) y, largura, altura);
            g2.setColor(new Color(255, 200, 0, 180));
            g2.fillRect((int) x + 8, (int) y, largura - 16, altura);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y, largura, altura);
    }

    public boolean isAtiva() { return ativa; }
}