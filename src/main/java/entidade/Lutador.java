package entidade;

import motor.CapturaTeclado;
import util.Configuracoes;
import util.CarregadorImagens;
import util.GerenciadorAudio;
import util.Sons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;


// classe base abstrata para todos os lutadores

public abstract class Lutador {

    // identificação
    protected String nome;
    protected boolean ehJogador1;

    // posição e física
    protected float x, y;
    protected float velX, velY;
    protected boolean noChao;

    // dimensões
    protected int largura  = Configuracoes.LARGURA_SPRITE;
    protected int altura   = Configuracoes.ALTURA_SPRITE;

    // atributos de combate
    protected int hpMaximo;
    protected int hpAtual;
    protected int danoSoco;
    protected int danoChute;
    protected int danoEspecial;
    protected float velocidade;

    // estado atual
    protected AcaoLutador acaoAtual = AcaoLutador.PARADO;
    protected boolean viradoParaDireita = true;

    // animação
    protected Map<AcaoLutador, BufferedImage[]> animacoes = new HashMap<>();
    protected int frameAtual    = 0;
    protected int contadorFrames = 0;
    protected int velocidadeAnimacao = Configuracoes.VELOCIDADE_ANIMACAO;

    // cooldowns e timers
    protected int timerAtaque      = 0;   // frames restantes da animação de ataque
    protected int cooldownAtaque   = 0;   // frames de cooldown entre ataques
    protected int timerInvencivel  = 0;   // frames de invencibilidade após tomar dano
    protected boolean jaTocouNesse = false; // evita dano múltiplo por ataque

    // hitboxes
    protected Rectangle hitbox      = new Rectangle(); // corpo do lutador
    protected Rectangle attackBox   = new Rectangle(); // alcance do ataque ativo
    
 // mecânica de Transformação
    protected boolean transformado = false;
    protected int framesSegurandoTransformacao = 0;
    protected int timerTransformacaoAtiva = 0;
    protected int cooldownTransformacao = 0;
    
    // 60 FPS * segundos
    protected final int TEMPO_CARGA_TRANSFORMACAO = 240; // 4 segundos
    protected final int DURACAO_TRANSFORMACAO = 1200;     // 20 segundos
    protected final int COOLDOWN_MAX_TRANSFORMACAO = 600; // 10 segundos de recarga
    
    protected List<Projetil> projeteis = new ArrayList<>();

    // ------------------------------------------------------
    //  métodos abstratos: cada personagem implementa os seus

    protected abstract void carregarSprites();
    public abstract void executarEspecial();
    protected abstract String getPastaSprites();
    
    public abstract void executarEspecial2();
    protected abstract void ativarTransformacao();
    protected abstract void desativarTransformacao();

    // -----------------------------------------------------
    //  inicialização

    public void inicializar(float x, float y, boolean ehJogador1) {
        this.x          = x;
        this.y          = y;
        this.ehJogador1 = ehJogador1;
        this.hpAtual    = hpMaximo;

        this.viradoParaDireita = ehJogador1;

        carregarSprites();
        atualizarHitbox();
    }

    // ------------------------------------------------------
    //  atualização

    public void atualizar(CapturaTeclado teclado, Lutador adversario) {
        if (estanocauteado()) return;

        processarInput(teclado);
        aplicarFisica();
        atualizarHitbox();
        atualizarAtackBox();
        atualizarTimers();
        atualizarAnimacao();
        virarParaAdversario(adversario);
        
     // atualiza os tiros e remove os que já bateram ou saíram da tela
        for (int i = projeteis.size() - 1; i >= 0; i--) {
            Projetil p = projeteis.get(i);
            p.atualizar();
            if (!p.isAtivo()) {
                projeteis.remove(i);
            }
        }   
    }

