package br.com.gerenciadortrader.controller;

import br.com.gerenciadortrader.client.ApiFootballClient;
import br.com.gerenciadortrader.dto.JogoResponseDTO;
import br.com.gerenciadortrader.model.Estrategia;
import br.com.gerenciadortrader.service.EstrategiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estrategias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EstrategiaController {

    private final EstrategiaService estrategiaService;

    @GetMapping
    public List<Estrategia> listarTodas() {
        return estrategiaService.listarTodas();
    }

    @PostMapping
    public Estrategia salvar(@RequestBody Estrategia estrategia) {
        return estrategiaService.salvar(estrategia);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        estrategiaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
