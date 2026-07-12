package entidade.lutadores;

import entidade.AcaoLutador;
import entidade.Lutador;
import entidade.Projetil;
import entidade.ProjetilRedemoinho;
import motor.CapturaTeclado;
import util.CarregadorImagens;
import util.Configuracoes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import entidade.RaioEletrico;
import java.util.ArrayList;
import java.util.List;

public class Mago extends Lutador {

    // sprites de efeitos
    private BufferedImage[] animAuraCarga;
    private BufferedImage[] animProjetilEspecial2;
    
    private BufferedImage[] animEfeitoTransformado;
    private int frameEfeito    = 0;
    private int contadorEfeito = 0;

    // contadores de animação independentes
    private int frameAura    = 0;
    private int contadorAura = 0;

    // mecânica especial 1
    private boolean especialAtivo = false;
    
    private boolean subindoEspecial1  = false;
    private boolean jaDeuDanoEspecial1 = false;
    private BufferedImage[] animTornado;
    private int frameTornado    = 0;
    private int contadorTornado = 0;
    private int cooldownEspecial1 = 0;
    private static final int COOLDOWN_ESPECIAL1_MAX = 100; // 1.5 segundos

    // status originais para restaurar após transformação
    private int danoEspecialOriginal;
    
 // transformação: voo
    private static final float Y_VOO = 80f; // altura fixa no topo da arena
    private static final int COOLDOWN_RAIO_MAX = 8; // raio a cada 8 frames
    private int cooldownRaio = 0;

    // raios elétricos
    private List<RaioEletrico> raios = new ArrayList<>();
    private BufferedImage[] animRaio;
    private BufferedImage[] animVoando; // sprites de voo da transformação

    public Mago() {
        this.nome         = "Bumblebee";
        this.hpMaximo     = 400;
        this.velocidade   = 5.0f;
        this.danoSoco     = 10;
        this.danoChute    = 15;
        this.danoEspecial = 30;

        this.danoEspecialOriginal = this.danoEspecial;
    }

    @Override
    protected String getPastaSprites() {
        return "/sprites/mago/";
    }

    @Override
    protected void carregarSprites() {
        String pasta = getPastaSprites();

        animacoes.put(AcaoLutador.PARADO,       CarregadorImagens.carregarAnimacao(pasta, "parado",       8));
        animacoes.put(AcaoLutador.CORRENDO,     CarregadorImagens.carregarAnimacao(pasta, "correndo",    10));
        animacoes.put(AcaoLutador.PULANDO,      CarregadorImagens.carregarAnimacao(pasta, "pulando",      3));
        animacoes.put(AcaoLutador.SOCOS,        CarregadorImagens.carregarAnimacao(pasta, "socos",        8));
        animacoes.put(AcaoLutador.CHUTE,        CarregadorImagens.carregarAnimacao(pasta, "chute",        6));
        animacoes.put(AcaoLutador.ESPECIAL,     CarregadorImagens.carregarAnimacao(pasta, "especial",     2));
        animacoes.put(AcaoLutador.ESPECIAL2,    CarregadorImagens.carregarAnimacao(pasta, "especial2",    1));
        animacoes.put(AcaoLutador.TOMANDO_DANO, CarregadorImagens.carregarAnimacao(pasta, "tomando_dano", 3));
        animacoes.put(AcaoLutador.NOCAUTEADO,   CarregadorImagens.carregarAnimacao(pasta, "nocauteado",   6));
        animacoes.put(AcaoLutador.VOANDO, CarregadorImagens.carregarAnimacao(pasta, "voando", 2));

        // efeitos
        try {
            // aura de carregamento da transformação
            animAuraCarga = new BufferedImage[4];
            animAuraCarga[0] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraRoxaNovo_0.png"));
            animAuraCarga[1] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraRoxaNovo_1.png"));
            animAuraCarga[2] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraRoxaNovo_0.png"));
            animAuraCarga[3] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraRoxaNovo_1.png"));
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao carregar aura do Bumblebee - " + e.getMessage());
        }

        try {
            animProjetilEspecial2 = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                animProjetilEspecial2[i] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/projetilMago_" + i + ".png"));
            }
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao carregar projétil do Bumblebee - " + e.getMessage());
        }
        
