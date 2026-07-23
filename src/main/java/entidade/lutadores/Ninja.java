package entidade.lutadores;

import entidade.AcaoLutador;
import entidade.Lutador;
import entidade.Projetil;
import util.CarregadorImagens;
import util.GerenciadorAudio;
import util.Sons;
import util.Configuracoes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.IOException;

public class Ninja extends Lutador {

    private Lutador adversarioRef;

    // sprites de efeitos DBZ
    private BufferedImage[] animAuraCarga;
    private BufferedImage[] animEletricidade;
    private BufferedImage[] animRajadaEnergia;
    private BufferedImage[] animTrovao;

    // contadores de animação independentes
    private int frameAura = 0;
    private int contadorAura = 0;
    private int frameEletricidade = 0;
    private int contadorEletricidade = 0;
    private int frameTrovao = 0;
    private int contadorTrovao = 0;

    // variáveis para salvar os status originais
    private int danoSocoOriginal;
    private float velocidadeOriginal;

    // clip dedicado ao loop de eletricidade da transformação
    private Clip clipEletricidade = null;

    public Ninja() {
        this.nome             = "Saiyajin";
        this.hpMaximo         = 400;
        this.velocidade       = 5.0f;
        this.danoSoco         = 10;
        this.danoChute        = 15;
        this.danoEspecial     = 30;
        this.velocidadeOriginal = this.velocidade;
        this.danoSocoOriginal   = this.danoSoco;
    }

    @Override
    protected String getPastaSprites() {
        return "/sprites/ninja/";
    }

