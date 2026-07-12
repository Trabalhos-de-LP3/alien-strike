package estado;

// representa os estados possíveis do jogo
// o PainelJogo usa este enum para decidir o que atualizar e desenhar
 
public enum EstadoJogo {
    MENU,
    SELECAO_PERSONAGEM,
    SELECAO_ARENA,
    LUTANDO,
    PAUSADO,
    FIM_DE_JOGO
}