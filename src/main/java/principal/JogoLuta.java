package principal;

import javax.swing.JFrame;
import motor.PainelJogo;

 // ponto de entrada do jogo
 // cria a janela principal e inicializa o painel de jogo
 
public class JogoLuta {

    public static void main(String[] args) {
        JFrame janela = new JFrame("ALIEN STRIKE");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setResizable(false);

        PainelJogo painel = new PainelJogo();
        janela.add(painel);
        janela.pack();

        janela.setLocationRelativeTo(null);
        janela.setVisible(true);

        painel.iniciarThreadJogo();
    }
}