package leandro.dev.vendas_service.validator;

import org.springframework.stereotype.Component;

@Component
public class CpfValidator {

    /**
     * Valida se o CPF é válido.
     *
     * @param cpf CPF no formato com ou sem pontuação.
     * @throws CpfInvalidoException caso o CPF seja inválido por qualquer motivo.
     */
    public void validar(String cpf) throws CpfInvalidoException {
        if (cpf == null || cpf.isEmpty()) {
            throw new CpfInvalidoException("CPF não pode ser nulo ou vazio.");
        }

        // Remove pontos e traços
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            throw new CpfInvalidoException("CPF deve conter 11 dígitos.");
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            throw new CpfInvalidoException("CPF não pode conter todos os dígitos iguais.");
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }

        if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito ||
                Character.getNumericValue(cpf.charAt(10)) != segundoDigito) {
            throw new CpfInvalidoException("CPF inválido: dígitos verificadores não conferem.");
        }
    }

    /**
     * Exceção personalizada para CPF inválido.
     */
    public static class CpfInvalidoException extends RuntimeException {
        public CpfInvalidoException(String message) {
            super(message);
        }
    }
}