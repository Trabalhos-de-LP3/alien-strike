package util;

/*
 * Centraliza os caminhos de todos os arquivos de áudio do jogo.
 *
 * estrutura de pastas:
 *   resources/sons/musica/   -> músicas de fundo (WAV, loop)
 *   resources/sons/efeitos/  -> efeitos sonoros pontuais (WAV)
 */

public class Sons {

    // ---------------------------------------------------------------
    //  músicas de fundo

    public static final String MUSICA_MENU     = "/sons/musica/espacoAzul.wav";
    public static final String MUSICA_SELECAO  = "/sons/musica/espacoAzul.wav";
    public static final String MUSICA_LUTA     = "/sons/musica/espacoAzul.wav"; // fallback
    public static final String MUSICA_VITORIA  = "/sons/musica/tema_vitoria.wav";

    // músicas por arena
    public static final String MUSICA_CASTELO   = "/sons/musica/castelo.wav";
    public static final String MUSICA_CITY      = "/sons/musica/city.wav";
    public static final String MUSICA_CYBERPUNK = "/sons/musica/cyberpunk-street.wav";
    public static final String MUSICA_SCIFILAB  = "/sons/musica/scifi-lab.wav";

    // --------------------------------------------------------------
    //  efeitos gerais (todos os personagens)

    // soco básico (todos os personagens)
    public static final String EFEITO_SOCO          = "/sons/efeitos/soco.wav";

    // chute básico (todos os personagens)
    public static final String EFEITO_CHUTE         = "/sons/efeitos/chute.wav";

    // receber golpe (todos os personagens)
    public static final String EFEITO_TOMAR_DANO    = "/sons/efeitos/tomar_dano.wav";

    // nocaute (todos os personagens)
    public static final String EFEITO_NOCAUTE       = "/sons/efeitos/nocaute.wav";

    // pulo normal (todos os personagens)
    public static final String EFEITO_PULAR         = "/sons/efeitos/pular.wav";

    // carregar transformação
    public static final String EFEITO_CARREGAR_TRANSFORMACAO = "/sons/efeitos/carregar_transformacao.wav";

    // transformação ativada
    public static final String EFEITO_TRANSFORMACAO  = "/sons/efeitos/transformacao.wav";

    // ----------------------------------------------------
    //  kree (guerreiro)

    // Especial 1: invocar parede de chamas
    public static final String EFEITO_KREE_PAREDE_CHAMAS    = "/sons/efeitos/kree_parede_chamas.wav";

    // Especial 1: chamas queimando (loop curto enquanto a parede existe)
    public static final String EFEITO_KREE_CHAMAS_ATIVAS    = "/sons/efeitos/kree_chamas_ativas.wav";

    // Especial 2: disparar projétil explosivo
    public static final String EFEITO_KREE_DISPARO          = "/sons/efeitos/kree_disparo.wav";

    // Especial 2: explosão do projétil ao acertar
    public static final String EFEITO_KREE_EXPLOSAO         = "/sons/efeitos/kree_explosao.wav";

    // Transformação: campo de energia ao redor do Kree
    public static final String EFEITO_KREE_CAMPO_ENERGIA    = "/sons/efeitos/kree_campo_energia.wav";

    // Transformação: choque ao adversário encostar no campo
    public static final String EFEITO_KREE_CHOQUE           = "/sons/efeitos/kree_choque.wav";

    // --------------------------------------------------------
    //  saiyajin (ninja)

    // Especial 1: teletransporte
    public static final String EFEITO_SAIYAJIN_TELETRANSPORTE = "/sons/efeitos/saiyajin_teletransporte.wav";

    // Especial 2: disparar rajada de energia
    public static final String EFEITO_SAIYAJIN_RAJADA         = "/sons/efeitos/saiyajin_rajada.wav";

    // Transformação: ativação (grito/power-up)
    public static final String EFEITO_SAIYAJIN_TRANSFORMACAO   = "/sons/efeitos/saiyajin_transformacao.wav";

