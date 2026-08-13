package br.com.gerenciadortrader.controller;

import br.com.gerenciadortrader.dto.JogoResponseDTO;
import br.com.gerenciadortrader.service.JogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/jogos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class JogoController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JogoService jogoService;

    /**
     * Retorna os jogos de uma data. Se nenhuma data for informada, usa a data
     * atual.
     *
     * @param data (opcional) data no formato 'YYYY-MM-DD'
     * @return lista de jogos do dia
     *
     *         Exemplos:
     *         GET /jogos → jogos de hoje
     *         GET /jogos?data=2026-08-12 → jogos do dia específico
     */
    @GetMapping
    public List<JogoResponseDTO> buscarJogos(
            @RequestParam(required = false) String data) {

        String dataConsulta = (data != null && !data.isBlank())
                ? data
                : LocalDate.now().format(FORMATO_DATA);

        return jogoService.buscarJogosPorData(dataConsulta);
    }
}
