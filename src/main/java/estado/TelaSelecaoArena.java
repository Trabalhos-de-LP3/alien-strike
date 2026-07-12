package estado;

import motor.CapturaTeclado;
import util.CarregadorImagens;
import util.Configuracoes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TelaSelecaoArena {

    private static final String[] NOMES = { "Castelo", "Laboratório", "Cidade", "Cyberpunk" };
    private static final String[] CAMINHOS = {
        "/sprites/cenario/castle.png",
        "/sprites/cenario/scifi-lab.png",
        "/sprites/cenario/city.png",
        "/sprites/cenario/cyberpunk-street.png"
    };
    
    private BufferedImage fundoMenu;
    private int selecao = 0;
    private int cooldown = 0;
    private boolean confirmado = false;

    private BufferedImage[] previews = new BufferedImage[4];
    
    public String getMusicaArena() {
        return switch (selecao) {
            case 0 -> util.Sons.MUSICA_CASTELO;
            case 1 -> util.Sons.MUSICA_SCIFILAB;
            case 2 -> util.Sons.MUSICA_CITY;
            case 3 -> util.Sons.MUSICA_CYBERPUNK;
            default -> util.Sons.MUSICA_CASTELO;
        };
    }

    // carrega as previews e o fundo de uma vez só
    public TelaSelecaoArena() {
        // carrega as imagens pequenas das arenas
        for (int i = 0; i < CAMINHOS.length; i++) {
            previews[i] = CarregadorImagens.carregar(CAMINHOS[i]);
        }
        
        // carrega a imagem de fundo com tratamento de erro
        try {
            fundoMenu = ImageIO.read(getClass().getResourceAsStream("/sprites/cenario/espacoAzul.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erro ao carregar o fundo da seleção de arena: " + e.getMessage());
        }
    }

    public void atualizar(CapturaTeclado teclado) {
        if (confirmado) return;
        if (cooldown > 0) { cooldown--; return; }

        if (teclado.j1Esquerda || teclado.j2Esquerda) {
            selecao = (selecao - 1 + NOMES.length) % NOMES.length;
            cooldown = 15;
        }
        if (teclado.j1Direita || teclado.j2Direita) {
            selecao = (selecao + 1) % NOMES.length;
            cooldown = 15;
        }
        if (teclado.confirmar) {
            confirmado = true;
            teclado.confirmar = false;
        }
    }

    public void desenhar(Graphics2D g2) {
        // fundo
    	if (fundoMenu != null) {
    	    g2.drawImage(fundoMenu, 0, 0, Configuracoes.LARGURA_TELA, Configuracoes.ALTURA_TELA, null);
    	} else {
    	    g2.setColor(new Color(15, 10, 35));
    	    g2.fillRect(0, 0, Configuracoes.LARGURA_TELA, Configuracoes.ALTURA_TELA);
    	}
    	// overlay escuro para suavizar
    	g2.setColor(new Color(0, 0, 0, 160));
    	g2.fillRect(0, 0, Configuracoes.LARGURA_TELA, Configuracoes.ALTURA_TELA);

        // título
        g2.setFont(new Font("Serif", Font.BOLD, 36));
        g2.setColor(new Color(255, 200, 50));
        String titulo = "ESCOLHA A ARENA";
        g2.drawString(titulo, (Configuracoes.LARGURA_TELA - g2.getFontMetrics().stringWidth(titulo)) / 2, 60);

        // preview da arena selecionada
        int prevW = 600, prevH = 200;
        int prevX = (Configuracoes.LARGURA_TELA - prevW) / 2;
        int prevY = 90;

        if (previews[selecao] != null) {
            g2.drawImage(previews[selecao], prevX, prevY, prevW, prevH, null);
        } else {
            g2.setColor(new Color(40, 40, 60));
            g2.fillRect(prevX, prevY, prevW, prevH);
            g2.setColor(new Color(80, 80, 100));
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            g2.drawString("(imagem não disponível)", prevX + 200, prevY + 105);
        }
        // borda do preview
        g2.setColor(new Color(200, 180, 100));
        g2.drawRect(prevX, prevY, prevW, prevH);

        // cards dos cenários
        int cardW = 180, cardH = 80;
        int espacamento = 20;
        int totalW = NOMES.length * cardW + (NOMES.length - 1) * espacamento;
        int xInicio = (Configuracoes.LARGURA_TELA - totalW) / 2;
        int yCard = 320;

        for (int i = 0; i < NOMES.length; i++) {
            int xCard = xInicio + i * (cardW + espacamento);
            boolean selecionado = (i == selecao);

            g2.setColor(selecionado ? new Color(60, 50, 20) : new Color(30, 25, 60));
            g2.fillRoundRect(xCard, yCard, cardW, cardH, 10, 10);

            g2.setColor(selecionado ? new Color(255, 200, 50) : new Color(100, 100, 140));
            g2.drawRoundRect(xCard, yCard, cardW, cardH, 10, 10);
            if (selecionado) g2.drawRoundRect(xCard+1, yCard+1, cardW-2, cardH-2, 10, 10);

            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(selecionado ? new Color(255, 220, 80) : Color.WHITE);
            String nome = NOMES[i];
            g2.drawString(nome, xCard + (cardW - g2.getFontMetrics().stringWidth(nome)) / 2, yCard + 47);
        }

        // instrução
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(new Color(180, 180, 180));
        String instr = "A/D ou Setas para navegar   |   ENTER para confirmar";
        g2.drawString(instr, (Configuracoes.LARGURA_TELA - g2.getFontMetrics().stringWidth(instr)) / 2, 450);
    }

    public boolean isConfirmado() { return confirmado; }
    public String getCaminhoArena() { return CAMINHOS[selecao]; }

    public void resetar() {
        selecao    = 0;
        confirmado = false;
        cooldown   = 30;   // absorve input residual
    }
}