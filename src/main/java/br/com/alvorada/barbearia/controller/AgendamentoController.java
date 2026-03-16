package br.com.alvorada.barbearia.controller;

import br.com.alvorada.barbearia.dto.agendamento.AgendamentoDto;
import br.com.alvorada.barbearia.dto.agendamento.AgendarDto;
import br.com.alvorada.barbearia.dto.agendamento.DetalhamentoAgendamentoDto;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

  @Autowired
  private AgendamentoService service;

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

  @PatchMapping("/concluir/{id}")
  public ResponseEntity<DetalhamentoAgendamentoDto> concluirAgendamento(
          @PathVariable Long id,
          @AuthenticationPrincipal Usuario logado
  ) {
    var agendamento = service.concluirAgendamento(id, logado);

    return ResponseEntity.ok(agendamento);
  }

  @PatchMapping("/cancelar/{id}")
  public ResponseEntity<DetalhamentoAgendamentoDto> cancelarAgendamento(
          @PathVariable Long id,
          @AuthenticationPrincipal Usuario logado
  ) {
    var agendamento = service.cancelarAgendamento(id, logado);

    return ResponseEntity.ok(agendamento);
  }

  @DeleteMapping("/deletar/{id}")
  public ResponseEntity<Void> excluirAgendamento(@PathVariable Long id) {
    service.deletarAgendamento(id);

    return ResponseEntity.noContent().build();
  }

}
