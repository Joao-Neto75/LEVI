package br.edu.ufersa.LEVI.model.entity;

import java.time.LocalDate;

// Centraliza a criação de Livro e Disco. Em vez de o resto do código chamar
// "new Livro(...)" ou "new Disco(...)" diretamente em vários lugares (telas,
// services), ele chama um dos métodos abaixo. Isso facilita manutenção: se um
// dia mudar a forma de criar um Produto (ex: gerar um código automático,
// validar algo antes), muda-se em um único lugar.
public class ProdutoFactory {

    private ProdutoFactory() {
        // classe utilitária, não deve ser instanciada
    }

    public static Livro criarLivro(String titulo, String genero, LocalDate ano, String autor,
                                   int paginas, int exemplares, float valorAluguel) {
        return new Livro(titulo, genero, ano, autor, paginas, exemplares, valorAluguel);
    }

    public static Disco criarDisco(String titulo, String banda, String estilo,
                                   int exemplares, float valorAluguel, LocalDate ano) {
        return new Disco(titulo, banda, estilo, exemplares, valorAluguel, ano);
    }

    // Versão genérica: recebe o tipo como String (útil quando o tipo vem de uma
    // tela com um ComboBox "Livro"/"Disco", por exemplo) e direciona para o
    // método certo. Os parâmetros que não fazem sentido para o tipo escolhido
    // podem ser passados como null/0, pois são ignorados.
    public static Produto criar(String tipo, String titulo, int exemplares, float valorAluguel,
                                LocalDate ano, String generoOuBanda, String autorOuEstilo, int paginas) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de produto não pode ser nulo!");
        }

        switch (tipo.trim().toUpperCase()) {
            case "LIVRO":
                return criarLivro(titulo, generoOuBanda, ano, autorOuEstilo, paginas, exemplares, valorAluguel);
            case "DISCO":
                return criarDisco(titulo, generoOuBanda, autorOuEstilo, exemplares, valorAluguel, ano);
            default:
                throw new IllegalArgumentException("Tipo de produto inválido: " + tipo + " (use LIVRO ou DISCO)");
        }
    }
}
