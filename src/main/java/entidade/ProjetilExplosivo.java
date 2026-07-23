package entidade;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import entidade.lutadores.Guerreiro;
import util.GerenciadorAudio;
import util.Sons; // importe a classe do Kree

public class ProjetilExplosivo extends Projetil {
    private int tempoVida;
    private boolean explodindo = false;
    private BufferedImage[] animExplosao;
    private int frameExplosao = 0;
    private int timerExplosao = 0;
    
    // dano da explosão
    private int danoExplosao; 

    public ProjetilExplosivo(float x, float y, float velX, int largura, int altura, int dano, boolean viradoDireita, BufferedImage[] animProjetil, BufferedImage[] animExplosao, int tempoVida, int danoExplosao) {
        super(x, y, velX, largura, altura, dano, viradoDireita, animProjetil);
        this.tempoVida = tempoVida;
        this.animExplosao = animExplosao;
        this.danoExplosao = danoExplosao;
    }

    public void verificarColisao(Lutador adversario) {
        // só checa colisão se não estiver explodindo ainda
        if (!explodindo && this.getHitbox().intersects(adversario.getHitbox())) {
            explodir(adversario);
        }
    }

    private void explodir(Lutador adversario) {
        explodindo = true; // trava a explosão
        velX = 0;
        adversario.receberDano(danoExplosao);
        GerenciadorAudio.tocarEfeito(Sons.EFEITO_KREE_EXPLOSAO);
    }
    
    @Override
    public Rectangle getHitbox() {
        if (explodindo) {
            // se quiser, pode aumentar estes valores para expandir a área de dano da explosão
            int margemExplosao = 60; 
            return new Rectangle((int) x - margemExplosao, 
                                 (int) y - margemExplosao, 
                                 largura + (margemExplosao * 2), 
                                 altura + (margemExplosao * 2));
        }
        // se ainda for a bala viajando, usa a hitbox original pequena
        return super.getHitbox();
    }

    @Override
    public void atualizar() {
        if (!explodindo) {
            super.atualizar(); // movimento normal
            tempoVida--;
            if (tempoVida <= 0) {
                explodindo = true;
                GerenciadorAudio.tocarEfeito(Sons.EFEITO_KREE_EXPLOSAO);
            }
        } else {
            // lógica de animação da explosão
            timerExplosao++;
            if (timerExplosao >= 5) {
                timerExplosao = 0;
                frameExplosao++;
                
                // só desativa o projétil quando chegar no último frame da explosão
                if (frameExplosao >= animExplosao.length) {
                    ativo = false; 
                }
            }
        }
    }
    
    @Override
    public void desenhar(Graphics2D g2) {
        // se ainda não explodiu, desenha a bala normal
        if (!explodindo) {
            super.desenhar(g2); 
        } 
        // se estiver explodindo, desenha a explosão, independente do estado ativo
        else if (animExplosao != null && frameExplosao < animExplosao.length) {
            
            int margemExtra = 100; 
            
            g2.drawImage(animExplosao[frameExplosao], 
                         (int) x - margemExtra, 
                         (int) y - margemExtra, 
                         largura + (margemExtra * 2), 
                         altura + (margemExtra * 2), 
                         null);
        }
    }
}