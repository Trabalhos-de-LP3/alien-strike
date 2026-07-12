package estado;

import entidade.Lutador;
import entidade.lutadores.Barbaro;
import entidade.lutadores.Guerreiro;
import entidade.lutadores.Mago;
import entidade.lutadores.Ninja;
import motor.CapturaTeclado;
import util.GerenciadorAudio;
import util.Sons;
import util.Configuracoes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;


// tela de seleção de personagem para os dois jogadores
// Jogador 1 navega com A/D e confirma com G
// Jogador 2 navega com Setas e confirma com 1
// quando ambos confirmam, a luta começa
 
public class TelaSelecao {

    private static final String[] NOMES = { "Kree", "Saiyajin", "Bumblebee", "Grey" };
    private static final String[] DESCRICOES = {
        "Parede de Chamas. Tiro Explosivo. Barreira Psiônica.",
        "Teletransporte. Rajada de Energia. Modo Berserker.",
        "Salto Relâmpago. Tornado. Chuva de Trovões.",
        "Barreira de Fogo. Martelo Demoníaco. Chuva Flamejante."
    };
    
    private BufferedImage spriteGuerreiro; // Kree
    private BufferedImage spriteNinja;     // Saiyajin
    private BufferedImage spriteMago;      // Bumblebee
    private BufferedImage spriteBarbaro;   // Grey
    private BufferedImage fundoMenu;
    
    private int selecaoJ1 = 0;
    private int selecaoJ2 = 1;
    private boolean j1Confirmou = false;
    private boolean j2Confirmou = false;
    
    public TelaSelecao() {
        carregarImagensPersonagens(); 
    }

    // cooldown para evitar múltiplas seleções por tecla
    private int cooldownJ1 = 0;
    private int cooldownJ2 = 0;

    private Lutador lutadorJ1;
    private Lutador lutadorJ2;
    private boolean prontoParaLutar = false;

    public void atualizar(CapturaTeclado teclado) {
        if (cooldownJ1 > 0) cooldownJ1--;
        if (cooldownJ2 > 0) cooldownJ2--;

        // navegação Jogador 1
        if (!j1Confirmou && cooldownJ1 == 0) {
            if (teclado.j1Esquerda) { selecaoJ1 = (selecaoJ1 - 1 + 4) % 4; cooldownJ1 = 15; GerenciadorAudio.tocarEfeito(Sons.EFEITO_SELECIONAR); }
            if (teclado.j1Direita)  { selecaoJ1 = (selecaoJ1 + 1) % 4;      cooldownJ1 = 15; GerenciadorAudio.tocarEfeito(Sons.EFEITO_SELECIONAR); }
            if (teclado.j1Soco)     { j1Confirmou = true; GerenciadorAudio.tocarEfeito(Sons.EFEITO_CONFIRMAR); }
        }

        // navegação Jogador 2
        if (!j2Confirmou && cooldownJ2 == 0) {
            if (teclado.j2Esquerda) { selecaoJ2 = (selecaoJ2 - 1 + 4) % 4; cooldownJ2 = 15; GerenciadorAudio.tocarEfeito(Sons.EFEITO_SELECIONAR); }
            if (teclado.j2Direita)  { selecaoJ2 = (selecaoJ2 + 1) % 4;      cooldownJ2 = 15; GerenciadorAudio.tocarEfeito(Sons.EFEITO_SELECIONAR); }
            if (teclado.j2Soco)     { j2Confirmou = true; GerenciadorAudio.tocarEfeito(Sons.EFEITO_CONFIRMAR); }
        }

        // ambos confirmaram
        if (j1Confirmou && j2Confirmou && !prontoParaLutar) {
            lutadorJ1 = criarLutador(selecaoJ1);
            lutadorJ2 = criarLutador(selecaoJ2);

            lutadorJ1.inicializar(Configuracoes.X_JOGADOR1, Configuracoes.Y_INICIAL, true);
            lutadorJ2.inicializar(Configuracoes.X_JOGADOR2, Configuracoes.Y_INICIAL, false);

            // Ninja precisa da referência do adversário para o teleporte
            if (lutadorJ1 instanceof Ninja) ((Ninja) lutadorJ1).setAdversario(lutadorJ2);
            if (lutadorJ2 instanceof Ninja) ((Ninja) lutadorJ2).setAdversario(lutadorJ1);

            prontoParaLutar = true;
        }
    }

    private Lutador criarLutador(int indice) {
        return switch (indice) {
            case 0 -> new Guerreiro();
            case 1 -> new Ninja();
            case 2 -> new Mago();
            case 3 -> new Barbaro();
            default -> new Guerreiro();
        };
    }

