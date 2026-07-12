package util;

// centraliza os caminhos de todos os arquivos de áudio do jogo
 
public class Sons {

    // músicas
	public static final String MUSICA_MENU     = "/sons/musica/espacoAzul.wav";
	public static final String MUSICA_SELECAO  = "/sons/musica/espacoAzul.wav";
	public static final String MUSICA_LUTA     = "/sons/musica/espacoAzul.wav"; // fallback
	public static final String MUSICA_VITORIA  = "/sons/musica/tema_vitoria.wav";

	// músicas por arena
	public static final String MUSICA_CASTELO   = "/sons/musica/castelo.wav";
	public static final String MUSICA_CITY      = "/sons/musica/city.wav";
	public static final String MUSICA_CYBERPUNK = "/sons/musica/cyberpunk-street.wav";
	public static final String MUSICA_SCIFILAB  = "/sons/musica/scifi-lab.wav";

    // efeitos de combate
    public static final String EFEITO_SOCO        = "/sons/efeitos/soco.wav";
    public static final String EFEITO_CHUTE       = "/sons/efeitos/chute.wav";
    public static final String EFEITO_ESPECIAL    = "/sons/efeitos/especial.wav";
    public static final String EFEITO_TOMAR_DANO  = "/sons/efeitos/tomar_dano.wav";
    public static final String EFEITO_NOCAUTE     = "/sons/efeitos/nocaute.wav";

    // efeitos de movimento
    public static final String EFEITO_PULAR       = "/sons/efeitos/pular.wav";

    // efeitos de UI
    public static final String EFEITO_SELECIONAR  = "/sons/efeitos/selecionar.wav";
    public static final String EFEITO_CONFIRMAR   = "/sons/efeitos/confirmar.wav";
    public static final String EFEITO_ANUNCIADOR  = "/sons/efeitos/anunciador_luta.wav";

    // construtor privado
    private Sons() {}
}