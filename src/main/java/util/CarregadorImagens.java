package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// utilitário para carregar e armazenar imagens em cache
// evita recarregar o mesmo arquivo várias vezes durante o jogo

public class CarregadorImagens {

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    /*
     * carrega uma imagem do classpath e armazena em cache
     * @param caminho caminho relativo dentro de /resources (ex: "/sprites/guerreiro/parado_0.png")
     * @return BufferedImage carregada, ou null se não encontrada.
     */
    
    public static BufferedImage carregar(String caminho) {
        if (cache.containsKey(caminho)) {
            return cache.get(caminho);
        }

        BufferedImage imagem = null;
        try (InputStream is = CarregadorImagens.class.getResourceAsStream(caminho)) {
            if (is != null) {
                imagem = ImageIO.read(is);
                cache.put(caminho, imagem);
            } else {
                System.err.println("[CarregadorImagens] Arquivo não encontrado: " + caminho);
            }
        } catch (IOException e) {
            System.err.println("[CarregadorImagens] Erro ao carregar: " + caminho);
            e.printStackTrace();
        }

        return imagem;
    }

    /*
     * carrega uma sequência de frames de animação
     * Convenção: nomeArquivo_0.png, nomeArquivo_1.png, ...
     * @param pastaBase  pasta base (ex: "/sprites/guerreiro/")
     * @param nomeAcao   nome da ação (ex: "parado", "correndo", "soco")
     * @param totalFrames quantidade de frames
     */
    
    public static BufferedImage[] carregarAnimacao(String pastaBase, String nomeAcao, int totalFrames) {
        BufferedImage[] frames = new BufferedImage[totalFrames];
        for (int i = 0; i < totalFrames; i++) {
            String caminho = pastaBase + nomeAcao + "_" + i + ".png";
            frames[i] = carregar(caminho);
        }
        return frames;
    }
}