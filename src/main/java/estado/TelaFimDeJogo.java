package estado;

import entidade.Lutador;
import motor.CapturaTeclado;
import util.Configuracoes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// tela exibida após o fim da luta
// mostra o vencedor e permite reiniciar ou voltar ao menu

public class TelaFimDeJogo {

    private String nomeVencedor = "";
    private boolean ehEmpate    = false;
    private int contadorPisca   = 0;

    public void definirResultado(Lutador j1, Lutador j2) {
        if (j1.getHpAtual() > j2.getHpAtual()) {
            nomeVencedor = j1.getNome() + " (Jogador 1)";
            ehEmpate     = false;
        } else if (j2.getHpAtual() > j1.getHpAtual()) {
            nomeVencedor = j2.getNome() + " (Jogador 2)";
            ehEmpate     = false;
        } else {
            ehEmpate     = true;
        }
    }

    public void desenhar(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // overlay escuro
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, Configuracoes.LARGURA_TELA, Configuracoes.ALTURA_TELA);

        int cx = Configuracoes.LARGURA_TELA / 2;

        // KO ou empate
        g2.setFont(new Font("Serif", Font.BOLD, 96));
        String textoTopo = ehEmpate ? "EMPATE!" : "K.O.!";
        g2.setColor(new Color(255, 50, 50));
        int largTopo = g2.getFontMetrics().stringWidth(textoTopo);
        g2.drawString(textoTopo, cx - largTopo / 2, 220);

        // vencedor
        if (!ehEmpate) {
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            g2.setColor(new Color(255, 220, 50));
            String vitoria = "VENCEDOR: " + nomeVencedor;
            int largVit = g2.getFontMetrics().stringWidth(vitoria);
            g2.drawString(vitoria, cx - largVit / 2, 300);
        }

        // instrução piscante
        contadorPisca++;
        if (contadorPisca < 50) {
            g2.setFont(new Font("Arial", Font.PLAIN, 22));
            g2.setColor(Color.WHITE);
            String instrucao = "ENTER — Jogar novamente    ESC — Menu";
            int largInstr = g2.getFontMetrics().stringWidth(instrucao);
            g2.drawString(instrucao, cx - largInstr / 2, 380);
        }
        if (contadorPisca >= 90) contadorPisca = 0;
    }
}