    public void desenhar(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
        String titulo = "ESCOLHA SEU LUTADOR";
        g2.drawString(titulo, (Configuracoes.LARGURA_TELA - g2.getFontMetrics().stringWidth(titulo)) / 2, 60);

        // cards dos personagens
        int cardLargura = 200;
        int cardAltura  = 220;
        int espacamento = 20;
        int totalLargura = 4 * cardLargura + 3 * espacamento;
        int xInicio = (Configuracoes.LARGURA_TELA - totalLargura) / 2;
        int yCard   = 100;

        for (int i = 0; i < 4; i++) {
            int xCard = xInicio + i * (cardLargura + espacamento);

            boolean selecionadoPorJ1 = (selecaoJ1 == i);
            boolean selecionadoPorJ2 = (selecaoJ2 == i);

            // fundo do card
            Color corFundo = new Color(30, 25, 60);
            if (selecionadoPorJ1 && selecionadoPorJ2) corFundo = new Color(80, 60, 20);
            else if (selecionadoPorJ1)                corFundo = new Color(20, 50, 90);
            else if (selecionadoPorJ2)                corFundo = new Color(90, 20, 20);

            g2.setColor(corFundo);
            g2.fillRoundRect(xCard, yCard, cardLargura, cardAltura, 12, 12);

            // borda de seleção
            if (selecionadoPorJ1) {
                g2.setColor(j1Confirmou ? new Color(50, 255, 100) : new Color(100, 180, 255));
                g2.drawRoundRect(xCard, yCard, cardLargura, cardAltura, 12, 12);
                g2.drawRoundRect(xCard + 1, yCard + 1, cardLargura - 2, cardAltura - 2, 12, 12);
            }
            if (selecionadoPorJ2) {
                g2.setColor(j2Confirmou ? new Color(50, 255, 100) : new Color(255, 100, 100));
                g2.drawRoundRect(xCard + 2, yCard + 2, cardLargura - 4, cardAltura - 4, 12, 12);
            }

            // área do sprite (placeholder)
            g2.setColor(new Color(50, 45, 80));
            g2.fillRoundRect(xCard + 10, yCard + 10, cardLargura - 20, 140, 8, 8);
            
            // lógica para desenhar a imagem correspondente ao índice
            BufferedImage spriteAtual = switch (i) {
                case 0 -> spriteGuerreiro;
                case 1 -> spriteNinja;
                case 2 -> spriteMago;
                case 3 -> spriteBarbaro;
                default -> null;
            };

            if (spriteAtual != null) {
                // desenha a imagem mantendo a nitidez do pixel art
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                int larguraImg = 96; 
                int alturaImg = 128;
                int xCentroImg = xCard + 10 + ((cardLargura - 20) / 2) - (larguraImg / 2);
                int yCentroImg = yCard + 10 + (140 / 2) - (alturaImg / 2);
                g2.drawImage(spriteAtual, xCentroImg, yCentroImg, larguraImg, alturaImg, null);
            } else {
                g2.setFont(new Font("Arial", Font.BOLD, 48));
                g2.setColor(new Color(100, 90, 130));
                g2.drawString(NOMES[i].substring(0, 1), xCard + cardLargura / 2 - 18, yCard + 90);
            }

            // nome
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(Color.WHITE);
            String nome = NOMES[i];
            g2.drawString(nome, xCard + (cardLargura - g2.getFontMetrics().stringWidth(nome)) / 2, yCard + 170);

            // indicadores de quem selecionou
            int yInd = yCard + 190;
            if (selecionadoPorJ1) {
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.setColor(new Color(100, 180, 255));
                g2.drawString("J1", xCard + 20, yInd);
            }
            if (selecionadoPorJ2) {
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.setColor(new Color(255, 100, 100));
                g2.drawString("J2", xCard + cardLargura - 35, yInd);
            }
        }

        // descrição do personagem selecionado por J1
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(new Color(150, 200, 255));
        g2.drawString("J1: " + DESCRICOES[selecaoJ1], 30, 370);
        g2.setColor(new Color(255, 150, 150));
        g2.drawString("J2: " + DESCRICOES[selecaoJ2], 30, 395);

        // instruções
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(new Color(180, 180, 180));
        g2.drawString("J1: A/D para navegar | G para confirmar", 30, 440);
        g2.drawString("J2: Setas para navegar | 1 para confirmar", 30, 460);

        if (j1Confirmou && j2Confirmou) {
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.setColor(new Color(255, 220, 50));
            String msg = "LUTA!";
            g2.drawString(msg, (Configuracoes.LARGURA_TELA - g2.getFontMetrics().stringWidth(msg)) / 2, 520);
        }
    }

    public boolean isProntoParaLutar() { return prontoParaLutar; }
    public Lutador getLutadorJ1()      { return lutadorJ1; }
    public Lutador getLutadorJ2()      { return lutadorJ2; }

    public void resetar() {
        j1Confirmou     = false;
        j2Confirmou     = false;
        prontoParaLutar = false;
        lutadorJ1       = null;
        lutadorJ2       = null;
        selecaoJ1       = 0;
        selecaoJ2       = 1;
        cooldownJ1      = 30;
        cooldownJ2      = 30;
    }

    private void carregarImagensPersonagens() {
        try {
            spriteGuerreiro = ImageIO.read(getClass().getResourceAsStream("/sprites/guerreiro/parado_0.png"));
            spriteNinja = ImageIO.read(getClass().getResourceAsStream("/sprites/ninja/parado_1.png"));
            spriteMago = ImageIO.read(getClass().getResourceAsStream("/sprites/mago/parado_0.png"));
            spriteBarbaro = ImageIO.read(getClass().getResourceAsStream("/sprites/barbaro/parado_1.png"));
            
            fundoMenu = ImageIO.read(getClass().getResourceAsStream("/sprites/cenario/espacoAzul.png"));
            
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erro ao carregar imagens da tela de seleção: " + e.getMessage());
        }
    }   
}