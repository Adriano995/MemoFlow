package sviluppo.adriano.MemoFlow.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sviluppo.adriano.MemoFlow.dto.creaDTO.NotaCreateDTO;
import sviluppo.adriano.MemoFlow.dto.modificaDTO.CambiaNotaDTO;
import sviluppo.adriano.MemoFlow.dto.NotaDTO;
import sviluppo.adriano.MemoFlow.entity.Nota;
import sviluppo.adriano.MemoFlow.entity.Utente;
import sviluppo.adriano.MemoFlow.mapper.NotaMapper;
import sviluppo.adriano.MemoFlow.repository.NotaRepository;
import sviluppo.adriano.MemoFlow.repository.UtenteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotaService {

    private NotaRepository notaRepository;
    private UtenteRepository utenteRepository;
    private NotaMapper notaMapper;

    @Autowired
    public NotaService(NotaRepository notaRepository, UtenteRepository utenteRepository, NotaMapper notaMapper){
        this.notaRepository = notaRepository;
        this.utenteRepository = utenteRepository;
        this.notaMapper = notaMapper;
    }

    public List<NotaDTO> cercaTutte() {
        List<Nota> note = notaRepository.findAll();
        return note.stream()
                .map(notaMapper::toDto)
                .toList();
    }

    public List<NotaDTO> cercaTuttePerDataEUtente(LocalDateTime data, Long utenteId) {
        LocalDateTime startOfDay = data.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = data.toLocalDate().atTime(23, 59, 59, 999_999_999);

        // Aggiungi il metodo al repository (vedi sotto per NotaRepository)
        List<Nota> note = notaRepository.findAllByDataCreazioneBetweenAndUtenteId(startOfDay, endOfDay, utenteId); //
        return note.stream()
                .map(notaMapper::toDto) //
                .collect(Collectors.toList());
    }

    public NotaDTO creaNota(NotaCreateDTO createDto) {
        // Controllo validità ID utente
        if (createDto.getUtenteId() == null) {
            throw new IllegalArgumentException("Utente mancante o ID non specificato");
        }

        // Controllo validità data
        if (createDto.getDataNota() == null) {
            throw new IllegalArgumentException("Data della nota non specificata");
        }

        // Recupero utente da DB
        Utente utente = utenteRepository.findById(createDto.getUtenteId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato"));

        // Creo entity da DTO
        Nota nota = notaMapper.toEntity(createDto);
        nota.setUtente(utente);

        nota.setDataCreazione(createDto.getDataNota().atStartOfDay());
        nota.setUltimaModifica(createDto.getDataNota().atStartOfDay());

        Nota salvata = notaRepository.save(nota);

        return notaMapper.toDto(salvata);
    }

    public NotaDTO aggiornaNota(Long id, CambiaNotaDTO modifica) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nota con ID " + id + " non trovata"));

        // Aggiorna i campi della nota
        if (modifica.getTitolo() != null) {
            nota.setTitolo(modifica.getTitolo());
        }

        if (modifica.getContenutoTesto() != null) {
            nota.setContenutoTesto(modifica.getContenutoTesto());
        }

        if (modifica.getTipoNota() != null) {
            nota.setTipoNota(modifica.getTipoNota());
        }

        nota.setUltimaModifica(LocalDateTime.now());

        Nota salvata = notaRepository.save(nota);
        return notaMapper.toDto(salvata);
    }

    public void eliminaNota(Long id){
        if (!notaRepository.existsById(id)){
            throw new EntityNotFoundException("Nota con ID " + id + " non trovata");
        }
        notaRepository.deleteById(id);
    }
}