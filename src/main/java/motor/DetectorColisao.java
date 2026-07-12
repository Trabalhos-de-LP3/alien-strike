package motor;

import entidade.Lutador;
import entidade.Projetil;
import entidade.ProjetilRedemoinho;

/*
 * responsável por verificar colisões de ataque entre os dois lutadores
 *
 * se o attackBox do lutador A intersecta o hitbox do lutador B, 
 * e o ataque ainda não tocou neste adversário neste frame, aplica o dano correspondente à ação atual
 */

public class DetectorColisao {

    /*
     * verifica e aplica dano entre dois lutadores, nos dois sentidos
     * deve ser chamado uma vez por frame no estado de luta
     *
     * @param lutador1 Jogador 1
     * @param lutador2 Jogador 2
     */
	
    public void verificar(Lutador lutador1, Lutador lutador2) {
        checarAtaque(lutador1, lutador2);
        checarAtaque(lutador2, lutador1);

        checarProjeteis(lutador1, lutador2);
        checarProjeteis(lutador2, lutador1);
        
        checarColisaoProjeteis(lutador1, lutador2);
        
        // empurra lutadores que se sobrepõem fisicamente
        resolverSobreposicao(lutador1, lutador2);
    }
    
    private void checarColisaoProjeteis(Lutador l1, Lutador l2) {
        for (Projetil p1 : l1.getProjeteis()) {
            if (!p1.isAtivo()) continue;
            for (Projetil p2 : l2.getProjeteis()) {
                if (!p2.isAtivo()) continue;
                if (p1.getHitbox().intersects(p2.getHitbox())) {
                    p1.setAtivo(false);
                    p2.setAtivo(false);
                }
            }
        }
    }

    // checa se o atacante está atingindo o defensor

    private void checarAtaque(Lutador atacante, Lutador defensor) {
        if (!atacante.estaAtacando()) return;
        if (atacante.isJaTocouNesse()) return;
        if (defensor.estanocauteado()) return;

        if (atacante.getAttackBox().intersects(defensor.getHitbox())) {
            defensor.receberDano(getDanoAtual(atacante));
            atacante.setJaTocouNesse(true);
        }
    }

    // determina o dano com base na ação atual do atacante

    private int getDanoAtual(Lutador atacante) {
        return switch (atacante.getAcaoAtual()) {
            case SOCOS    -> atacante.getDanoSoco();
            case CHUTE    -> atacante.getDanoChute();
            case ESPECIAL -> atacante.getDanoEspecial();
            default       -> 0;
        };
    }

    // impede que os dois lutadores se sobreponham fisicamente
    // empurra cada um para o seu lado quando os hitboxes colidem
     
    private void resolverSobreposicao(Lutador l1, Lutador l2) {
        if (!l1.getHitbox().intersects(l2.getHitbox())) return;

        float empurrao = 2.5f;

        if (l1.getX() < l2.getX()) {
            l1.empurrar(-empurrao);
            l2.empurrar(empurrao);
        } else {
            l1.empurrar(empurrao);
            l2.empurrar(-empurrao);
        }
    }
    
    private void checarProjeteis(Lutador atacante, Lutador defensor) {
        if (defensor.estanocauteado()) return;

        for (Projetil p : atacante.getProjeteis()) {
            if (!p.isAtivo()) continue;

            // cancelamento entre projéteis

            if (p instanceof ProjetilRedemoinho) {
                // redemoinho: tenta capturar o defensor
                ((ProjetilRedemoinho) p).checarCaptura(defensor);
            } else {
                // projétil normal: dano e destrói
                if (p.getHitbox().intersects(defensor.getHitbox())) {
                    defensor.receberDano(p.getDano());
                    p.setAtivo(false);
                }
            }
        }
    }
}