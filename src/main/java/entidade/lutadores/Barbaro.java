package entidade.lutadores;

import entidade.AcaoLutador;
import entidade.Lutador;
import entidade.Projetil;
import motor.CapturaTeclado;
import util.CarregadorImagens;
import util.Configuracoes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Barbaro extends Lutador {

    // ---------------------------------------------
    //  especial 1: Barreira de Fogo
	
    private boolean barreirAtiava    = false;
    private int     vidaBarreira     = 0;
    private static final int DURACAO_BARREIRA = 60;
    private static final int DANO_BARREIRA    = 30;
    private static final float FORCA_REPULSAO = 200f;
    private int cooldownDanoBarreira = 0;

    private BufferedImage[] animBarreira;
    private int frameBarreira    = 0;
    private int contadorBarreira = 0;

    // ---------------------------------------------------
    //  especial 2: Golpe de Martelo
    
    private GolpeMartelo golpeMartelo = null;

    private BufferedImage[] animGolpeMartelo; // 12 frames (golpeMartelo_0 a golpeMartelo_11)

    // -----------------------------------------------------------
    //  transformação: Bolas de fogo + Caveiras + Fogo Mágico

    private List<BolaDeFogo> bolasDeFogo = new ArrayList<>();
    private static final int COOLDOWN_BOLA_MAX = 20;
    private int cooldownBola = 0;

    private int anguloCaveiras = 0;

    private BufferedImage[] animBolaDeFogo;
    private BufferedImage[] animCaveira;
    private int frameCaveira    = 0;
    private int contadorCaveira = 0;

    private BufferedImage[] animAuraCarga;
    private int frameAura    = 0;
    private int contadorAura = 0;

    // fogo mágico (aura ao redor durante transformação)
    private BufferedImage[] animFogoMagico;
    private int frameFogo    = 0;
    private int contadorFogo = 0;

    private static final Random random = new Random();

    // --------------------------------------------------------
    //  construtor

    public Barbaro() {
        this.nome         = "Grey";
        this.hpMaximo     = 400;
        this.velocidade   = 5.0f;
        this.danoSoco     = 10;
        this.danoChute    = 15;
        this.danoEspecial = 30;
    }

    @Override
    protected String getPastaSprites() {
        return "/sprites/barbaro/";
    }

    @Override
    protected void carregarSprites() {
        String pasta = getPastaSprites();

        animacoes.put(AcaoLutador.PARADO,       CarregadorImagens.carregarAnimacao(pasta, "parado",        8));
        animacoes.put(AcaoLutador.CORRENDO,      CarregadorImagens.carregarAnimacao(pasta, "correndo",    10));
        animacoes.put(AcaoLutador.PULANDO,       CarregadorImagens.carregarAnimacao(pasta, "pulando",      3));
        animacoes.put(AcaoLutador.SOCOS,         CarregadorImagens.carregarAnimacao(pasta, "socos",        8));
        animacoes.put(AcaoLutador.CHUTE,         CarregadorImagens.carregarAnimacao(pasta, "chute",        6));
        animacoes.put(AcaoLutador.ESPECIAL,      CarregadorImagens.carregarAnimacao(pasta, "especial",     3));
        animacoes.put(AcaoLutador.ESPECIAL2,     CarregadorImagens.carregarAnimacao(pasta, "especial2",    1));
        animacoes.put(AcaoLutador.TOMANDO_DANO,  CarregadorImagens.carregarAnimacao(pasta, "tomando_dano", 3));
        animacoes.put(AcaoLutador.NOCAUTEADO,    CarregadorImagens.carregarAnimacao(pasta, "nocauteado",   6));

        // Barreira de fogo
        try {
            animBarreira = new BufferedImage[5];
            for (int i = 0; i < 5; i++)
                animBarreira[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/sprites/efeitos/barreiraBarbaro_" + i + ".png"));
        } catch (Exception e) {
            System.err.println("Aviso: sprites da barreira não encontrados.");
        }

        // Golpe de Martelo (12 frames)
        try {
            animGolpeMartelo = new BufferedImage[12];
            for (int i = 0; i < 12; i++)
                animGolpeMartelo[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/sprites/efeitos/golpeMartelo_" + i + ".png"));
        } catch (Exception e) {
            System.err.println("Aviso: sprites do golpe de martelo não encontrados.");
        }

        // Bolas de fogo
        try {
            animBolaDeFogo = new BufferedImage[3];
            for (int i = 0; i < 3; i++)
                animBolaDeFogo[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/sprites/efeitos/bolaDeFogoBarbaro_" + i + ".png"));
        } catch (Exception e) {
            System.err.println("Aviso: sprites das bolas de fogo não encontrados.");
        }

        // Caveiras flamejantes
        try {
            animCaveira = new BufferedImage[8];
            for (int i = 0; i < 8; i++)
                animCaveira[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/sprites/efeitos/caveiraBarbaro_" + i + ".png"));
        } catch (Exception e) {
            System.err.println("Aviso: sprites das caveiras não encontrados.");
        }

        // Aura de carregamento
        try {
            animAuraCarga = new BufferedImage[2];
            for (int i = 0; i < 2; i++)
                animAuraCarga[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/sprites/efeitos/auraBarbaro_" + i + ".png"));
        } catch (Exception e) {
            System.err.println("Aviso: sprites da aura não encontrados.");
        }

        // Fogo mágico (aura durante transformação)
        try {
            animFogoMagico = new BufferedImage[4];
            for (int i = 0; i < 4; i++)
                animFogoMagico[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/sprites/efeitos/fogoMagico_" + i + ".png"));
        } catch (Exception e) {
            System.err.println("Aviso: sprites do fogo mágico não encontrados.");
        }
    }

    // ------------------------------------------------
    //  atualização

    @Override
    public void atualizar(CapturaTeclado teclado, Lutador adversario) {
        super.atualizar(teclado, adversario);

        atualizarBarreira(adversario);
        atualizarGolpeMartelo(adversario);
        atualizarTransformacaoEfeitos(adversario);
        atualizarFramesEfeitos();
    }

    private void atualizarBarreira(Lutador adversario) {
        if (!barreirAtiava) return;

        vidaBarreira--;
        if (vidaBarreira <= 0) { barreirAtiava = false; return; }

        if (cooldownDanoBarreira > 0) cooldownDanoBarreira--;

        contadorBarreira++;
        if (contadorBarreira >= 5) {
            contadorBarreira = 0;
            if (animBarreira != null)
                frameBarreira = (frameBarreira + 1) % animBarreira.length;
        }

        Rectangle areaBarreira = new Rectangle(
            (int) x - 40, (int) y - 20,
            largura + 80, altura + 40
        );

        if (areaBarreira.intersects(adversario.getHitbox()) && cooldownDanoBarreira <= 0) {
            adversario.receberDano(DANO_BARREIRA);
            float direcao = adversario.getX() > x ? 1f : -1f;
            adversario.empurrar(direcao * FORCA_REPULSAO);
            cooldownDanoBarreira = 60;
        }

        for (Projetil p : adversario.getProjeteis()) {
            if (!p.isAtivo()) continue;
            if (areaBarreira.intersects(p.getHitbox()))
                p.setAtivo(false);
        }
    }

    private void atualizarGolpeMartelo(Lutador adversario) {
        if (golpeMartelo == null) return;
        golpeMartelo.atualizar(adversario);
        if (!golpeMartelo.isAtivo()) golpeMartelo = null;
    }

    private void atualizarTransformacaoEfeitos(Lutador adversario) {
        if (!transformado) return;

        anguloCaveiras += 3;

        int raio = 80;
        Rectangle areaCaveiras = new Rectangle(
            (int) x - raio, (int) y - raio,
            largura + raio * 2, altura + raio * 2
        );
        if (areaCaveiras.intersects(adversario.getHitbox()))
            adversario.receberDano(5);

        for (Projetil p : adversario.getProjeteis()) {
            if (!p.isAtivo()) continue;
            if (areaCaveiras.intersects(p.getHitbox()))
                p.setAtivo(false);
        }

        cooldownBola--;
        if (cooldownBola <= 0) {
            float xBola = random.nextInt(Configuracoes.LARGURA_TELA);
            bolasDeFogo.add(new BolaDeFogo(xBola, animBolaDeFogo, 8 + random.nextInt(5)));
            cooldownBola = COOLDOWN_BOLA_MAX;
        }

        for (int i = bolasDeFogo.size() - 1; i >= 0; i--) {
            BolaDeFogo b = bolasDeFogo.get(i);
            b.atualizar();
            if (b.getHitbox().intersects(adversario.getHitbox()) && b.isAtiva()) {
                adversario.receberDano(15);
                b.desativar();
            }
            if (!b.isAtiva()) bolasDeFogo.remove(i);
        }
    }

    private void atualizarFramesEfeitos() {
        contadorAura++;
        if (contadorAura >= 6) { contadorAura = 0; frameAura++; }

        contadorCaveira++;
        if (contadorCaveira >= 6) { contadorCaveira = 0; frameCaveira++; }

        contadorFogo++;
        if (contadorFogo >= 8) { contadorFogo = 0; frameFogo++; }
    }

    // --------------------------------------------------------
    //  especiais

    @Override
    public void executarEspecial() {
        barreirAtiava        = true;
        vidaBarreira         = DURACAO_BARREIRA;
        frameBarreira        = 0;
        cooldownDanoBarreira = 0;
    }

    @Override
    public void executarEspecial2() {
        if (golpeMartelo != null) return;

        // spawna na frente do Grey
        float xMartelo = viradoParaDireita ? x + largura : x - GolpeMartelo.MARTELO_W;

        // se estiver no ar, o martelo começa na altura atual do Grey e cai até o chão
        boolean noAr = !noChao;
        float yMartelo = noAr ? y : Configuracoes.ALTURA_CHAO - GolpeMartelo.MARTELO_H;

        golpeMartelo = new GolpeMartelo(xMartelo, yMartelo, noAr, animGolpeMartelo, danoEspecial);
    }

    // --------------------------------------------------------
    //  transformação

    @Override
    protected void ativarTransformacao() {
        bolasDeFogo.clear();
        cooldownBola   = 0;
        anguloCaveiras = 0;
        frameFogo      = 0;
        contadorFogo   = 0;
    }

    @Override
    protected void desativarTransformacao() {
        bolasDeFogo.clear();
    }

    @Override
    public void receberDano(int dano) {
        if (transformado) return;
        super.receberDano(dano);
    }

    @Override
    public void receberDanoRedemoinho(int dano) {
        if (transformado) return;
        super.receberDanoRedemoinho(dano);
    }

    // ------------------------------------------------------
    // renderização

    @Override
    public void desenhar(Graphics2D g2) {
        // aura de carregamento
        if (framesSegurandoTransformacao > 0 && animAuraCarga != null) {
            BufferedImage fa = animAuraCarga[frameAura % animAuraCarga.length];
            if (fa != null)
                g2.drawImage(fa, (int) x - 20, (int) y - 10, largura + 40, altura + 20, null);
        }

        // fogo mágico desenhado antes do sprite para ficar atrás do personagem
        if (transformado && animFogoMagico != null) {
            desenharFogoMagico(g2);
        }

        super.desenhar(g2);

        // Barreira de fogo
        if (barreirAtiava) {
            if (animBarreira != null && animBarreira[frameBarreira] != null) {
                g2.drawImage(animBarreira[frameBarreira],
                    (int) x - 40, (int) y - 20, largura + 80, altura + 40, null);
            } else {
                g2.setColor(new Color(255, 100, 0, 120));
                g2.fillOval((int) x - 40, (int) y - 20, largura + 80, altura + 40);
                g2.setColor(new Color(255, 200, 0, 200));
                g2.drawOval((int) x - 40, (int) y - 20, largura + 80, altura + 40);
            }
        }

        // Golpe de Martelo
        if (golpeMartelo != null) golpeMartelo.desenhar(g2);

        // Bolas de fogo caindo
        for (BolaDeFogo b : bolasDeFogo) b.desenhar(g2);

        // Caveiras orbitando
        if (transformado) desenharCaveiras(g2);
    }

    private void desenharFogoMagico(Graphics2D g2) {
        BufferedImage frame = animFogoMagico[frameFogo % animFogoMagico.length];
        if (frame == null) return;

        int tamanho = 250;
        int fx = (int) x + largura / 2 - tamanho / 2;
        int fy = (int) y + altura - tamanho;

        g2.drawImage(frame, fx, fy, tamanho, tamanho, null);
    }

    private void desenharCaveiras(Graphics2D g2) {
        int raio = 80;
        int cx   = (int) x + largura / 2;
        int cy   = (int) y + altura / 2;
        BufferedImage spriteCaveira = (animCaveira != null)
            ? animCaveira[frameCaveira % animCaveira.length] : null;

        for (int i = 0; i < 3; i++) {
            double angulo    = Math.toRadians(anguloCaveiras + i * 120);
            double cosAngulo = viradoParaDireita ? Math.cos(angulo) : -Math.cos(angulo);
            int cx2 = cx + (int)(raio * cosAngulo);
            int cy2 = cy + (int)(raio * Math.sin(angulo));

            if (spriteCaveira != null) {
                java.awt.geom.AffineTransform at = g2.getTransform();
                if (!viradoParaDireita) {
                    g2.drawImage(spriteCaveira, cx2 - 35, cy2 - 35, 70, 70, null);
                } else {
                    g2.translate(cx2 - 35 + 70, cy2 - 35);
                    g2.scale(-1, 1);
                    g2.drawImage(spriteCaveira, 0, 0, 70, 70, null);
                    g2.setTransform(at);
                }
            } else {
                g2.setColor(new Color(255, 50, 0, 220));
                g2.fillOval(cx2 - 15, cy2 - 15, 30, 30);
                g2.setColor(Color.ORANGE);
                g2.drawOval(cx2 - 15, cy2 - 15, 30, 30);
            }
        }
    }

    // -----------------------------------------------------------
    //  classes internas

    // ---------------------------------------------------------
    //  Golpe de Martelo

    private static class GolpeMartelo {

        static final int MARTELO_W = 250;
        static final int MARTELO_H = 250;

        // dano e área de impacto no chão
        private static final int    AREA_IMPACTO_W  = 200;
        private static final int    AREA_IMPACTO_H  = 60;
        private static final int    DANO_IMPACTO    = 20;   // dano em área (menor que o especial direto)
        private static final float  FORCA_REPULSAO  = 350f;

        // queda (quando invocado no ar)
        private static final float GRAVIDADE       = 1.2f;
        private static final float VEL_QUEDA_MAX   = 20f;

        // velocidade de animação (ticks por frame)
        private static final int TICKS_POR_FRAME_QUEDA    = 3;  // mais rápido durante a queda
        private static final int TICKS_POR_FRAME_IMPACTO  = 5;  // normal no impacto

        private float x, y;
        private boolean noAr;
        private float velY = 0f;
        private boolean impactou = false;
        private boolean ativo    = true;

        private BufferedImage[] frames;
        private int frameAtual     = 0;
        private int contadorFrames = 0;

        // frame a partir do qual o impacto começa (última metade da animação)
        // frames 0–5: martelo descendo / chegando 
        // frames 6–11: impacto no chão
        
        private static final int FRAME_INICIO_IMPACTO = 6;
        private static final int TOTAL_FRAMES         = 12;

        private boolean danoAplicado = false;

        public GolpeMartelo(float x, float y, boolean noAr,
                            BufferedImage[] frames, int danoBase) {
            this.x      = x;
            this.y      = y;
            this.noAr   = noAr;
            this.frames = frames;

            // se já está no chão, começa do frame 0 e percorre a animação completa
            if (!noAr) {
                impactou   = true;
                frameAtual = 0;
            }
        }

        public void atualizar(Lutador adversario) {
            if (!ativo) return;

            if (noAr && !impactou) {
                // fase de queda
                velY = Math.min(velY + GRAVIDADE, VEL_QUEDA_MAX);
                y   += velY;

                // anima os frames de ddescida em loop até pousar
                contadorFrames++;
                if (contadorFrames >= TICKS_POR_FRAME_QUEDA) {
                    contadorFrames = 0;
                    // cicla apenas nos frames pré-impacto
                    frameAtual = (frameAtual + 1) % FRAME_INICIO_IMPACTO;
                }

                // pouso: quando a base do martelo toca o chão
                if (y + MARTELO_H >= Configuracoes.ALTURA_CHAO) {
                    y        = Configuracoes.ALTURA_CHAO - MARTELO_H;
                    impactou = true;
                    frameAtual = FRAME_INICIO_IMPACTO; // começa a animação de impacto
                    contadorFrames = 0;
                }

            } else {
                // fase de impacto
                contadorFrames++;
                if (contadorFrames >= TICKS_POR_FRAME_IMPACTO) {
                    contadorFrames = 0;
                    frameAtual++;
                    if (frameAtual >= TOTAL_FRAMES) {
                        ativo = false;
                        return;
                    }
                }

                // cancela projéteis do adversário que baterem no martelo
                Rectangle hitboxMartelo = new Rectangle((int) x, (int) y, MARTELO_W, MARTELO_H);
                for (Projetil p : adversario.getProjeteis()) {
                    if (p.isAtivo() && hitboxMartelo.intersects(p.getHitbox()))
                        p.setAtivo(false);
                }

                // aplica dano + repulsão uma única vez, no primeiro frame do impacto
                if (!danoAplicado && frameAtual == FRAME_INICIO_IMPACTO) {
                    danoAplicado = true;
                    Rectangle areaImpacto = getAreaImpacto();
                    if (areaImpacto.intersects(adversario.getHitbox())) {
                        adversario.receberDano(DANO_IMPACTO);
                        // repele para o lado oposto ao martelo
                        float direcao = adversario.getX() > x ? 1f : -1f;
                        adversario.empurrar(direcao * FORCA_REPULSAO);
                    }
                }
            }
        }

        // área de impacto no chão, centralizada abaixo do martelo
        private Rectangle getAreaImpacto() {
            int cx = (int) x + MARTELO_W / 2;
            return new Rectangle(
                cx - AREA_IMPACTO_W / 2,
                (int) y + MARTELO_H - AREA_IMPACTO_H / 2,
                AREA_IMPACTO_W,
                AREA_IMPACTO_H
            );
        }

        public void desenhar(Graphics2D g2) {
            if (!ativo) return;

            int fi = Math.min(frameAtual, TOTAL_FRAMES - 1);

            if (frames != null && frames.length > fi && frames[fi] != null) {
                g2.drawImage(frames[fi], (int) x, (int) y, MARTELO_W, MARTELO_H, null);
            } else {
                // Fallback visual
                g2.setColor(new Color(180, 180, 180, 220));
                g2.fillRect((int) x + MARTELO_W / 2 - 10, (int) y, 20, MARTELO_H - 20);
                g2.fillRect((int) x, (int) y + 10, MARTELO_W, 30);
                // onda de choque no impacto
                if (impactou) {
                    g2.setColor(new Color(255, 255, 100, 150));
                    g2.fillOval((int) x - AREA_IMPACTO_W / 4,
                                (int) y + MARTELO_H - 20,
                                MARTELO_W + AREA_IMPACTO_W / 2, 40);
                }
            }
        }

        public boolean isAtivo() { return ativo; }
    }

    // ---------------------------------------------------------
    //  Bola de Fogo (transformação)
    
    private static class BolaDeFogo {
        private float x, y;
        private float velY;
        private boolean ativa  = true;
        private int largura    = 110;
        private int altura     = 110;

        private BufferedImage[] frames;
        private int frameAtual     = 0;
        private int contadorFrames = 0;

        public BolaDeFogo(float x, BufferedImage[] frames, float velY) {
            this.x      = x;
            this.y      = -60;
            this.frames = frames;
            this.velY   = velY;
        }

        public void atualizar() {
            if (!ativa) return;
            y += velY;
            if (y > Configuracoes.ALTURA_CHAO) { ativa = false; return; }

            contadorFrames++;
            if (contadorFrames >= 5) {
                contadorFrames = 0;
                if (frames != null && frames.length > 0)
                    frameAtual = (frameAtual + 1) % frames.length;
            }
        }

        public void desenhar(Graphics2D g2) {
            if (!ativa) return;
            if (frames != null && frames.length > 0 && frames[frameAtual] != null) {
                g2.drawImage(frames[frameAtual], (int) x, (int) y, largura, altura, null);
            } else {
                g2.setColor(new Color(255, 120, 0, 220));
                g2.fillOval((int) x, (int) y, largura, altura);
                g2.setColor(Color.YELLOW);
                g2.drawOval((int) x, (int) y, largura, altura);
            }
        }

        public Rectangle getHitbox() {
            return new Rectangle((int) x + 10, (int) y + 10, largura - 20, altura - 20);
        }

        public boolean isAtiva() { return ativa; }
        public void desativar()  { ativa = false; }
    }
}