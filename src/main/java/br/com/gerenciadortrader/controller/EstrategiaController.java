package br.com.gerenciadortrader.controller;

import br.com.gerenciadortrader.model.Estrategia;
import br.com.gerenciadortrader.repository.EstrategiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estrategias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EstrategiaController {

    private final EstrategiaRepository estrategiaRepository;

    @GetMapping
    public List<Estrategia> listarTodas() {
        return estrategiaRepository.findAll();
    }

    @PostMapping
    public Estrategia salvar(@RequestBody Estrategia estrategia) {
        return estrategiaRepository.save(estrategia);
    }
}
