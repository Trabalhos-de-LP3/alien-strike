package util;

// constantes globais do jogo

public class Configuracoes {

    // tela
    public static final int LARGURA_TELA  = 1024; //1024
    public static final int ALTURA_TELA   = 576; // 576
    public static final int FPS_ALVO      = 60;

    // física
    public static final float GRAVIDADE        = 0.6f;
    public static final float FORCA_PULO       = -14f;
    public static final int   ALTURA_CHAO      = ALTURA_TELA - 40; // y do chão

    // lutadores
    public static final int LARGURA_SPRITE  = 144; //96
    public static final int ALTURA_SPRITE   = 192; //128

    // animação
    public static final int VELOCIDADE_ANIMACAO = 8; // frames por quadro de sprite

    // posições iniciais
    public static final int X_JOGADOR1 = 150;
    public static final int X_JOGADOR2 = LARGURA_TELA - 150 - LARGURA_SPRITE;
    public static final int Y_INICIAL  = ALTURA_CHAO - ALTURA_SPRITE;

    // combate
    public static final int TEMPO_ROUND = 300; // segundos
}