        try {
            // raio elétrico (fallback visual até ter os sprites)
            animRaio = new BufferedImage[5];
            for (int i = 0; i < 5; i++) {
                animRaio[i] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/raioMago_" + i + ".png"));
            }
        } catch (Exception e) {
            System.err.println("Aviso: sprites do raio não encontrados, usando fallback.");
        }
        
        try {
            animEfeitoTransformado = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                animEfeitoTransformado[i] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/transformadoMago_" + i + ".png"));
            }
        } catch (Exception e) {
            System.err.println("Aviso: sprites do efeito transformado não encontrados.");
        }
        
        try {
            animTornado = new BufferedImage[5];
            for (int i = 0; i < 5; i++) {
                animTornado[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/sprites/efeitos/impulsoRaio_" + i + ".png"));
            }
        } catch (Exception e) {
            System.err.println("Aviso: sprites do tornado não encontrados.");
        }
    }

    // -------------------------------------------------------------
    // atualização

    @Override
    public void atualizar(CapturaTeclado teclado, Lutador adversario) {
        if (transformado) {
        	
            // trava no topo (zera gravidade)
            this.y    = Y_VOO;
            this.velY = 0;

            // permite só movimento horizontal
            boolean moverEsq = ehJogador1 ? teclado.j1Esquerda : teclado.j2Esquerda;
            boolean moverDir = ehJogador1 ? teclado.j1Direita  : teclado.j2Direita;
            boolean dispararRaio = ehJogador1 ? teclado.j1Transformar : teclado.j2Transformar;

            velX = 0;
            if (moverEsq) velX = -velocidade;
            if (moverDir) velX =  velocidade;

            x += velX;
            if (x < 0) x = 0;
            if (x + largura > util.Configuracoes.LARGURA_TELA)
                x = util.Configuracoes.LARGURA_TELA - largura;
            
            velX = 0;
            if (moverEsq) { velX = -velocidade; acaoAtual = AcaoLutador.VOANDO; }
            else if (moverDir) { velX =  velocidade; acaoAtual = AcaoLutador.VOANDO; }
            else { acaoAtual = AcaoLutador.VOANDO; } // parado no ar também usa VOANDO

            // disparar raio
            if (dispararRaio && cooldownRaio <= 0) {
                raios.add(new RaioEletrico(
                    x + largura / 2f,
                    (int)(y + altura),
                    danoEspecial / 3,
                    animRaio
                ));
                cooldownRaio = COOLDOWN_RAIO_MAX;
            }
            if (cooldownRaio > 0) cooldownRaio--;

            // atualiza e verifica dano dos raios
            for (int i = raios.size() - 1; i >= 0; i--) {
                RaioEletrico r = raios.get(i);
                r.atualizar();
                r.checarDano(adversario);
                if (!r.isAtivo()) raios.remove(i);
            }

            // atualiza timers de transformação manualmente
            atualizarTimersTransformacao();
            atualizarAnimacao();
            virarParaAdversario(adversario);

        } else {
        		
        	// checa dano do especial 1 durante a subida
        	if (subindoEspecial1) {
        	    if (velY >= 0) {
        	        // começou a descer, encerra a fase de dano
        	        subindoEspecial1 = false;
        	    } else if (!jaDeuDanoEspecial1) {
        	        checarDanoEspecial1(adversario);
        	    }
        	}
        		if (cooldownEspecial1 > 0) cooldownEspecial1--;
            super.atualizar(teclado, adversario);
        }

        atualizarFramesEfeitos();
    }
    
    private void checarDanoEspecial1(Lutador adversario) {
        // área maior ao redor do personagem durante o salto
        int margemArea = 80;
        java.awt.Rectangle areaEspecial = new java.awt.Rectangle(
            (int) x - margemArea,
            (int) y - margemArea / 2,
            largura + margemArea * 2,
            altura  + margemArea
        );

        if (areaEspecial.intersects(adversario.getHitbox())) {
            adversario.receberDano(danoEspecial);
            jaDeuDanoEspecial1 = true;
        }
    }
    
    private void atualizarTimersTransformacao() {
        if (timerInvencivel > 0) timerInvencivel--;
        if (transformado) {
            timerTransformacaoAtiva--;
            if (timerTransformacaoAtiva <= 0) {
                transformado = false;
                cooldownTransformacao = COOLDOWN_MAX_TRANSFORMACAO;
                desativarTransformacao();
            }
        }
    }

    // ------------------------------------------------------------
    //  especiais

    @Override
    public void executarEspecial() {
        if (cooldownEspecial1 > 0) return;

        velY = Configuracoes.FORCA_PULO * 1.4f;
        noChao = false;
        subindoEspecial1   = true;
        jaDeuDanoEspecial1 = false;
        cooldownEspecial1  = COOLDOWN_ESPECIAL1_MAX;
        acaoAtual = AcaoLutador.PULANDO;
    }

    @Override
    protected void atualizarAtackBox() {
        if (!estaAtacando()) {
            attackBox.setBounds(0, 0, 0, 0);
            especialAtivo = false;
            return;
        }

        int alcance  = especialAtivo ? 140 : 70;
        int altCaixa = especialAtivo ?  80 : 50;
        int yAtaque  = (int) y + altura / 3;

        if (viradoParaDireita) {
            attackBox.setBounds((int) x + largura, yAtaque, alcance, altCaixa);
        } else {
            attackBox.setBounds((int) x - alcance, yAtaque, alcance, altCaixa);
        }
    }

    @Override
    public void executarEspecial2() {
        float velTiro = viradoParaDireita ? 5f : -5f;
        float xTiro   = viradoParaDireita ? x + largura : x - 80;
        float yTiro = y + altura / 4;

        ProjetilRedemoinho redemoinho = new ProjetilRedemoinho(
            xTiro, yTiro, velTiro,
            150, 150,           // largura e altura do redemoinho
            danoEspecial,     // dano de captura
            viradoParaDireita,
            animProjetilEspecial2,
            100               // tempo de vida: 2,5 segundos
        );
        projeteis.add(redemoinho);
    }

    // -------------------------------------------------
    //  transformação

    @Override
    protected void ativarTransformacao() {
        this.danoEspecialOriginal = this.danoEspecial;
        this.danoEspecial += 30;

        // teleporta para o topo da arena e trava lá
        this.y    = Y_VOO;
        this.velY = 0;
        this.noChao = false;
    }

    @Override
    protected void desativarTransformacao() {
        this.danoEspecial = this.danoEspecialOriginal;
        // cai normalmente ao fim da transformação
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

    // ------------------------------------------------
    //  renderização

    @Override
    public void desenhar(Graphics2D g2) {
        atualizarFramesEfeitos();

        // aura de carregamento
        if (framesSegurandoTransformacao > 0 && animAuraCarga != null && animAuraCarga.length > 0) {
            BufferedImage frameAuraAtual = animAuraCarga[frameAura % animAuraCarga.length];
            if (frameAuraAtual != null) {
                g2.drawImage(frameAuraAtual, (int) x - 20, (int) y - 10, largura + 40, altura + 20, null);
            }
        }

        // sprite principal
        super.desenhar(g2);
        
     // tornado embaixo dos pés durante o especial 1
        if (subindoEspecial1 && animTornado != null && animTornado.length > 0) {
            BufferedImage frameTornadoAtual = animTornado[frameTornado % animTornado.length];
            if (frameTornadoAtual != null) {
                int tornadoW = largura + 60;
                int tornadoH = 180;
                g2.drawImage(frameTornadoAtual,
                    (int) x - 30,
                    (int) y + altura - 20,  // embaixo dos pés
                    tornadoW, tornadoH, null);
            }
        }
        
     // efeito visual da transformação ativa
        if (transformado && animEfeitoTransformado != null && animEfeitoTransformado.length > 0) {
            BufferedImage frameEfeitoAtual = animEfeitoTransformado[frameEfeito % animEfeitoTransformado.length];
            if (frameEfeitoAtual != null) {
                g2.drawImage(frameEfeitoAtual,
                    (int) x - 30,
                    (int) y + 20,
                    largura + 60,
                    altura + 40,
                    null);
            }
        }

        // raios elétricos
        for (RaioEletrico r : raios) {
            r.desenhar(g2);
        }
    }

    private void atualizarFramesEfeitos() {
        contadorAura++;
        if (contadorAura >= 6) { contadorAura = 0; frameAura++; }

        contadorEfeito++;
        if (contadorEfeito >= 6) { contadorEfeito = 0; frameEfeito++; }

        contadorTornado++;
        if (contadorTornado >= 5) { contadorTornado = 0; frameTornado++; }
    }
}