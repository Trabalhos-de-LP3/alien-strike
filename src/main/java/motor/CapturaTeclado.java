package motor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * captura o estado das teclas em tempo real para os dois jogadores
 * 
 * Jogador 1: WASD + teclas de ação
 *   Mover:   A (esquerda), D (direita)
 *   Pular:   W
 *   Transformar: S
 *   Soco:    G
 *   Chute:   H
 *   Especial 1: J
 *   Especial 2: Y
 *
 * Jogador 2: Setas + teclas de ação
 *   Mover:   Seta esquerda, seta direita
 *   Pular:   Seta para cima
 *   Transoformar: Seta para baixo
 *   Soco:    1
 *   Chute:   2
 *   Especial 1: 3
 *   Especial 2: 5
 */

public class CapturaTeclado implements KeyListener {

    // Jogador 1
    public boolean j1Esquerda, j1Direita, j1Pular, j1Transformar;
    public boolean j1Soco, j1Chute, j1Especial, j1Especial2;

    // Jogador 2
    public boolean j2Esquerda, j2Direita, j2Pular, j2Transformar;
    public boolean j2Soco, j2Chute, j2Especial, j2Especial2;

    // sistema
    public boolean pausar;
    public boolean confirmar;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            // Jogador 1
            case KeyEvent.VK_A         -> j1Esquerda = true;
            case KeyEvent.VK_D         -> j1Direita  = true;
            case KeyEvent.VK_W         -> j1Pular    = true;
            case KeyEvent.VK_S         -> j1Transformar  = true;
            case KeyEvent.VK_G         -> j1Soco     = true;
            case KeyEvent.VK_H         -> j1Chute    = true;
            case KeyEvent.VK_J         -> j1Especial = true;
            case KeyEvent.VK_Y         -> j1Especial2 = true;

            // Jogador 2
            case KeyEvent.VK_LEFT      -> j2Esquerda = true;
            case KeyEvent.VK_RIGHT     -> j2Direita  = true;
            case KeyEvent.VK_UP        -> j2Pular    = true;
            case KeyEvent.VK_DOWN      -> j2Transformar  = true;
            case KeyEvent.VK_NUMPAD1         -> j2Soco     = true;
            case KeyEvent.VK_NUMPAD2         -> j2Chute    = true;
            case KeyEvent.VK_NUMPAD3 -> j2Especial = true;
            case KeyEvent.VK_NUMPAD5 -> j2Especial2 = true;

            // sistema
            case KeyEvent.VK_ESCAPE    -> pausar     = true;
            case KeyEvent.VK_ENTER     -> confirmar  = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            // Jogador 1
            case KeyEvent.VK_A         -> j1Esquerda = false;
            case KeyEvent.VK_D         -> j1Direita  = false;
            case KeyEvent.VK_W         -> j1Pular    = false;
            case KeyEvent.VK_S         -> j1Transformar  = false;
            case KeyEvent.VK_G         -> j1Soco     = false;
            case KeyEvent.VK_H         -> j1Chute    = false;
            case KeyEvent.VK_J         -> j1Especial = false;
            case KeyEvent.VK_Y         -> j1Especial2 = false;

            // Jogador 2
            case KeyEvent.VK_LEFT      -> j2Esquerda = false;
            case KeyEvent.VK_RIGHT     -> j2Direita  = false;
            case KeyEvent.VK_UP        -> j2Pular    = false;
            case KeyEvent.VK_DOWN      -> j2Transformar  = false;
            case KeyEvent.VK_NUMPAD1         -> j2Soco     = false;
            case KeyEvent.VK_NUMPAD2         -> j2Chute    = false;
            case KeyEvent.VK_NUMPAD3 -> j2Especial = false;
            case KeyEvent.VK_NUMPAD5 -> j2Especial2 = false;
            
            // sistema
            case KeyEvent.VK_ESCAPE    -> pausar    = false;
            case KeyEvent.VK_ENTER     -> confirmar = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // não utilizado
    }
}