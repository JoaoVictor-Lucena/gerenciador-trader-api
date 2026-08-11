package br.com.gerenciadortrader.controller;

import br.com.gerenciadortrader.model.Anotacao;
import br.com.gerenciadortrader.model.Confronto;
import br.com.gerenciadortrader.model.Estrategia;
import br.com.gerenciadortrader.repository.ConfrontoRepository;
import br.com.gerenciadortrader.repository.EstrategiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        confrontoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
