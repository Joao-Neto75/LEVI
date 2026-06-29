package br.edu.ufersa.LEVI.model.exception;

/**
 * Lançada quando as credenciais informadas no login são inválidas
 * (e-mail não encontrado, senha incorreta ou campos em branco).
 * É uma RuntimeException (unchecked) pois falha de autenticação
 * deve interromper o fluxo e ser tratada pela camada de apresentação.
 */
public class AutenticacaoException extends RuntimeException {

    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
