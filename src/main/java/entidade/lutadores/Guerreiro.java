package entidade.lutadores;

import entidade.AcaoLutador;
import entidade.Lutador;
import entidade.Projetil;
import entidade.ProjetilExplosivo;
import motor.CapturaTeclado;
import util.CarregadorImagens;
import util.GerenciadorAudio;
import util.Sons;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import entidade.ParedeChamas;
import java.util.ArrayList;
import java.util.List;

public class Guerreiro extends Lutador {

    // sprites de efeitos
    private BufferedImage[] animAuraCarga;
    private BufferedImage[] animCampoEnergia;
    private BufferedImage[] animProjetilKree;
    private BufferedImage[] animExplosao;
    private List<ParedeChamas> paredesChamas = new ArrayList<>();
    private BufferedImage[] animParedeChamas;

    // contadores de animação independentes
    private int frameAura = 0;
    private int contadorAura = 0;
    private int frameCampo = 0;
    private int contadorCampo = 0;

    // variáveis de mecânica
    private int danoSocoOriginal;
    private float velocidadeOriginal;
    private int cooldownDanoArea = 0; // controla o tempo entre os "choques" do campo de energia

    public Guerreiro() {
        this.nome         = "Kree";
        this.hpMaximo     = 400;
        this.velocidade   = 5.0f;
        this.danoSoco     = 10;
        this.danoChute    = 15;
        this.danoEspecial = 30;
        
        this.danoSocoOriginal = this.danoSoco;
        this.velocidadeOriginal = this.velocidade;
    }

    @Override
    protected String getPastaSprites() {
        return "/sprites/guerreiro/";
    }

    @Override
    protected void carregarSprites() {
        String pasta = getPastaSprites();

        // animações base
        animacoes.put(AcaoLutador.PARADO,       CarregadorImagens.carregarAnimacao(pasta, "parado",       8));
        animacoes.put(AcaoLutador.CORRENDO,     CarregadorImagens.carregarAnimacao(pasta, "correndo",    10));
        animacoes.put(AcaoLutador.PULANDO,      CarregadorImagens.carregarAnimacao(pasta, "pulando",      3));
        animacoes.put(AcaoLutador.SOCOS,        CarregadorImagens.carregarAnimacao(pasta, "socos",        8));
        animacoes.put(AcaoLutador.CHUTE,        CarregadorImagens.carregarAnimacao(pasta, "chute",        6));
        animacoes.put(AcaoLutador.ESPECIAL,     CarregadorImagens.carregarAnimacao(pasta, "especial",     1));
        animacoes.put(AcaoLutador.TOMANDO_DANO, CarregadorImagens.carregarAnimacao(pasta, "tomando_dano", 3));
        animacoes.put(AcaoLutador.NOCAUTEADO,   CarregadorImagens.carregarAnimacao(pasta, "nocauteado",   6));
        animacoes.put(AcaoLutador.ESPECIAL2,    CarregadorImagens.carregarAnimacao(pasta, "especial2",     1));

        // carregando os sprites individuais dos efeitos
        try {
            
            animAuraCarga = new BufferedImage[4];
            animAuraCarga[0] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraVerdeNovo_0.png"));
            animAuraCarga[1] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraVerdeNovo_1.png"));
            animAuraCarga[2] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraVerdeNovo_0.png"));
            animAuraCarga[3] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/auraVerdeNovo_1.png"));
            
            // projétil (especial 2)
            animProjetilKree = new BufferedImage[3];
            animProjetilKree[0] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/projetilKree_0.png"));
            animProjetilKree[1] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/projetilKree_0.png"));
            animProjetilKree[2] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/projetilKree_0.png"));
            
            animParedeChamas = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                animParedeChamas[i] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/chamas_" + i + ".png"));
            }
            
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao carregar efeitos individuais do Kree - " + e.getMessage());
        }
        
