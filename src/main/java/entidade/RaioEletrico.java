package entidade;

import util.Configuracoes;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class RaioEletrico {

    private float x;
    private int largura  = 80;
    private int yTopo;   // onde o raio começa (posição Y do Bumblebee)
    private int yBase;   // onde o raio termina (chão)
    private int vida     = 20; // dura ~0.33 segundos
    private boolean ativo = true;
    private int dano;

    private boolean jaDeuDano = false;

    private BufferedImage[] frames;
    private int frameAtual     = 0;
    private int contadorFrames = 0;

    public RaioEletrico(float x, int yTopo, int dano, BufferedImage[] frames) {
        this.x      = x;
        this.yTopo  = yTopo;
        this.yBase  = Configuracoes.ALTURA_CHAO;
        this.dano   = dano;
        this.frames = frames;
    }

    public void atualizar() {
        if (!ativo) return;

        vida--;
        if (vida <= 0) { ativo = false; return; }

        // animação
        if (frames != null && frames.length > 0) {
            contadorFrames++;
            if (contadorFrames >= 4) {
                contadorFrames = 0;
                frameAtual = (frameAtual + 1) % frames.length;
            }
        }
    }

    public void checarDano(Lutador alvo) {
        if (!ativo || jaDeuDano) return;
        if (getHitbox().intersects(alvo.getHitbox())) {
            alvo.receberDano(dano);
            jaDeuDano = true;
        }
    }

    public void desenhar(Graphics2D g2) {
        if (!ativo) return;

        int altura = yBase - yTopo;

        if (frames != null && frames.length > 0 && frames[frameAtual] != null) {
            g2.drawImage(frames[frameAtual], (int) x - largura / 2, yTopo, largura, altura, null);
        } else {
            // fallback: coluna de eletricidade amarela
            g2.setColor(new Color(255, 255, 0, 180));
            g2.fillRect((int) x - largura / 2, yTopo, largura, altura);
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fillRect((int) x - largura / 4, yTopo, largura / 2, altura);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle((int) x - largura / 2, yTopo, largura, yBase - yTopo);
    }

    public boolean isAtivo() { return ativo; }
}