package br.com.alvorada.barbearia.controller;

import br.com.alvorada.barbearia.dto.barbeiro.*;
import br.com.alvorada.barbearia.model.Usuario;
import br.com.alvorada.barbearia.service.BarbeiroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/barbeiros")
public class BarbeiroController {

  private final BarbeiroService service;

  public BarbeiroController(BarbeiroService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<List<BarbeiroDto>> listarBarbeiros() {
    var barbeiros = service.listarBarbeiros();

    return ResponseEntity.ok(barbeiros);
  }

  @GetMapping("/ativo")
  public ResponseEntity<List<BarbeiroDto>> listarBarbeirosAtivos() {
    var barbeiros = service.listarBarbeirosAtivos();

    return ResponseEntity.ok(barbeiros);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DetalhamentoBarbeiroDto> detalharBarbeiro(
          @PathVariable Long id
  ) {
    var barbeiro = service.detalharBarbeiro(id);

    return ResponseEntity.ok(barbeiro);
  }

  @GetMapping("/listar-horarios-disponiveis/{id}/{data}")
  public ResponseEntity<List<HorarioDto>> listarHorariosDisponiveis(
          @PathVariable Long id,
          @PathVariable String data
  ) {
    var horariosDisponiveis = service.listarHorariosDisponiveis(id, data);

    return ResponseEntity.ok(horariosDisponiveis);
  }

  @PostMapping("/cadastrar")
  public ResponseEntity<DetalhamentoBarbeiroDto> cadastrar(
          @Valid @RequestBody CadastroBarbeiroDto dto,
          UriComponentsBuilder uriBuilder
  ) {
    var barbeiro = service.cadastrar(dto);
    var uri = uriBuilder
            .path("/barbeiros/{id}")
            .buildAndExpand(barbeiro.id())
            .toUri();

    return ResponseEntity.created(uri).body(barbeiro);
  }

  @PatchMapping("/atualizar-dados")
  public ResponseEntity<DetalhamentoBarbeiroDto> atualizarDados(
          @Valid @RequestBody AtualizacaoDadosBarbeiroDto dto,
          @AuthenticationPrincipal Usuario logado
  ) {
    var barbeiro = service.atualizarDados(dto, logado);

    return ResponseEntity.ok(barbeiro);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletarBarbeiro(
          @PathVariable Long id,
          @AuthenticationPrincipal Usuario logado
  ) {
    service.deletarBarbeiro(id, logado);

    return ResponseEntity.noContent().build();
  }

}