    @Override
    protected void carregarSprites() {
        String pasta = getPastaSprites();

        animacoes.put(AcaoLutador.PARADO,       CarregadorImagens.carregarAnimacao(pasta, "parado",        8));
        animacoes.put(AcaoLutador.CORRENDO,      CarregadorImagens.carregarAnimacao(pasta, "correndo",    10));
        animacoes.put(AcaoLutador.PULANDO,       CarregadorImagens.carregarAnimacao(pasta, "pulando",      3));
        animacoes.put(AcaoLutador.SOCOS,         CarregadorImagens.carregarAnimacao(pasta, "socos",        8));
        animacoes.put(AcaoLutador.CHUTE,         CarregadorImagens.carregarAnimacao(pasta, "chute",        6));
        animacoes.put(AcaoLutador.ESPECIAL,      CarregadorImagens.carregarAnimacao(pasta, "especial",     4));
        animacoes.put(AcaoLutador.TOMANDO_DANO,  CarregadorImagens.carregarAnimacao(pasta, "tomando_dano", 3));
        animacoes.put(AcaoLutador.NOCAUTEADO,    CarregadorImagens.carregarAnimacao(pasta, "nocauteado",   6));
        animacoes.put(AcaoLutador.ESPECIAL2,     CarregadorImagens.carregarAnimacao(pasta, "especial2",    1));

        try {
            animAuraCarga = new BufferedImage[4];
            animAuraCarga[0] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraAzulNovo_0.png"));
            animAuraCarga[1] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraAzulNovo_1.png"));
            animAuraCarga[2] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraAzulNovo_0.png"));
            animAuraCarga[3] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraAzulNovo_1.png"));

            animRajadaEnergia = new BufferedImage[3];
            animRajadaEnergia[0] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/rajadaAzul_0.png"));
            animRajadaEnergia[1] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/rajadaAzul_1.png"));
            animRajadaEnergia[2] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/rajadaAzul_2.png"));

            animEletricidade = new BufferedImage[8];
            for (int i = 0; i < 8; i++)
                animEletricidade[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/sprites/efeitos/eletricidade_" + i + ".png"));

            animTrovao = new BufferedImage[8];
            for (int i = 0; i < 8; i++)
                animTrovao[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/sprites/efeitos/trovao_" + i + ".png"));

        } catch (Exception e) {
            System.err.println("Aviso: Falha ao carregar efeitos individuais do DBZ - " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    //  loop de eletricidade

    private void iniciarLoopEletricidade() {
        try {
            var is = getClass().getResourceAsStream(Sons.EFEITO_SAIYAJIN_ELETRICIDADE);
            if (is == null) {
                System.err.println("[Audio] Efeito não encontrado: " + Sons.EFEITO_SAIYAJIN_ELETRICIDADE);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                new java.io.BufferedInputStream(is));
            clipEletricidade = AudioSystem.getClip();
            clipEletricidade.open(audioStream);
            clipEletricidade.loop(Clip.LOOP_CONTINUOUSLY);
            clipEletricidade.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("[Audio] Erro ao iniciar eletricidade: " + e.getMessage());
        }
    }

    private void pararLoopEletricidade() {
        if (clipEletricidade != null) {
            clipEletricidade.stop();
            clipEletricidade.close();
            clipEletricidade = null;
        }
    }

    // ----------------------------------------------------
    //  mecânica de transformação

    @Override
    protected void ativarTransformacao() {
        this.danoSoco   = this.danoSocoOriginal * 2;
        this.velocidade = this.velocidadeOriginal + 10.0f;
        iniciarLoopEletricidade();
    }

    @Override
    protected void desativarTransformacao() {
        this.danoSoco   = this.danoSocoOriginal;
        this.velocidade = this.velocidadeOriginal;
        pararLoopEletricidade();
    }

    // ---------------------------------------------------------------
    //  especiais e projéteis

    @Override
    public void executarEspecial() {
        if (adversarioRef == null) return;

        float xAdv = adversarioRef.getX();

        if (viradoParaDireita) {
            x = xAdv + adversarioRef.getLargura() + 10;
        } else {
            x = xAdv - largura - 10;
        }

        if (x < 0) x = 0;
        if (x + largura > Configuracoes.LARGURA_TELA)
            x = Configuracoes.LARGURA_TELA - largura;

        GerenciadorAudio.tocarEfeito(Sons.EFEITO_SAIYAJIN_TELETRANSPORTE);
    }

    @Override
    public void executarEspecial2() {
        float velTiro = viradoParaDireita ? 10f : -10f;
        float xTiro   = viradoParaDireita ? this.x + largura - 20 : this.x - 40;
        float yTiro   = this.y + (altura / 3);

        int larguraTiro = 80;
        int alturaTiro  = 60;
        int danoTiro    = transformado ? 50 : 25;

        Projetil energia = new Projetil(xTiro, yTiro, velTiro, larguraTiro, alturaTiro, danoTiro, viradoParaDireita, animRajadaEnergia);
        this.projeteis.add(energia);
        GerenciadorAudio.tocarEfeito(Sons.EFEITO_SAIYAJIN_RAJADA);
    }

    public void setAdversario(Lutador adversario) {
        this.adversarioRef = adversario;
    }

    // --------------------------------------------------------
    //  renderização em camadas
    //  aura -> trovão -> sprite -> eletricidade

    @Override
    public void desenhar(Graphics2D g2) {
        atualizarFramesEfeitos();

        // 1. Aura de carregamento (atrás de tudo)
        if (framesSegurandoTransformacao > 0 && animAuraCarga != null && animAuraCarga.length > 0) {
            BufferedImage frameAuraAtual = animAuraCarga[frameAura % animAuraCarga.length];
            g2.drawImage(frameAuraAtual, (int) x - 20, (int) y - 10, largura + 40, altura + 20, null);
        }

        // 2. Trovão ao redor (atrás do personagem e da eletricidade)
        if (transformado && animTrovao != null && animTrovao.length > 0) {
            BufferedImage frameTrovaoAtual = animTrovao[frameTrovao % animTrovao.length];
            if (frameTrovaoAtual != null) {
                int tw = largura + 60;
                int th = altura  + 60;
                int tx = (int) x - 30;
                int ty = (int) y - 30;
                if (viradoParaDireita) {
                    g2.drawImage(frameTrovaoAtual, tx, ty, tw, th, null);
                } else {
                    g2.drawImage(frameTrovaoAtual, tx + tw, ty, -tw, th, null);
                }
            }
        }

        // 3. Sprite principal do personagem
        super.desenhar(g2);

        // 4. Eletricidade por cima de tudo
        if (transformado && animEletricidade != null && animEletricidade.length > 0) {
            BufferedImage frameRaio = animEletricidade[frameEletricidade % animEletricidade.length];
            int offsetBaixo = 45;
            if (viradoParaDireita) {
                g2.drawImage(frameRaio, (int) x, (int) y + offsetBaixo, largura, altura, null);
            } else {
                g2.drawImage(frameRaio, (int) x + largura, (int) y + offsetBaixo, -largura, altura, null);
            }
        }
    }

    private void atualizarFramesEfeitos() {
        contadorAura++;
        if (contadorAura >= 6) {
            contadorAura = 0;
            frameAura++;
        }

        contadorEletricidade++;
        if (contadorEletricidade >= 5) {
            contadorEletricidade = 0;
            frameEletricidade++;
        }

        contadorTrovao++;
        if (contadorTrovao >= 7) {
            contadorTrovao = 0;
            frameTrovao++;
        }
    }
}