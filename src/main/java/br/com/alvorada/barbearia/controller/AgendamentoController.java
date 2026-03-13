package br.com.alvorada.barbearia.controller;

import br.com.alvorada.barbearia.dto.agendamento.AgendamentoDto;
import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.dto.agendamento.DetalhamentoAgendamentoDto;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

  private final AgendamentoService service;

  public AgendamentoController(AgendamentoService service) {
    this.service = service;
  }

  @PostMapping("/agendar")
  public ResponseEntity<DetalhamentoAgendamentoDto> agendar(
          @Valid @RequestBody AgendarDto dto,
          @AuthenticationPrincipal Usuario logado,
          UriComponentsBuilder uriBuilder
  ) {
    var agendamento = service.agendar(dto, logado);
    var uri = uriBuilder
            .path("/agendamentos/{id}")
            .buildAndExpand(agendamento.id())
            .toUri();

    return ResponseEntity.created(uri).body(agendamento);
  }

  @GetMapping
  public ResponseEntity<List<AgendamentoDto>> listarAgendamentos() {
    var agendamentos = service.listarAgendamentos();

    return ResponseEntity.ok(agendamentos);
  }

  @GetMapping("/cliente/{id}")
  public ResponseEntity<List<AgendamentoDto>> listarAgendamentosCliente(
          @PathVariable Long id,
          @AuthenticationPrincipal Usuario logado
  ) {
    var agendamentos = service.listarAgendamentosCliente(id, logado);

    return ResponseEntity.ok(agendamentos);
  }

  @GetMapping("/barbeiro/{id}")
  public ResponseEntity<List<AgendamentoDto>> listarAgendamentosBarbeiro(
          @PathVariable Long id,
          @AuthenticationPrincipal Usuario logado
  ) {
    var agendamentos = service.listarAgendamentosBarbeiro(id, logado);

    return ResponseEntity.ok(agendamentos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DetalhamentoAgendamentoDto> detalharAgendamento(
          @PathVariable Long id,
          @AuthenticationPrincipal Usuario logado
  ) {
    var agendamento = service.detalharAgendamento(id, logado);

    return ResponseEntity.ok(agendamento);
  }

}
