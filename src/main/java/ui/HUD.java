package ui;

import entidade.Lutador;
import util.Configuracoes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;

// HUD do jogo
// desenha as barras de HP dos dois jogadores e o timer do round
 
public class HUD {

    private static final int MARGEM       = 20;
    private static final int LARGURA_BARRA = 380;
    private static final int ALTURA_BARRA  = 28;
    private static final int Y_BARRA       = 20;

    private static final Font FONTE_NOME   = new Font("Arial", Font.BOLD, 14);
    private static final Font FONTE_TIMER  = new Font("Arial", Font.BOLD, 36);
    private static final Font FONTE_HP     = new Font("Arial", Font.PLAIN, 12);

    private int timerSegundos;
    private int contadorFrames = 0;

    public HUD() {
        this.timerSegundos = Configuracoes.TEMPO_ROUND;
    }

    // atualiza o timer regressivo, deve ser chamado a cada frame
    
    public void atualizar() {
        contadorFrames++;
        if (contadorFrames >= Configuracoes.FPS_ALVO) {
            contadorFrames = 0;
            if (timerSegundos > 0) timerSegundos--;
        }
    }

    // desenha toda a HUD na tela
    
    public void desenhar(Graphics2D g2, Lutador jogador1, Lutador jogador2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        desenharBarraHP(g2, jogador1, true);
        desenharBarraHP(g2, jogador2, false);
        desenharTimer(g2);
        desenharNomes(g2, jogador1, jogador2);
    }

    private void desenharBarraHP(Graphics2D g2, Lutador lutador, boolean ehJogador1) {
        int xBarra = ehJogador1
                ? MARGEM
                : Configuracoes.LARGURA_TELA - MARGEM - LARGURA_BARRA;

        float proporcao = (float) lutador.getHpAtual() / lutador.getHpMaximo();
        int larguraAtual = (int) (LARGURA_BARRA * proporcao);

        // fundo da barra (cinza escuro)
        g2.setColor(new Color(40, 40, 40, 200));
        g2.fillRoundRect(xBarra, Y_BARRA, LARGURA_BARRA, ALTURA_BARRA, 8, 8);

        // barra de HP com gradiente
        Color corAlta  = ehJogador1 ? new Color(50, 200, 80)  : new Color(200, 50, 50);
        Color corBaixa = ehJogador1 ? new Color(180, 220, 50) : new Color(220, 130, 50);
        Color corAtual = proporcao > 0.5f ? corAlta : (proporcao > 0.25f ? corBaixa : Color.RED);

        if (larguraAtual > 0) {
            // Jogador 2: barra cresce da direita para a esquerda
            int xHP = ehJogador1 ? xBarra : xBarra + (LARGURA_BARRA - larguraAtual);
            g2.setColor(corAtual);
            g2.fillRoundRect(xHP, Y_BARRA, larguraAtual, ALTURA_BARRA, 8, 8);

            // brilho no topo da barra
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillRoundRect(xHP, Y_BARRA, larguraAtual, ALTURA_BARRA / 2, 8, 8);
        }

        // borda da barra
        g2.setColor(new Color(200, 180, 100));
        g2.drawRoundRect(xBarra, Y_BARRA, LARGURA_BARRA, ALTURA_BARRA, 8, 8);

        // texto de HP
        g2.setFont(FONTE_HP);
        g2.setColor(Color.WHITE);
        String textoHP = lutador.getHpAtual() + " / " + lutador.getHpMaximo();
        int xTexto = ehJogador1
                ? xBarra + 5
                : xBarra + LARGURA_BARRA - g2.getFontMetrics().stringWidth(textoHP) - 5;
        g2.drawString(textoHP, xTexto, Y_BARRA + ALTURA_BARRA - 8);
    }

    private void desenharNomes(Graphics2D g2, Lutador j1, Lutador j2) {
        g2.setFont(FONTE_NOME);

        // nome J1
        g2.setColor(new Color(100, 200, 255));
        g2.drawString(j1.getNome(), MARGEM, Y_BARRA + ALTURA_BARRA + 18);

        // nome J2
        g2.setColor(new Color(255, 150, 100));
        String nomeJ2 = j2.getNome();
        int largNomeJ2 = g2.getFontMetrics().stringWidth(nomeJ2);
        g2.drawString(nomeJ2,
                Configuracoes.LARGURA_TELA - MARGEM - largNomeJ2,
                Y_BARRA + ALTURA_BARRA + 18);
    }

    private void desenharTimer(Graphics2D g2) {
        String texto = String.valueOf(timerSegundos);

        g2.setFont(FONTE_TIMER);
        int largTexto = g2.getFontMetrics().stringWidth(texto);
        int x = (Configuracoes.LARGURA_TELA - largTexto) / 2;

        // sombra
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(texto, x + 2, Y_BARRA + ALTURA_BARRA + 2);

        // cor muda para vermelho nos últimos 10 segundos
        g2.setColor(timerSegundos <= 10 ? new Color(255, 60, 60) : new Color(255, 220, 50));
        g2.drawString(texto, x, Y_BARRA + ALTURA_BARRA);
    }

    public int getTimerSegundos() { return timerSegundos; }
    public void resetar() { timerSegundos = Configuracoes.TEMPO_ROUND; contadorFrames = 0; }
}