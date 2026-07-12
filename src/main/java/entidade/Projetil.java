package entidade;

import util.Configuracoes;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Projetil {
    protected float x, y;
    protected float velX;
    protected int largura, altura;
    protected int dano;
    protected boolean ativo = true;
    protected boolean viradoParaDireita;

    private BufferedImage[] frames;
    private int frameAtual = 0;
    private int contadorFrames = 0;

    public Projetil(float x, float y, float velX, int largura, int altura, int dano, boolean viradoParaDireita, BufferedImage[] frames) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.largura = largura;
        this.altura = altura;
        this.dano = dano;
        this.viradoParaDireita = viradoParaDireita;
        this.frames = frames;
    }

    public void atualizar() {
        x += velX;
        atualizarAnimacao();

        if (x < -200 || x > Configuracoes.LARGURA_TELA + 200) {
            ativo = false;
        }
    }

    protected void atualizarAnimacao() {
        if (frames != null && frames.length > 0) {
            contadorFrames++;
            if (contadorFrames >= 5) {
                contadorFrames = 0;
                frameAtual = (frameAtual + 1) % frames.length;
            }
        }
    }

    public void desenhar(Graphics2D g2) {
        if (!ativo || frames == null) return;

        BufferedImage frame = frames[frameAtual];
        if (viradoParaDireita) {
            g2.drawImage(frame, (int) x, (int) y, largura, altura, null);
        } else {
            g2.drawImage(frame, (int) x + largura, (int) y, -largura, altura, null);
        }
    }

    public Rectangle getHitbox() {
        int margemX = largura / 4;
        int margemY = altura / 4;
        int larguraHit = largura / 2;
        int alturaHit = altura / 2;
        return new Rectangle((int) x + margemX, (int) y + margemY, larguraHit, alturaHit);
    }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public int getDano() { return dano; }
}