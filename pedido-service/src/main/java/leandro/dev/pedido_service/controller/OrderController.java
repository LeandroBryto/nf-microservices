package leandro.dev.pedido_service.controller;

import jakarta.validation.Valid;
import leandro.dev.pedido_service.dto.OrderRequestDTO;
import leandro.dev.pedido_service.dto.OrderResponseDTO;
import leandro.dev.pedido_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> criarPedido(@Valid @RequestBody OrderRequestDTO request){
        log.info("Recebida requisição para criar pedido: {}", request.getClienteNome());
        try {
            OrderResponseDTO response = orderService.criaPedido(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e){
            log.error("Erro ao criar pedido: {}",e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("Pedido Service está funcionando!");
    }
}
