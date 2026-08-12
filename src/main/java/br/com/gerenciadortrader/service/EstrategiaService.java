package br.com.gerenciadortrader.service;

import br.com.gerenciadortrader.model.Estrategia;
import br.com.gerenciadortrader.repository.EstrategiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EstrategiaService {

    private final EstrategiaRepository estrategiaRepository;

    public List<Estrategia> listarTodas() {
        return estrategiaRepository.findAll();
    }

    public Estrategia salvar(Estrategia estrategia) {
        return estrategiaRepository.save(estrategia);
    }

    public void deletar(Long id) {
        if (!estrategiaRepository.existsById(id)) {
            throw new NoSuchElementException("Estratégia não encontrada com id: " + id);
        }
        estrategiaRepository.deleteById(id);
    }
}