        // campo de energia da transformação
        try {
            animCampoEnergia = new BufferedImage[30];
            for (int i = 0; i < 30; i++) {
                animCampoEnergia[i] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/campoEnergia_" + i + ".png"));
            }
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao carregar Campo de Ernergia do Kree");
        }
        
        
     // explosão do projétil
        try {
            animExplosao = new BufferedImage[9];
            for (int i = 0; i < 9; i++) {
                animExplosao[i] = ImageIO.read(getClass().getResourceAsStream("/sprites/efeitos/explosao_" + i + ".png"));
            }
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao carregar Explosão do Kree");
        }
    }

    // ----------------------------------------------------------
    //  mecânicas exclusivas (atualizar e receber dano)

    @Override
    public void atualizar(CapturaTeclado teclado, Lutador adversario) {
        super.atualizar(teclado, adversario); // roda a física e inputs base

        // 1. Reseta a animação do especial 2 ao terminar (Kree tem 3 frames, ou seja, 0 a 2)
        if (acaoAtual == AcaoLutador.ESPECIAL2 && frameAtual >= 2) {
            acaoAtual = AcaoLutador.PARADO;
            frameAtual = 0;
        }

        // 2. Lógica do Dano da Bola de Energia (transformado)
        if (transformado && adversario != null) {
            cooldownDanoArea++;
            
            // aplica dano a cada 30 frames (meio segundo) se o adversário estiver muito perto
            if (cooldownDanoArea >= 30) {
                // cria um retângulo que representa a área da bola de energia (maior que o lutador)
                int margemBola = 40; 
                Rectangle areaCampo = new Rectangle((int)x - margemBola, (int)y - margemBola, largura + (margemBola*2), altura + (margemBola*2));
                
                // se o adversário encostar na bola de energia, toma dano
                if (areaCampo.intersects(adversario.getHitbox())) {
                    adversario.receberDano(5); // dano baixo, mas constante
                    GerenciadorAudio.tocarEfeito(Sons.EFEITO_KREE_CHOQUE);
                }
                cooldownDanoArea = 0;
            }
        }
        
     // atualiza paredes de chamas
        for (int i = paredesChamas.size() - 1; i >= 0; i--) {
            ParedeChamas p = paredesChamas.get(i);
            p.atualizar();
            p.checarDano(adversario);
            // verifica projéteis do adversário contra a parede
            for (Projetil proj : adversario.getProjeteis()) {
                p.checarProjetil(proj);
            }
            if (!p.isAtiva()) paredesChamas.remove(i);
        }
        
        for (Projetil p : projeteis) {
            if (p instanceof ProjetilExplosivo) {
                ((ProjetilExplosivo) p).verificarColisao(adversario);
            }
        }
    }

    @Override
    public void receberDano(int dano) {
        // se estiver transformado, ele é intangível, ignora o ataque completamente
        if (transformado) {
            return; 
        }
        
        // se não estiver transformado, toma dano normalmente pelas regras da classe pai
        super.receberDano(dano);
    }
    
    @Override
    public void receberDanoRedemoinho(int dano) {
        if (transformado) return;
        super.receberDanoRedemoinho(dano);
    }

    // ------------------------------------------------------
    //  transformação

    @Override
    protected void ativarTransformacao() {
        this.danoSoco = this.danoSocoOriginal + 10;
        GerenciadorAudio.tocarEfeito(Sons.EFEITO_KREE_CAMPO_ENERGIA);
    }

    @Override
    protected void desativarTransformacao() {
        this.danoSoco = this.danoSocoOriginal;
    }

    // ----------------------------------------------------------
    //  especiais

    @Override
    public void executarEspecial() {
        // spawna a parede à frente do lutador
        float xParede = viradoParaDireita ? x + largura + 5 : x - 80;
        paredesChamas.add(new ParedeChamas(xParede, y, animParedeChamas));
        GerenciadorAudio.tocarEfeito(Sons.EFEITO_KREE_PAREDE_CHAMAS);
    }

    @Override
    public void executarEspecial2() {
        this.acaoAtual = AcaoLutador.ESPECIAL2;
        this.frameAtual = 0;
        
        float velTiro = viradoParaDireita ? 10f : -10f; // bala mais lenta
        float xTiro = viradoParaDireita ? this.x + largura - 20 : this.x - 40;
        float yTiro = this.y + (altura / 3);
        
        int larguraTiro = 100; // bala menor que a rajada do Ninja (Sayiajin)
        int alturaTiro = 100;
        int danoTiro = transformado ? 70 : 40;
        
        ProjetilExplosivo bala = new ProjetilExplosivo(
        	    xTiro, yTiro, velTiro, 40, 40, 
        	    40, // dano de impacto da bala
        	    viradoParaDireita, animProjetilKree, animExplosao, 
        	    60, // tempo de vida
        	    80  // dano da explosão (o oponente toma esse dano extra ao explodir)
        	);
        
        this.projeteis.add(bala);
        GerenciadorAudio.tocarEfeito(Sons.EFEITO_KREE_DISPARO);
    }

    // ----------------------------------------------------------------
    //  renderização (aura -> boneco -> campo de energia)

    @Override
    public void desenhar(Graphics2D g2) {
        atualizarFramesEfeitos();

        // 1. Fundo: aura de carga (aparece se tiver segurando o botão)
        if (framesSegurandoTransformacao > 0 && animAuraCarga != null && animAuraCarga.length > 0) {
            BufferedImage frameAuraAtual = animAuraCarga[frameAura % animAuraCarga.length];
            g2.drawImage(frameAuraAtual, (int) x - 20, (int) y - 10, largura + 40, altura + 20, null);
        }

        // 2. Meio: o sprite principal do personagem
        super.desenhar(g2);
        
        for (ParedeChamas p : paredesChamas) {
            p.desenhar(g2);
        }
        
     // 3. Frente/redor: campo de energia (aparece quando a transformação está ativa)
        if (transformado && animCampoEnergia != null && animCampoEnergia.length > 0) {
            BufferedImage frameBolaAtual = animCampoEnergia[frameCampo % animCampoEnergia.length];
            
            int offsetMargem = 15;
            
            // variável para ajuste vertical
            // aumente este número para descer mais o campo de energia
            int descerY = 40; 
            
            if (viradoParaDireita) {
                g2.drawImage(frameBolaAtual, (int) x - offsetMargem, (int) y - offsetMargem + descerY, largura + (offsetMargem*2), altura + (offsetMargem*2), null);
            } else {
                g2.drawImage(frameBolaAtual, (int) x + largura + offsetMargem, (int) y - offsetMargem + descerY, -(largura + (offsetMargem*2)), altura + (offsetMargem*2), null);
            }
        }
    }

    private void atualizarFramesEfeitos() {
        contadorAura++;
        if (contadorAura >= 6) {
            contadorAura = 0;
            frameAura++;
        }

        contadorCampo++;
        if (contadorCampo >= 5) {
            contadorCampo = 0;
            frameCampo++;
        }
    }
}