    // Transformação: eletricidade constante ao redor
    public static final String EFEITO_SAIYAJIN_ELETRICIDADE    = "/sons/efeitos/saiyajin_eletricidade.wav";

    // -------------------------------------------------------------
    //  bumblebee (mago)

    // Especial 1: salto relâmpago (impulso)
    public static final String EFEITO_BUMBLEBEE_SALTO          = "/sons/efeitos/bumblebee_raio.wav";

    // Especial 1: dano ao adversário próximo na subida
    public static final String EFEITO_BUMBLEBEE_IMPACTO_SALTO  = "/sons/efeitos/tomar_dano.wav";

    // Especial 2: lançar redemoinho
    public static final String EFEITO_BUMBLEBEE_REDEMOINHO      = "/sons/efeitos/bumblebee_redemoinho.wav";

    // Especial 2: adversário capturado pelo redemoinho
    public static final String EFEITO_BUMBLEBEE_CAPTURA         = "/sons/efeitos/tomar_dano.wav";

    // Transformação: teleporte para o topo
    public static final String EFEITO_BUMBLEBEE_TELEPORTE       = "/sons/efeitos/saiyajin_teletransporte.wav";

    // Transformação: disparar raio elétrico
    public static final String EFEITO_BUMBLEBEE_RAIO            = "/sons/efeitos/bumblebee_raio.wav";

    // Transformação: raio atingindo o chão
    public static final String EFEITO_BUMBLEBEE_RAIO_IMPACTO    = "/sons/efeitos/bumblebee_raio_impacto.wav";

    // -----------------------------------------------------------
    //  grey (bárbaro)

    // Especial 1: ativar barreira de fogo
    public static final String EFEITO_GREY_BARREIRA             = "/sons/efeitos/grey_barreira.wav";

    // Especial 1: adversário tocando na barreira (repulsão)
    public static final String EFEITO_GREY_REPULSAO             = "/sons/efeitos/tomar_dano.wav";

    // Especial 2: invocar martelo
    public static final String EFEITO_GREY_MARTELO_INVOCAR      = "/sons/efeitos/kree_parede_chamas.wav";

    // Especial 2: martelo batendo no chão
    public static final String EFEITO_GREY_MARTELO_IMPACTO      = "/sons/efeitos/grey_martelo_impacto.wav";

    // Transformação: ativação
    public static final String EFEITO_GREY_TRANSFORMACAO        = "/sons/efeitos/grey_transformacao.wav";

    // Transformação: bola de fogo caindo
    public static final String EFEITO_GREY_BOLA_FOGO            = "/sons/efeitos/grey_bola_fogo.wav";

    // Transformação: bola de fogo acertando o adversário
    public static final String EFEITO_GREY_BOLA_FOGO_IMPACTO    = "/sons/efeitos/tomar_dano.wav";

    // Transformação: caveiras orbitando
    public static final String EFEITO_GREY_CAVEIRAS             = "/sons/efeitos/kree_chamas_ativas.wav";
    
    // Transformação: fogo queimando
    public static final String EFEITO_GREY_FOGO_LOOP = "/sons/efeitos/fogo_loop.wav";

    // --------------------------------------------------
    //  UI

    // Navegar pelos menus de seleção
    public static final String EFEITO_SELECIONAR  = "/sons/efeitos/selecionar.wav";

    // Confirmar seleção
    public static final String EFEITO_CONFIRMAR   = "/sons/efeitos/confirmar.wav";

    // Anunciador "LUTEM!" no início do round
    public static final String EFEITO_ANUNCIADOR  = "/sons/efeitos/anunciador_luta.wav";
    
    // Anunciador de empate
    public static final String EFEITO_EMPATE = "/sons/efeitos/empate.wav";
    
    // Anunciador de vitória
    public static final String EFEITO_ANUNCIADOR_VITORIA = "/sons/efeitos/vitoria.wav";

    // Construtor privado
    private Sons() {}
}