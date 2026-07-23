package motor;

import entidade.Lutador;
import estado.EstadoJogo;
import estado.TelaFimDeJogo;
import estado.TelaMenu;
import estado.TelaSelecao;
import estado.TelaSelecaoArena;
import ui.HUD;
import util.Configuracoes;
import util.CarregadorImagens;
import util.GerenciadorAudio;
import util.Sons;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class PainelJogo extends JPanel implements Runnable {

    // game loop
    private Thread threadJogo;
    private final long INTERVALO_NS = 1_000_000_000L / Configuracoes.FPS_ALVO;

    // input
    private final CapturaTeclado teclado = new CapturaTeclado();

    // estado
    private EstadoJogo estadoAtual = EstadoJogo.MENU;

    // telas
    private final TelaMenu         telaMenu         = new TelaMenu();
    private final TelaSelecao      telaSelecao      = new TelaSelecao();
    private final TelaSelecaoArena telaSelecaoArena = new TelaSelecaoArena();
    private final TelaFimDeJogo    telaFimDeJogo    = new TelaFimDeJogo();
    private final HUD              hud              = new HUD();

    // luta
    private Lutador jogador1;
    private Lutador jogador2;
    private final DetectorColisao detectorColisao = new DetectorColisao();

    // cenário
    private BufferedImage imagemCenario;

    // ------------------------------------------------
    //  inicialização

    public PainelJogo() {
        setPreferredSize(new Dimension(Configuracoes.LARGURA_TELA, Configuracoes.ALTURA_TELA));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        addKeyListener(teclado);
        setFocusable(true);

        carregarCenario();
        preCarregarAudio();
        GerenciadorAudio.tocarMusica(Sons.MUSICA_MENU);
    }

    private void carregarCenario() {
        imagemCenario = CarregadorImagens.carregar("/sprites/cenario/castle.png");
    }

    private void preCarregarAudio() {
        // Gerais
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_SOCO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_CHUTE);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_TOMAR_DANO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_NOCAUTE);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_PULAR);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_CARREGAR_TRANSFORMACAO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_TRANSFORMACAO);
        // Kree
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_KREE_PAREDE_CHAMAS);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_KREE_CHAMAS_ATIVAS);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_KREE_DISPARO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_KREE_EXPLOSAO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_KREE_CAMPO_ENERGIA);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_KREE_CHOQUE);
        // Saiyajin
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_SAIYAJIN_TELETRANSPORTE);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_SAIYAJIN_RAJADA);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_SAIYAJIN_TRANSFORMACAO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_SAIYAJIN_ELETRICIDADE);
        // Bumblebee
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_BUMBLEBEE_SALTO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_BUMBLEBEE_IMPACTO_SALTO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_BUMBLEBEE_REDEMOINHO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_BUMBLEBEE_CAPTURA);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_BUMBLEBEE_TELEPORTE);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_BUMBLEBEE_RAIO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_BUMBLEBEE_RAIO_IMPACTO);
        // Grey
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_GREY_BARREIRA);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_GREY_REPULSAO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_GREY_MARTELO_INVOCAR);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_GREY_MARTELO_IMPACTO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_GREY_TRANSFORMACAO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_GREY_BOLA_FOGO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_GREY_BOLA_FOGO_IMPACTO);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_GREY_CAVEIRAS);
        // UI
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_SELECIONAR);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_CONFIRMAR);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_ANUNCIADOR);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_EMPATE);
        GerenciadorAudio.preCarregarEfeito(Sons.EFEITO_ANUNCIADOR_VITORIA);
    }

    // ---------------------------------------------------------------
    //  game loop

    public void iniciarThreadJogo() {
        threadJogo = new Thread(this);
        threadJogo.start();
    }

    @Override
    public void run() {
        long ultimoTempo = System.nanoTime();
        double delta     = 0;

        while (threadJogo != null) {
            long agora = System.nanoTime();
            delta += (double)(agora - ultimoTempo) / INTERVALO_NS;
            ultimoTempo = agora;

            if (delta >= 1) {
                atualizar();
                repaint();
                delta--;
            }

            try { Thread.sleep(1); } catch (InterruptedException ignored) {}
        }
    }

    // ----------------------------------------------------
    //  atualização

    private void atualizar() {
        switch (estadoAtual) {
            case MENU               -> atualizarMenu();
            case SELECAO_PERSONAGEM -> atualizarSelecao();
            case SELECAO_ARENA      -> atualizarSelecaoArena();
            case LUTANDO            -> atualizarLuta();
            case PAUSADO            -> atualizarPausa();
            case FIM_DE_JOGO        -> atualizarFimDeJogo();
        }
    }

    private void atualizarMenu() {
        telaMenu.atualizar(teclado);
        if (teclado.confirmar) {
            teclado.confirmar = false;
            estadoAtual = EstadoJogo.SELECAO_PERSONAGEM;
            GerenciadorAudio.tocarMusica(Sons.MUSICA_SELECAO);
        }
    }

    private void atualizarSelecao() {
        telaSelecao.atualizar(teclado);
        if (telaSelecao.isProntoParaLutar()) {
            jogador1 = telaSelecao.getLutadorJ1();
            jogador2 = telaSelecao.getLutadorJ2();
            telaSelecao.resetar();
            estadoAtual = EstadoJogo.SELECAO_ARENA;
        }
    }

    private void atualizarSelecaoArena() {
        telaSelecaoArena.atualizar(teclado);
        if (telaSelecaoArena.isConfirmado()) {
            imagemCenario = CarregadorImagens.carregar(telaSelecaoArena.getCaminhoArena());
            hud.resetar();
            estadoAtual = EstadoJogo.LUTANDO;
            GerenciadorAudio.tocarMusica(telaSelecaoArena.getMusicaArena());
            GerenciadorAudio.tocarEfeito(Sons.EFEITO_ANUNCIADOR);
        }
    }

    private void atualizarLuta() {
        if (teclado.pausar) {
            teclado.pausar = false;
            estadoAtual = EstadoJogo.PAUSADO;
            GerenciadorAudio.pausarMusica();
            return;
        }

        jogador1.atualizar(teclado, jogador2);
        jogador2.atualizar(teclado, jogador1);
        detectorColisao.verificar(jogador1, jogador2);
        hud.atualizar();

        boolean j1Morreu    = jogador1.estanocauteado();
        boolean j2Morreu    = jogador2.estanocauteado();
        boolean tempoAcabou = hud.getTimerSegundos() <= 0;

        if (j1Morreu || j2Morreu || tempoAcabou) {
            telaFimDeJogo.definirResultado(jogador1, jogador2);
            estadoAtual = EstadoJogo.FIM_DE_JOGO;

            boolean empate = jogador1.getHpAtual() == jogador2.getHpAtual();
            if (empate) {
                GerenciadorAudio.pararMusica();
                GerenciadorAudio.tocarEfeito(Sons.EFEITO_EMPATE);
            } else {
                GerenciadorAudio.tocarMusica(Sons.MUSICA_VITORIA);
                GerenciadorAudio.tocarEfeito(Sons.EFEITO_ANUNCIADOR_VITORIA);
            }
        }
    }

    private void atualizarPausa() {
        if (teclado.pausar) {
            teclado.pausar = false;
            estadoAtual = EstadoJogo.LUTANDO;
            GerenciadorAudio.retomarMusica();
        }
        if (teclado.confirmar) {
            teclado.confirmar = false;
            telaSelecao.resetar();
            telaSelecaoArena.resetar();
            estadoAtual = EstadoJogo.MENU;
            GerenciadorAudio.tocarMusica(Sons.MUSICA_MENU);
        }
    }

    private void atualizarFimDeJogo() {
        if (teclado.confirmar) {
            teclado.confirmar = false;
            telaSelecao.resetar();
            telaSelecaoArena.resetar();
            estadoAtual = EstadoJogo.SELECAO_PERSONAGEM;
            GerenciadorAudio.tocarMusica(Sons.MUSICA_SELECAO);
        }
        if (teclado.pausar) {
            teclado.pausar = false;
            telaSelecao.resetar();
            telaSelecaoArena.resetar();
            estadoAtual = EstadoJogo.MENU;
            GerenciadorAudio.tocarMusica(Sons.MUSICA_MENU);
        }
    }

    // ---------------------------------------------------------------
    //  renderização

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (estadoAtual) {
            case MENU               -> telaMenu.desenhar(g2);
            case SELECAO_PERSONAGEM -> telaSelecao.desenhar(g2);
            case SELECAO_ARENA      -> telaSelecaoArena.desenhar(g2);
            case LUTANDO            -> desenharLuta(g2);
            case PAUSADO            -> { desenharLuta(g2); desenharPausa(g2); }
            case FIM_DE_JOGO        -> { desenharLuta(g2); telaFimDeJogo.desenhar(g2); }
        }

        g2.dispose();
    }

    private void desenharLuta(Graphics2D g2) {
        if (imagemCenario != null) {
            g2.drawImage(imagemCenario, 0, 0,
                    Configuracoes.LARGURA_TELA, Configuracoes.ALTURA_TELA, null);
        } else {
            desenharCenarioFallback(g2);
        }

        if (jogador1 != null) jogador1.desenhar(g2);
        if (jogador2 != null) jogador2.desenhar(g2);

        if (jogador1 != null && jogador2 != null) {
            hud.desenhar(g2, jogador1, jogador2);
        }
    }

    private void desenharCenarioFallback(Graphics2D g2) {
        for (int i = 0; i < Configuracoes.ALTURA_TELA; i++) {
            float t = (float) i / Configuracoes.ALTURA_TELA;
            int r  = (int)(20 + t * 60);
            int gv = (int)(10 + t * 30);
            int b  = (int)(60 + t * 40);
            g2.setColor(new Color(r, gv, b));
            g2.drawLine(0, i, Configuracoes.LARGURA_TELA, i);
        }

        int yChao = Configuracoes.ALTURA_CHAO;
        g2.setColor(new Color(80, 55, 30));
        g2.fillRect(0, yChao, Configuracoes.LARGURA_TELA, Configuracoes.ALTURA_TELA - yChao);
        g2.setColor(new Color(110, 80, 40));
        g2.fillRect(0, yChao, Configuracoes.LARGURA_TELA, 8);

        g2.setColor(new Color(60, 40, 20));
        g2.fillRect(80, yChao - 80, 10, 80);
        g2.setColor(new Color(255, 160, 30, 180));
        g2.fillOval(72, yChao - 100, 26, 30);
        g2.fillRect(Configuracoes.LARGURA_TELA - 90, yChao - 80, 10, 80);
        g2.fillOval(Configuracoes.LARGURA_TELA - 98, yChao - 100, 26, 30);
    }

    private void desenharPausa(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, Configuracoes.LARGURA_TELA, Configuracoes.ALTURA_TELA);

        g2.setColor(Color.WHITE);
        g2.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 64));
        String texto = "PAUSADO";
        int largText = g2.getFontMetrics().stringWidth(texto);
        g2.drawString(texto,
                (Configuracoes.LARGURA_TELA - largText) / 2,
                Configuracoes.ALTURA_TELA / 2);

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
        g2.setColor(new Color(200, 200, 200));
        String sub = "ESC — continuar    ENTER — menu";
        int largSub = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub,
                (Configuracoes.LARGURA_TELA - largSub) / 2,
                Configuracoes.ALTURA_TELA / 2 + 50);
    }
}