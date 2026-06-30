package br.com.gustavo.iam;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Classe responsável pelo tratamento de erros da API de forma centralizada.
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UsuarioJaExisteException.class)
    public ResponseEntity<ErroResponse> tratarUsuarioJaExiste(UsuarioJaExisteException exception) {
        ErroResponse erro = new ErroResponse(exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarUsuarioNaoEncontrado(UsuarioNaoEncontradoException exception) {
        ErroResponse erro = new ErroResponse(exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarErroDeValidacao(MethodArgumentNotValidException exception) {
        FieldError erroDeCampo = exception.getBindingResult().getFieldError();

        String mensagem = "Dados inválidos";

        if (erroDeCampo != null) {
            mensagem = erroDeCampo.getDefaultMessage();
        }

        ErroResponse erro = new ErroResponse(mensagem);

        return ResponseEntity.badRequest().body(erro);
    }
}