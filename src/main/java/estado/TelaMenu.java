package estado;

import motor.CapturaTeclado;
import util.Configuracoes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import util.CarregadorImagens;
import java.awt.image.BufferedImage;

import util.GerenciadorAudio;
import util.Sons;

// tela de menu principal
// exibe o título do jogo e opções de navegação
// confirmar com ENTER inicia a seleção de personagens

public class TelaMenu {
	
	private BufferedImage fundoMenu;

	public TelaMenu() {
	    fundoMenu = CarregadorImagens.carregar("/sprites/cenario/espacoAzul.png");
	}

    private static final Font FONTE_TITULO = new Font("Serif",    Font.BOLD,  72);
    private static final Font FONTE_SUBTIT = new Font("Serif", Font.BOLD, 26);
    private static final Font FONTE_OPCAO  = new Font("Arial",    Font.BOLD,  28);

    private int opcaoSelecionada = 0;
    private final String[] opcoes = { "JOGAR", "SAIR" };
    private int contadorPisca = 0;

    public void atualizar(CapturaTeclado teclado) {
        // o confirmar em si é tratado pelo PainelJogo,
        // mas tocamos o efeito aqui quando a tecla é pressionada
        if (teclado.confirmar) {
            GerenciadorAudio.tocarEfeito(Sons.EFEITO_CONFIRMAR);
        }
    }

    public void desenhar(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // fundo gradiente
        if (fundoMenu != null) {
            g2.drawImage(fundoMenu, 0, 0, Configuracoes.LARGURA_TELA, Configuracoes.ALTURA_TELA, null);
        } else {
            // fallback: gradiente original
            for (int i = 0; i < Configuracoes.ALTURA_TELA; i++) {
                float t = (float) i / Configuracoes.ALTURA_TELA;
                int r = (int)(10 + t * 30);
                int gv = (int)(5 + t * 10);
                int b = (int)(40 + t * 60);
                g2.setColor(new Color(r, gv, b));
                g2.drawLine(0, i, Configuracoes.LARGURA_TELA, i);
            }
        }

        // título
        g2.setFont(FONTE_TITULO);
        String titulo = "ALIEN STRIKE";
        int largTit = g2.getFontMetrics().stringWidth(titulo);
        // sombra
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(titulo, (Configuracoes.LARGURA_TELA - largTit) / 2 + 4, 184);
        // texto
        g2.setColor(new Color(255, 200, 50));
        g2.drawString(titulo, (Configuracoes.LARGURA_TELA - largTit) / 2, 180);

        // bubtítulo
        g2.setFont(FONTE_SUBTIT);
        String sub = "PANCADARIA EXTRATERRESTRE";
        int largSub = g2.getFontMetrics().stringWidth(sub);
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(sub, (Configuracoes.LARGURA_TELA - largSub) / 2 + 2, 222);
        g2.setColor(new Color(255, 200, 50));
        g2.drawString(sub, (Configuracoes.LARGURA_TELA - largSub) / 2, 220);

        // instruções
        contadorPisca++;
        if (contadorPisca < 45) {
            g2.setFont(FONTE_OPCAO);
            g2.setColor(Color.WHITE);
            String msg = "Pressione ENTER para jogar";
            int largMsg = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (Configuracoes.LARGURA_TELA - largMsg) / 2, 380);
        }
        if (contadorPisca >= 90) contadorPisca = 0;

        // controles
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        int yCtrl = 460;
        // faixa escura atrás dos controles
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(40, yCtrl - 16, 700, 50, 8, 8);
        // sombra do texto
        g2.setColor(new Color(0, 0, 0, 220));
        g2.drawString("Jogador 1: A/D mover | W pular | S carregar | G soco | H chute | J especial 1 | Y especial 2", 52, yCtrl + 2);
        g2.drawString("Jogador 2: Setas mover/pular/carregar | 1 soco | 2 chute | 3 especial 1 | 5 especial 2", 52, yCtrl + 24);
        // texto
        g2.setColor(new Color(210, 210, 210));
        g2.drawString("Jogador 1: A/D mover | W pular | S carregar | G soco | H chute | J especial 1 | Y especial 2", 50, yCtrl);
        g2.drawString("Jogador 2: Setas mover/pular/carregar | 1 soco | 2 chute | 3 especial 1 | 5 especial 2", 50, yCtrl + 22);
    }
}