    private void processarInput(CapturaTeclado teclado) {
        if (estaAtacando() || acaoAtual == AcaoLutador.TOMANDO_DANO) return; 

        boolean moverEsq    = ehJogador1 ? teclado.j1Esquerda    : teclado.j2Esquerda;
        boolean moverDir    = ehJogador1 ? teclado.j1Direita     : teclado.j2Direita;
        boolean pular       = ehJogador1 ? teclado.j1Pular       : teclado.j2Pular;
        boolean transformar = ehJogador1 ? teclado.j1Transformar : teclado.j2Transformar;
        boolean soco        = ehJogador1 ? teclado.j1Soco        : teclado.j2Soco;
        boolean chute       = ehJogador1 ? teclado.j1Chute       : teclado.j2Chute;
        boolean especial    = ehJogador1 ? teclado.j1Especial    : teclado.j2Especial;
        boolean especial2   = ehJogador1 ? teclado.j1Especial2   : teclado.j2Especial2;

        velX = 0;

        // lógica de carregar transformação
        if (transformar && !transformado && cooldownTransformacao <= 0) {
            framesSegurandoTransformacao++;
            if (framesSegurandoTransformacao >= TEMPO_CARGA_TRANSFORMACAO) {
                // completou os 4 segundos
                transformado = true;
                timerTransformacaoAtiva = DURACAO_TRANSFORMACAO;
                framesSegurandoTransformacao = 0;
                GerenciadorAudio.tocarEfeito(Sons.EFEITO_ESPECIAL); // toca um som de aviso
                ativarTransformacao(); 
            }
        } else {
            // se o jogador soltar o botão antes dos 4 segundos, zera a carga
            framesSegurandoTransformacao = 0;
        }

        // movimentação (só permite mover se não estiver parado carregando a transformação)
        if (framesSegurandoTransformacao == 0) {
            if (moverEsq) {
                velX = -velocidade;
                acaoAtual = AcaoLutador.CORRENDO;
            } else if (moverDir) {
                velX = velocidade;
                acaoAtual = AcaoLutador.CORRENDO;
            } else {
                acaoAtual = AcaoLutador.PARADO;
            }

            if (pular && noChao) {
                velY = Configuracoes.FORCA_PULO;
                noChao = false;
                acaoAtual = AcaoLutador.PULANDO;
                GerenciadorAudio.tocarEfeito(Sons.EFEITO_PULAR);
            }
        }

        // ataques
        if (cooldownAtaque <= 0 && framesSegurandoTransformacao == 0) {
            if (soco) {
                iniciarAtaque(AcaoLutador.SOCOS, 30, 25);
            } else if (chute) {
                iniciarAtaque(AcaoLutador.CHUTE, 36, 20);
            } else if (especial) {
                iniciarAtaque(AcaoLutador.ESPECIAL, 50, 20);
                executarEspecial();
            } else if (especial2) {
                iniciarAtaque(AcaoLutador.ESPECIAL2, 30, 40);
                executarEspecial2();
            }
        }
    }

    private void aplicarFisica() {
        velY += Configuracoes.GRAVIDADE;
        y    += velY;
        x    += velX;

        if (y >= Configuracoes.ALTURA_CHAO - altura) {
            y     = Configuracoes.ALTURA_CHAO - altura;
            velY  = 0;
            noChao = true;
            if (acaoAtual == AcaoLutador.PULANDO) {
                acaoAtual = AcaoLutador.PARADO;
            }
        }

        if (x < 0) x = 0;
        if (x + largura > Configuracoes.LARGURA_TELA) {
            x = Configuracoes.LARGURA_TELA - largura;
        }
    }

    private void iniciarAtaque(AcaoLutador acao, int duracaoFrames, int cooldown) {
        acaoAtual      = acao;
        timerAtaque    = duracaoFrames;
        cooldownAtaque = cooldown;
        jaTocouNesse   = false;
        frameAtual     = 0;
        contadorFrames = 0;

        switch (acao) {
            case SOCOS:    GerenciadorAudio.tocarEfeito(Sons.EFEITO_SOCO);     break;
            case CHUTE:    GerenciadorAudio.tocarEfeito(Sons.EFEITO_CHUTE);    break;
            case ESPECIAL: GerenciadorAudio.tocarEfeito(Sons.EFEITO_ESPECIAL); break;
            default: break;
        }
    }

    private void atualizarTimers() {
        if (timerAtaque > 0) {
            timerAtaque--;
            if (timerAtaque == 0 && acaoAtual != AcaoLutador.PARADO) {
                acaoAtual = AcaoLutador.PARADO;
            }
        }
        if (cooldownAtaque > 0) cooldownAtaque--;
        if (timerInvencivel > 0) timerInvencivel--;
        
        // gerenciamento da transformação
        if (transformado) {
            timerTransformacaoAtiva--;
            if (timerTransformacaoAtiva <= 0) {
                transformado = false;
                cooldownTransformacao = COOLDOWN_MAX_TRANSFORMACAO; // inicia a recarga
                desativarTransformacao();
            }
        } else if (cooldownTransformacao > 0) {
            cooldownTransformacao--; // diminui a recarga com o tempo
        }
    }

    protected void atualizarAnimacao() {
        contadorFrames++;
        if (contadorFrames >= velocidadeAnimacao) {
            contadorFrames = 0;
            BufferedImage[] frames = animacoes.get(acaoAtual);
            if (frames != null && frames.length > 0) {
                frameAtual = (frameAtual + 1) % frames.length;
            }
        }
    }

    protected void virarParaAdversario(Lutador adversario) {
        viradoParaDireita = (adversario.x > this.x);
    }

    // sincroniza o hitbox cortando os espaços em branco da imagem
     
    protected void atualizarHitbox() {
        int margemX = 45; // corta 45 pixels da esquerda e direita
        int margemY = 30; // corta 30 pixels da cabeça do sprite
        
        hitbox.setBounds((int) x + margemX, 
                         (int) y + margemY, 
                         largura - (margemX * 2), 
                         altura - margemY);
    }

    // define o attackBox à frente do lutador

