# 👽 ALIEN STRIKE
### Pancadaria Extraterrestre

Jogo de luta 2D para dois jogadores, desenvolvido em Java com Java2D. Quatro personagens jogáveis, quatro arenas, trilha sonora por arena, sistema de transformação, projéteis e efeitos visuais em camadas.

Projeto desenvolvido para a disciplina de **Programação Orientada a Objetos II** na UESB.

---

## Como Jogar

### Controles

| Ação | Jogador 1 | Jogador 2 |
|---|---|---|
| Mover | A / D | Seta Esq / Dir |
| Pular | W | Seta Cima |
| Carregar Transformação | S (segurar) | Seta Baixo (segurar) |
| Soco | G | Numpad 1 |
| Chute | H | Numpad 2 |
| Especial 1 | J | Numpad 3 |
| Especial 2 | Y | Numpad 5 |
| Pausar | ESC | ESC |
| Confirmar (menus) | ENTER | ENTER |

### Transformação
Segure o botão de transformação por **4 segundos** para ativar. Dura **20 segundos** e entra em recarga de **10 segundos** após expirar.

---

## Personagens

### Kree (Guerreiro)
- **Especial 1:** Parede de Chamas. Spawna uma parede de fogo à frente que dura 3 segundos, causa dano por contato e cancela projéteis.
- **Especial 2:** Tiro Explosivo. Projétil que explode ao colidir ou expirar, causando dano em área.
- **Transformação:** Barreira Psiônica. Fica intangível, ganha bônus de dano e um campo de energia que machuca quem se aproximar.

### Saiyajin (Ninja)
- **Especial 1:** Teletransporte. Reposiciona instantaneamente ao lado do adversário.
- **Especial 2:** Rajada de Energia. Projétil rápido de energia azul.
- **Transformação:** Modo Berserker. Velocidade e dano dobram. Efeitos visuais de trovão e eletricidade ao redor do corpo.

### Bumblebee (Mago)
- **Especial 1:** Salto Relâmpago. Pulo com força aumentada que causa dano ao adversário próximo durante a subida.
- **Especial 2:** Tornado. Lança um redemoinho que captura e arrasta o adversário causando dano contínuo.
- **Transformação:** Voo. Levita no topo da tela, fica imune a golpes e pode disparar raios elétricos verticais.

### Grey (Bárbaro)
- **Especial 1:** Barreira de Fogo. Escudo ao redor do corpo que repele e causa dano ao adversário, além de cancelar projéteis.
- **Especial 2:** Golpe de Martelo. Invoca um martelo à frente. Se usado no ar, o martelo cai com gravidade até o chão antes de bater.
- **Transformação:** Chuva Flamejante. Fica invulnerável. Caveiras orbitam ao redor causando dano e bolas de fogo caem aleatoriamente pela tela.

---

## Arenas

| Arena | Trilha Sonora |
|---|---|
| Castelo | castelo.wav |
| Laboratório Sci-Fi | scifi-lab.wav |
| Cidade | city.wav |
| Cyberpunk | cyberpunk-street.wav |

---

## Como Executar

### Requisitos
- Java JDK 17 ou superior
- Eclipse IDE (recomendado) ou qualquer IDE Java

### Passos
1. Clone o repositório
2. Importe o projeto no Eclipse: `File > Import > Existing Projects into Workspace`
3. Selecione a pasta `JogoLuta/`
4. Execute a classe `principal.JogoLuta`

---

## Tecnologias Utilizadas

- **Linguagem:** Java
- **Gráficos:** Java2D (Graphics2D, BufferedImage)
- **Áudio:** javax.sound.sampled
- **Entrada:** java.awt.event.KeyListener
- **Assets:** PNG (sprites) e WAV (áudio)

---

## Estrutura do Projeto

```
JogoLuta/
├── src/main/java/
│   ├── principal/      # Ponto de entrada (JogoLuta.java)
│   ├── motor/          # Game loop, input e colisão
│   ├── entidade/       # Lutador, Projetil e variantes
│   ├── entidade/lutadores/  # Os 4 personagens
│   ├── estado/         # Telas e máquina de estados
│   ├── ui/             # HUD (barras de HP e timer)
│   └── util/           # Configurações, áudio e imagens
└── resources/
    ├── sprites/        # Animações dos personagens e efeitos
    └── sons/           # Músicas de fundo
```

---

## Conceitos de POO Aplicados

- **Herança e polimorfismo:** `Lutador` abstrato com subclasses concretas
- **Encapsulamento:** atributos protegidos com acesso por getters
- **Classes abstratas e métodos abstratos:** cada personagem implementa seus próprios especiais e transformação
- **Inner classes:** `GolpeMartelo` e `BolaDeFogo` encapsuladas dentro de `Barbaro`
- **Herança especializada:** `ProjetilExplosivo` e `ProjetilRedemoinho` estendem `Projetil`
- **Enums:** `AcaoLutador` e `EstadoJogo` como máquinas de estado
- **Padrão Cache:** `CarregadorImagens` com `HashMap` para evitar I/O repetido
