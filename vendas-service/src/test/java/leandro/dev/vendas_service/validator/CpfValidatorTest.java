package leandro.dev.vendas_service.validator;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
class CpfValidatorTest {

    private CpfValidator cpfValidator;

    @BeforeEach
    void setUp(){
        cpfValidator = new CpfValidator();
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "123.456.789-09",
            "111.444.777-35",
            "123.456.001-09",
            "000.000.001-09"
    })
    void deveValidarCpfsValidos(String cpf) {
        assertTrue(cpfValidator.isValid(cpf));
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "123.456.789-00",
            "111.111.111-11",
            "000.000.000-00",
            "999.999.999-99",
            "123.456.789-10"
    })
    void deveInvalidarCpfsInvalidos(String cpf){
        assertFalse(cpfValidator.isValid(cpf));
    }

    @Test
    void deveInvalidarCpfsNulo(){
        assertFalse(cpfValidator.isValid(null));
    }
    @Test
    void deveInvalidarCpfsVazio(){
        assertFalse(cpfValidator.isValid(""));
    }
    @Test
    void deveInvalidarCpfsComMenosDe11Digitos(){
        assertFalse(cpfValidator.isValid("123.456.789"));
    }
    @Test
    void deveInvalidarCpfsComMaisDe11Digitos(){
        assertFalse(cpfValidator.isValid("123.456.7889-001"));
    }
    @Test
    void deveValidarCpfSemFormatacao(){
        assertTrue(cpfValidator.isValid("12345678909"));
    }
    @Test
    void deveInvalidarCpfComLetras(){
        assertFalse(cpfValidator.isValid("123.456.789-0o"));
    }
    @Test
    void deveInvalidarCpfComTodosDigitosIguais(){
        assertFalse(cpfValidator.isValid("111.111.111-11"));
    }
}
