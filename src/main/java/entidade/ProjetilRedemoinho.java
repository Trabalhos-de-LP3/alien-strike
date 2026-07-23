package entidade;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import util.Configuracoes;
import util.GerenciadorAudio;
import util.Sons;

public class ProjetilRedemoinho extends Projetil {

    private int tempoVida;          // frames de vida total
    private Lutador vitima = null;  // lutador capturado
    private int danoContato;        // dano ao capturar
    private int cooldownDano = 0;   // dano periódico enquanto arrasta

    public ProjetilRedemoinho(float x, float y, float velX, int largura, int altura,
                               int dano, boolean viradoParaDireita,
                               BufferedImage[] frames, int tempoVida) {
        super(x, y, velX, largura, altura, dano, viradoParaDireita, frames);
        this.tempoVida   = tempoVida;
        this.danoContato = dano;
    }

    @Override
    public void atualizar() {
        if (!ativo) return;

        tempoVida--;
        if (tempoVida <= 0) {
            liberarVitima();
            ativo = false;
            return;
        }

        // se tiver uma vítima capturada, arrasta ela junto
        if (vitima != null) {
            vitima.setX(x + largura / 4f);

            // dano periódico enquanto arrasta (a cada 20 frames)
            if (cooldownDano > 0) {
                cooldownDano--;
            } else {
                vitima.receberDanoRedemoinho(danoContato / 5);
                cooldownDano = 20;
            }
        }

        // move o redemoinho (e a vítima junto, pois setX é chamado acima)
        x += velX;

        // animação
        super.atualizarAnimacao();

        // sai da tela
        if (x < -200 || x > Configuracoes.LARGURA_TELA + 200) {
            liberarVitima();
            ativo = false;
        }
    }

    // tenta capturar um lutador ao colidir
    public void checarCaptura(Lutador alvo) {
        if (!ativo || vitima != null) return;
        if (alvo.estanocauteado()) return;

        if (getHitbox().intersects(alvo.getHitbox())) {
            vitima = alvo;
            vitima.receberDanoRedemoinho(danoContato); // dano inicial de captura
            GerenciadorAudio.tocarEfeito(Sons.EFEITO_BUMBLEBEE_CAPTURA);
        }
    }

    private void liberarVitima() {
        vitima = null;
    }

    public boolean temVitima() { return vitima != null; }
}