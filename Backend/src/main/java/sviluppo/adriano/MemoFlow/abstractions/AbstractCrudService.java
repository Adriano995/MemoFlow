package sviluppo.adriano.MemoFlow.abstractions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Classe astratta generica per fornire operazioni CRUD base.
 * 
 * @param <T>  Entity (es. Nota, Utente)
 * @param <ID> Tipo dell'ID (es. Long)
 * @param <D>  DTO di output
 * @param <C>  DTO per la creazione
 * @param <U>  DTO per la modifica
 * @param <R>  Repository che estende JpaRepository<T, ID>
 */
public abstract class AbstractCrudService<
        T,
        ID,
        D,
        C,
        U,
        R extends JpaRepository<T, ID>> {

    protected final R repository;

    public AbstractCrudService(R repository) {
        this.repository = repository;
    }

    /**
     * Converte un'entità in DTO
     */
    protected abstract D toDto(T entity);

    /**
     * Converte un DTO di creazione in entità
     */
    protected abstract T toEntity(C createDto);

    /**
     * Applica le modifiche da un DTO all'entità
     */
    protected abstract void updateEntity(T entity, U updateDto);

    /**
     * Restituisce tutti gli elementi convertiti in DTO
     */
    public List<D> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Cerca un singolo elemento per ID
     */
    public D findById(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Elemento con ID " + id + " non trovato"));
        return toDto(entity);
    }

    /**
     * Crea un nuovo elemento
     */
    public D create(C createDto) {
        T entity = toEntity(createDto);
        T saved = repository.save(entity);
        return toDto(saved);
    }

    /**
     * Aggiorna un elemento esistente
     */
    public D update(ID id, U updateDto) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Elemento con ID " + id + " non trovato"));
        updateEntity(entity, updateDto);
        T updated = repository.save(entity);
        return toDto(updated);
    }

    /**
     * Elimina un elemento per ID
     */
    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Elemento con ID " + id + " non trovato");
        }
        repository.deleteById(id);
    }
}