    protected void atualizarAtackBox() {
        if (!estaAtacando()) {
            attackBox.setBounds(0, 0, 0, 0);
            return;
        }

        int alcanceAtaque = 60; // quão longe o soco/chute vai
        int alturaAtaque = 50;  // altura da área de contato
        int margemDoOmbro = 30; // deslocamento no eixo Y (para não sair do pé)
        
        int yAtaque = (int) y + margemDoOmbro;

        // projeta a caixa vermelha de ataque na direção que o lutador está olhando
        if (viradoParaDireita) {
            attackBox.setBounds((int) x + (largura / 2), yAtaque, alcanceAtaque, alturaAtaque);
        } else {
            attackBox.setBounds((int) x + (largura / 2) - alcanceAtaque, yAtaque, alcanceAtaque, alturaAtaque);
        }
    }

    // ------------------------------------------------
    //  combate

    public void receberDano(int dano) {
        if (timerInvencivel > 0 || estanocauteado()) return;

        hpAtual -= dano;
        timerInvencivel = 20;
        velX = 0;

        if (hpAtual <= 0) {
            hpAtual   = 0;
            acaoAtual = AcaoLutador.NOCAUTEADO;
            GerenciadorAudio.tocarEfeito(Sons.EFEITO_NOCAUTE);
        } else {
            acaoAtual   = AcaoLutador.TOMANDO_DANO;
            timerAtaque = 15;
            GerenciadorAudio.tocarEfeito(Sons.EFEITO_TOMAR_DANO);
        }
    }

    // ----------------------------------------------------
    //  renderização

    public void desenhar(Graphics2D g2) {
        BufferedImage[] frames = animacoes.get(acaoAtual);

        if (frames == null || frames[frameAtual % frames.length] == null) {
            desenharFallback(g2);
            return;
        }

        BufferedImage frame = frames[frameAtual % frames.length];

        // mantém os pixels nítidos ao esticar a imagem
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        if (viradoParaDireita) {
            g2.drawImage(frame, (int) x, (int) y, largura, altura, null);
        } else {
            g2.drawImage(frame, (int) x + largura, (int) y, -largura, altura, null);
        }
        
     // desenha os projéteis soltos pela tela
        for (Projetil p : projeteis) {
            p.desenhar(g2);
        }
    }

    private void desenharFallback(Graphics2D g2) {
        g2.setColor(ehJogador1 ? new Color(70, 130, 200) : new Color(200, 70, 70));
        g2.fillRect((int) x, (int) y, largura, altura);

        g2.setColor(Color.WHITE);
        g2.drawString(nome != null ? nome.substring(0, 1) : "?",
                (int) x + largura / 2 - 5,
                (int) y + altura / 2);
    }

    // ---------------------------------------------------
    //  helpers de estado e getters
    
    public void receberDanoRedemoinho(int dano) {
        if (estanocauteado()) return;
        hpAtual -= dano;
        if (hpAtual <= 0) {
            hpAtual   = 0;
            acaoAtual = AcaoLutador.NOCAUTEADO;
            util.GerenciadorAudio.tocarEfeito(util.Sons.EFEITO_NOCAUTE);
        }
    }

    // permite ao redemoinho reposicionar o lutador diretamente
    public void setX(float novoX) {
        this.x = novoX;
        if (this.x < 0) this.x = 0;
        if (this.x + largura > Configuracoes.LARGURA_TELA) {
            this.x = Configuracoes.LARGURA_TELA - largura;
        }
    }
    
    public void empurrar(float deltaX) {
        x += deltaX;
        if (x < 0) x = 0;
        if (x + largura > util.Configuracoes.LARGURA_TELA) {
            x = util.Configuracoes.LARGURA_TELA - largura;
        }
    }

    public AcaoLutador getAcaoAtual() { return acaoAtual; }

    public boolean estaAtacando() {
        return acaoAtual == AcaoLutador.SOCOS
            || acaoAtual == AcaoLutador.CHUTE
            || acaoAtual == AcaoLutador.ESPECIAL
            || acaoAtual == AcaoLutador.ESPECIAL2;
    }

    public boolean estanocauteado() {
        return acaoAtual == AcaoLutador.NOCAUTEADO;
    }

    public String getNome()           { return nome; }
    public int    getHpAtual()        { return hpAtual; }
    public int    getHpMaximo()       { return hpMaximo; }
    public int    getDanoSoco()       { return danoSoco; }
    public int    getDanoChute()      { return danoChute; }
    public int    getDanoEspecial()   { return danoEspecial; }
    public boolean isJaTocouNesse()   { return jaTocouNesse; }
    public void   setJaTocouNesse(boolean v) { jaTocouNesse = v; }
    public Rectangle getHitbox()      { return hitbox; }
    public Rectangle getAttackBox()   { return attackBox; }
    public float  getX()              { return x; }
    public float  getY()              { return y; }
    
    public int getLargura() { return largura; }
    public int getAltura()  { return altura; }
    
    public List<Projetil> getProjeteis() { return projeteis; }
}