package entidade;

import util.Configuracoes;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class ParedeChamas {

    private float x, y;
    private int largura = 180;
    private int altura  = 200;
    private int dano    = 8;
    private int vida    = 180; // dura 3 segundos (180 frames)
    private boolean ativa = true;

    private int cooldownDano = 0;

    private BufferedImage[] frames;
    private int frameAtual    = 0;
    private int contadorFrames = 0;

    public ParedeChamas(float x, float y, BufferedImage[] frames) {
        this.x = x - 40;
        this.y = Configuracoes.ALTURA_CHAO - altura; // sempre ancorado no chão
        this.frames = frames;
    }

    public void atualizar() {
        if (!ativa) return;

        vida--;
        if (vida <= 0) { ativa = false; return; }

        if (cooldownDano > 0) cooldownDano--;

        // animação
        if (frames != null && frames.length > 0) {
            contadorFrames++;
            if (contadorFrames >= 6) {
                contadorFrames = 0;
                frameAtual = (frameAtual + 1) % frames.length;
            }
        }
    }

    // verifica se um lutador tocou na parede e aplica dano
    public void checarDano(Lutador alvo) {
        if (!ativa || cooldownDano > 0) return;
        if (getHitbox().intersects(alvo.getHitbox())) {
            alvo.receberDano(dano);
            cooldownDano = 30; // dano a cada 30 frames
        }
    }

    // verifica se um projétil colidiu com a parede e o cancela
    public void checarProjetil(Projetil p) {
        if (!ativa || !p.isAtivo()) return;
        if (getHitbox().intersects(p.getHitbox())) {
            p.setAtivo(false); // cancela o projétil
        }
    }

    public void desenhar(Graphics2D g2) {
        if (!ativa) return;

        if (frames != null && frames.length > 0 && frames[frameAtual] != null) {
            g2.drawImage(frames[frameAtual], (int) x, (int) y, largura, altura, null);
        } else {
            // Fallback visual
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