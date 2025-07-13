package leandro.dev.vendas_service.validator;

import org.springframework.stereotype.Component;

@Component
public class CpfValidator {
    public boolean isValid(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }

        // Remove pontos e traços
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }

        // Calcula o segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }

        // Verifica se os dígitos calculados conferem com os informados
        return Character.getNumericValue(cpf.charAt(9)) == primeiroDigito &&
                Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
    }
}