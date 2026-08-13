package br.com.gerenciadortrader.controller;

import br.com.gerenciadortrader.dto.JogoResponseDTO;
import br.com.gerenciadortrader.model.Anotacao;
import br.com.gerenciadortrader.model.Confronto;
import br.com.gerenciadortrader.model.Estrategia;
import br.com.gerenciadortrader.model.StatusOperacao;
import br.com.gerenciadortrader.repository.ConfrontoRepository;
import br.com.gerenciadortrader.repository.EstrategiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/confrontos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ConfrontoController {

    private final ConfrontoRepository confrontoRepository;
    private final EstrategiaRepository estrategiaRepository;

    @GetMapping
    public List<Confronto> listarTodos() {
        return confrontoRepository.findAll();
    }

    @PostMapping
    public Confronto salvar(@RequestBody Confronto confronto) {
        if (confronto.getAnotacoes() == null) {
            confronto.setAnotacoes(new ArrayList<>());
        }
        if (confronto.getEstrategias() == null) {
            confronto.setEstrategias(new ArrayList<>());
        }
        return confrontoRepository.save(confronto);
    }

    @PutMapping("/{id}/anotacoes")
    public ResponseEntity<Confronto> adicionarAnotacao(
            @PathVariable Long id,
            @RequestBody String textoAnotacao) {
        return confrontoRepository.findById(id).map(confronto -> {
            Anotacao novaAnotacao = new Anotacao();
            novaAnotacao.setTexto(textoAnotacao);
            novaAnotacao.setDataHora(LocalDateTime.now());
            novaAnotacao.setConfronto(confronto);

            if (confronto.getAnotacoes() == null) {
                confronto.setAnotacoes(new ArrayList<>());
            }
            confronto.getAnotacoes().add(novaAnotacao);

            Confronto confrontoAtualizado = confrontoRepository.save(confronto);
            return ResponseEntity.ok(confrontoAtualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idConfronto}/estrategias/{idEstrategia}")
    public ResponseEntity<Confronto> vincularEstrategia(
            @PathVariable Long idConfronto,
            @PathVariable Long idEstrategia) {

        Optional<Confronto> confrontoOptional = confrontoRepository.findById(idConfronto);
        if (confrontoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Estrategia> estrategiaOptional = estrategiaRepository.findById(idEstrategia);
        if (estrategiaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Confronto confronto = confrontoOptional.get();
        Estrategia estrategia = estrategiaOptional.get();

        if (confronto.getEstrategias() == null) {
            confronto.setEstrategias(new ArrayList<>());
        }

        confronto.getEstrategias().add(estrategia);
        Confronto confrontoAtualizado = confrontoRepository.save(confronto);

        return ResponseEntity.ok(confrontoAtualizado);
    }

    /**
     * Cria um Confronto a partir de um jogo da API-Football,
     * já vinculando-o à Estrategia informada.
     *
     * POST /confrontos/api-sports/estrategia/{idEstrategia}
     */
    @PostMapping("/api-sports/estrategia/{idEstrategia}")
    public ResponseEntity<Confronto> salvarDeApiSports(
            @PathVariable Long idEstrategia,
            @RequestBody JogoResponseDTO jogoApi,
            @RequestParam(defaultValue = "TRABALHAR") String status) {

        Optional<Estrategia> estrategiaOptional = estrategiaRepository.findById(idEstrategia);
        if (estrategiaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Estrategia estrategia = estrategiaOptional.get();

        // Converte "2026-08-12T15:00:00+00:00" → LocalDateTime no fuso do servidor
        LocalDateTime horario = OffsetDateTime.parse(jogoApi.dataHora())
                .toLocalDateTime();

        Confronto novoConfronto = Confronto.builder()
                .times(jogoApi.timeCasa() + " x " + jogoApi.timeVisitante())
                .campeonato(jogoApi.liga())
                .horario(horario)
                .escudoCasa(jogoApi.escudoCasa())
                .escudoVisitante(jogoApi.escudoVisitante())
                .status(StatusOperacao.valueOf(status))
                .anotacoes(new ArrayList<>())
                .estrategias(new ArrayList<>(List.of(estrategia)))
                .build();

        Confronto confrontoSalvo = confrontoRepository.save(novoConfronto);
        return ResponseEntity.status(HttpStatus.CREATED).body(confrontoSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        confrontoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
