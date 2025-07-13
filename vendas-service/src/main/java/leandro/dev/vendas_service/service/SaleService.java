package leandro.dev.vendas_service.service;

import leandro.dev.vendas_service.config.RabbitMQConfig;
import leandro.dev.vendas_service.dto.OrderDTO;
import leandro.dev.vendas_service.dto.SaleDTO;
import leandro.dev.vendas_service.validator.CpfValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.IClassFileProvider;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {

    private final RabbitTemplate rabbitTemplate;
    private final CpfValidator cpfValidator;

    public void processorPedido(OrderDTO pedido){
        log.info("Processando pedido ID: {} para cliente: {}",pedido.getId(),pedido.getClienteNome());

        try {
            validarPedido(pedido);

            // cria venda
            SaleDTO venda = criarVenda(pedido);

            rabbitTemplate.convertAndSend(RabbitMQConfig.VENDAS_NOVAS_QUEUE,venda);
            log.info("Venda criada e envida para fila {} - ID: {}",
                    RabbitMQConfig.VENDAS_NOVAS_QUEUE,venda.getId());

        }catch (Exception e){
            log.error("Erro ao processar pedido ID? {} - {}", pedido.getId(), e.getMessage(), e);
            throw e;
        }


    }

    private void validarPedido(OrderDTO pedido) {
        log.debug("Validando pedido ID: {}", pedido.getId());

        // validar CPF

        if (!cpfValidator.isValid(pedido.getClienteCpf())){
            throw new IllegalArgumentException("CPF inválido: " + pedido.getClienteCpf());
        }

        // validar valor
        if (pedido.getValor() == null || pedido.getValor().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Valor deve ser igual que zero");
        }
        // Validar valor máximo
        if (pedido.getValor().compareTo(new BigDecimal("50000.00")) >0){
            throw new IllegalArgumentException("Valor excede o límete máximo de R$50.000,00");
        }
        // Validar nome
        if (pedido.getClienteEmail() == null || !pedido.getClienteEmail().contains("@")){
            throw new IllegalArgumentException("Email inválido: " + pedido.getClienteEmail());
        }
        // Valida nome
        if (pedido.getClienteNome() == null || pedido.getClienteNome().trim().length() < 2){
            throw new IllegalArgumentException("Nome do cliente inválido");
        }
        log.debug("Pedido ID: {} validado com sucesso ", pedido.getId() );
    }

    private SaleDTO criarVenda(OrderDTO pedido){
        SaleDTO venda = new SaleDTO();
        venda.setId(UUID.randomUUID());
        venda.setOrderId(pedido.getId());
        venda.setClienteNome(pedido.getClienteNome());
        venda.setClienteEmail(pedido.getClienteEmail());
        venda.setClienteCpf(pedido.getClienteCpf());
        venda.setValor(pedido.getValor());
        venda.setDescricao(pedido.getDescricao());
        venda.setDataVenda(LocalDateTime.now());
        venda.setStatus("PROCESSADO");

        log.info("Venda criada - ID: {} para pedido: {}",venda.getId(),pedido.getId());
        return venda;
    }
}
