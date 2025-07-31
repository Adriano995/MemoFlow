import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 10 },    // Rampa su a 10 utenti in 10 secondi
    { duration: '20s', target: 50 },    // Mantieni 50 utenti per 20 secondi
    { duration: '10s', target: 100 },   // Rampa su a 100 utenti in 10 secondi
    { duration: '40s', target: 60 },    // Rampa giù a 60 utenti per 40 secondi
    { duration: '10s', target: 0 },     // Rampa giù a 0 utenti in 10 secondi
  ],
  thresholds: {
    'http_req_failed': ['rate<0.01'],   // Tasso di fallimento richieste HTTP < 1%
    'http_req_duration': ['p(95)<500'], // 95% delle richieste deve essere completato in meno di 500ms
    'checks': ['rate>0.90'],            // Tasso di successo dei check > 90%
  },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  sleep(2); // Breve ritardo iniziale per distribuire l'avvio dei VU

  // --- 1. Login ---
  const loginPayload = JSON.stringify({
    email: 'test@example.com', // L'email dell'utente nel data.sql
    password: 'banana123', // La password non bcrypt, come hai risolto
  });

  const loginRes = http.post(`${BASE_URL}/auth/login`, loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  const token = loginRes.json('token');
  if (!check(loginRes, { 'Login successful': (r) => r.status === 200 && token !== null })) {
    console.error(`ERRORE: Login fallito con stato ${loginRes.status}: ${loginRes.body}`);
    return; // Interrompi l'esecuzione per questo VU se il login fallisce
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Login riuscito. Token: ${token.substring(0, 10)}...`);

  const authHeaders = {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };

  // --- 2. Operazioni GET di lettura globale/filtrata (Read All) ---
  console.log(`VU ${__VU}, ITER ${__ITER}: Inizio GET di lettura globale...`);
  const getEndpoints = [
    { name: 'GET_Utenti_ListaTutti', url: `${BASE_URL}/utente/listaUtenti` },
    { name: 'GET_Note_ListaTutte', url: `${BASE_URL}/nota/listaTutte` },
    { name: 'GET_Note_PerUtente1', url: `${BASE_URL}/nota/perUtente?utenteId=1` },
    { name: 'GET_Eventi_ListaTutti', url: `${BASE_URL}/eventi/listaTutti` },
    { name: 'GET_Eventi_StatoProgrammato', url: `${BASE_URL}/eventi/stato?stato=STATO_PROGRAMMATO` },
  ];

  for (let i = 0; i < getEndpoints.length; i++) {
    const ep = getEndpoints[i];
    const getRes = http.get(ep.url, authHeaders, { tags: { name: ep.name } });

    // Gestione speciale per /nota/perUtente (potrebbe restituire 404 se non ci sono note)
    if (ep.url.includes('/nota/perUtente')) {
      check(getRes, {
        [`${ep.name} status is 2xx or 404 (if empty)`]: (r) => (r.status >= 200 && r.status < 300) || r.status === 404,
      });
    } else {
      check(getRes, {
        [`${ep.name} status is 2xx`]: (r) => r.status >= 200 && r.status < 300,
      });
    }
    if (getRes.status >= 400) {
        console.error(`ERRORE ${ep.name}: Status ${getRes.status}, Body: ${getRes.body}`);
    }
    sleep(0.5);
  }

  // --- 3. Ciclo CRUD per UTENTE (richiede un utente con ruoli admin o developer) ---
  console.log(`VU ${__VU}, ITER ${__ITER}: Inizio CRUD Utente...`);
  let newUserId;
  let newUserEmail = `k6test_user_${__VU}_${__ITER}@example.com`;

  // 3.1 Crea un nuovo utente (POST)
  const createUserPayload = JSON.stringify({
    nome: `TestNome${__VU}${__ITER}`,
    cognome: `TestCognome${__VU}${__ITER}`,
    credenziali: { // <-- Questo è l'oggetto annidato!
        email: newUserEmail,
        password: `password${__VU}${__ITER}`,
    },
  });

  const createUserRes = http.post(`${BASE_URL}/utente/creaUtente`, createUserPayload, authHeaders, { tags: { name: 'POST_Utente_Crea' } });
  check(createUserRes, {
    'Create User status is 2xx': (r) => r.status >= 200 && r.status < 300,
    'Create User response contains ID': (r) => r.json('id') !== undefined,
  });
  if (createUserRes.status >= 400) {
      console.error(`ERRORE POST_Utente_Crea: Status ${createUserRes.status}, Body: ${createUserRes.body}`);
  }
  newUserId = createUserRes.json('id');
  if (!newUserId) {
    console.warn(`VU ${__VU}, ITER ${__ITER}: Impossibile creare utente, saltando le operazioni successive sull'utente.`);
    return; // O gestisci l'errore in altro modo
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Creato Utente con ID: ${newUserId}`);
  sleep(1);

  // 3.2 Ottieni l'utente appena creato per ID (GET)
  const getUserByIdRes = http.get(`${BASE_URL}/utente/${newUserId}`, authHeaders, { tags: { name: 'GET_Utente_ById' } });
  check(getUserByIdRes, {
    'Get User By ID status is 2xx': (r) => r.status >= 200 && r.status < 300,
    'Get User By ID response matches created ID': (r) => r.json('id') === newUserId,
  });
  if (getUserByIdRes.status >= 400) {
      console.error(`ERRORE GET_Utente_ById: Status ${getUserByIdRes.status}, Body: ${getUserByIdRes.body}`);
  }
  sleep(1);

  // 3.3 Modifica i dati dell'utente (PUT)
  const updateUserPayload = JSON.stringify({
    nome: `NomeAggiornato${__VU}${__ITER}`,
    cognome: `CognomeAggiornato${__VU}${__ITER}`,
    email: newUserEmail // L'email non viene modificata qui, se non c'è un DTO apposito
  });
  const updateUserRes = http.put(`${BASE_URL}/utente/modificaDati/${newUserId}`, updateUserPayload, authHeaders, { tags: { name: 'PUT_Utente_ModificaDati' } });
  check(updateUserRes, {
    'Update User status is 2xx': (r) => r.status >= 200 && r.status < 300,
  });
  if (updateUserRes.status >= 400) {
      console.error(`ERRORE PUT_Utente_ModificaDati: Status ${updateUserRes.status}, Body: ${updateUserRes.body}`);
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Aggiornato Utente con ID: ${newUserId}, Status: ${updateUserRes.status}`);
  sleep(1);

  // 3.4 Elimina l'utente (DELETE)
  const deleteUserRes = http.del(`${BASE_URL}/utente/eliminaUtente/${newUserId}`, null, authHeaders, { tags: { name: 'DELETE_Utente_Elimina' } });
  check(deleteUserRes, {
    'Delete User status is 2xx': (r) => r.status >= 200 && r.status < 300,
  });
  if (deleteUserRes.status >= 400) {
      console.error(`ERRORE DELETE_Utente_Elimina: Status ${deleteUserRes.status}, Body: ${deleteUserRes.body}`);
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Eliminato Utente con ID: ${newUserId}, Status: ${deleteUserRes.status}`);
  sleep(1);


  // --- 4. Ciclo CRUD per NOTA ---
  console.log(`VU ${__VU}, ITER ${__ITER}: Inizio CRUD Nota...`);
  let newNoteId;

  // 4.1 Crea una nuova nota (POST)
  const createNotePayload = JSON.stringify({
    titolo: `Nota K6 - VU${__VU}-ITER${__ITER}`,
    contenutoTesto: `Contenuto della nota creata da K6. VU: ${__VU}, Iter: ${__ITER}`,
    tipoNota: "TESTO" // Assicurati che il tipo nota sia valido (es. TESTO, IMMAGINE, AUDIO)
  });

  const createNoteRes = http.post(`${BASE_URL}/nota/creaNota`, createNotePayload, authHeaders, { tags: { name: 'POST_Nota_Crea' } });
  check(createNoteRes, {
    'Create Note status is 2xx': (r) => r.status >= 200 && r.status < 300,
    'Create Note response contains ID': (r) => r.json('id') !== undefined,
  });
  if (createNoteRes.status >= 400) {
      console.error(`ERRORE POST_Nota_Crea: Status ${createNoteRes.status}, Body: ${createNoteRes.body}`);
  }
  newNoteId = createNoteRes.json('id');
  if (!newNoteId) {
    console.warn(`VU ${__VU}, ITER ${__ITER}: Impossibile creare nota, saltando le operazioni successive sulla nota.`);
    return;
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Creata Nota con ID: ${newNoteId}`);
  sleep(1);

  // 4.2 Ottieni la nota appena creata per ID (GET)
  const getNoteByIdRes = http.get(`${BASE_URL}/nota/${newNoteId}`, authHeaders, { tags: { name: 'GET_Nota_ById' } });
  check(getNoteByIdRes, {
    'Get Nota By ID status is 2xx': (r) => r.status >= 200 && r.status < 300,
    'Get Nota By ID response matches created ID': (r) => r.json('id') === newNoteId,
  });
  if (getNoteByIdRes.status >= 400) {
      console.error(`ERRORE GET_Nota_ById: Status ${getNoteByIdRes.status}, Body: ${getNoteByIdRes.body}`);
  }
  sleep(1);

  // 4.3 Modifica la nota appena creata (PUT)
  const updateNotePayload = JSON.stringify({
    titolo: `Nota Aggiornata K6 - VU${__VU}-ITER${__ITER}`,
    contenutoTesto: "Contenuto aggiornato da K6",
    tipoNota: "TESTO"
  });

  const updateNoteRes = http.put(`${BASE_URL}/nota/modificaNota/${newNoteId}`, updateNotePayload, authHeaders, { tags: { name: 'PUT_Nota_Modifica' } });
  check(updateNoteRes, {
    'Update Note status is 2xx': (r) => r.status >= 200 && r.status < 300,
  });
  if (updateNoteRes.status >= 400) {
      console.error(`ERRORE PUT_Nota_Modifica: Status ${updateNoteRes.status}, Body: ${updateNoteRes.body}`);
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Aggiornata Nota con ID: ${newNoteId}, Status: ${updateNoteRes.status}`);
  sleep(1);

  // 4.4 Elimina la nota appena modificata (DELETE)
  const deleteNoteRes = http.del(`${BASE_URL}/nota/eliminaNota/${newNoteId}`, null, authHeaders, { tags: { name: 'DELETE_Nota_Elimina' } });
  check(deleteNoteRes, {
    'Delete Note status is 2xx': (r) => r.status >= 200 && r.status < 300,
  });
  if (deleteNoteRes.status >= 400) {
      console.error(`ERRORE DELETE_Nota_Elimina: Status ${deleteNoteRes.status}, Body: ${deleteNoteRes.body}`);
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Eliminata Nota con ID: ${newNoteId}, Status: ${deleteNoteRes.status}`);
  sleep(1);


  // --- 5. Ciclo CRUD per EVENTO ---
  console.log(`VU ${__VU}, ITER ${__ITER}: Inizio CRUD Evento...`);
  let newEventId;

  // 5.1 Crea un nuovo evento (POST)
  const createEventPayload = JSON.stringify({
    titolo: `Test Evento K6 - VU${__VU}-ITER${__ITER}`,
    descrizione: `Evento creato da K6 VU: ${__VU}, Iter: ${__ITER}`,
    dataInizio: "2025-07-20T10:00:00", // Formato ISO 8601
    dataFine: "2025-07-20T12:00:00",
    luogo: "Online",
    stato: "STATO_PROGRAMMATO"
  });

  const createEventRes = http.post(`${BASE_URL}/eventi/creaEvento`, createEventPayload, authHeaders, { tags: { name: 'POST_Evento_Crea' } });
  check(createEventRes, {
    'Create Event status is 2xx': (r) => r.status >= 200 && r.status < 300,
    'Create Event response contains ID': (r) => r.json('id') !== undefined,
  });
  if (createEventRes.status >= 400) {
      console.error(`ERRORE POST_Evento_Crea: Status ${createEventRes.status}, Body: ${createEventRes.body}`);
  }
  newEventId = createEventRes.json('id');
  if (!newEventId) {
    console.warn(`VU ${__VU}, ITER ${__ITER}: Impossibile creare evento, saltando le operazioni successive sull'evento.`);
    return;
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Creato Evento con ID: ${newEventId}`);
  sleep(1);

  // 5.2 Ottieni l'evento appena creato per ID (GET)
  const getEventByIdRes = http.get(`${BASE_URL}/eventi/${newEventId}`, authHeaders, { tags: { name: 'GET_Evento_ById' } });
  check(getEventByIdRes, {
    'Get Event By ID status is 2xx': (r) => r.status >= 200 && r.status < 300,
    'Get Event By ID response matches created ID': (r) => r.json('id') === newEventId,
  });
  if (getEventByIdRes.status >= 400) {
      console.error(`ERRORE GET_Evento_ById: Status ${getEventByIdRes.status}, Body: ${getEventByIdRes.body}`);
  }
  sleep(1);

  // 5.3 Modifica l'evento appena creato (PUT)
  const updateEventPayload = JSON.stringify({
    titolo: `Evento Aggiornato K6 - VU${__VU}-ITER${__ITER}`,
    descrizione: "Descrizione aggiornata da K6",
    stato: "STATO_IN_CORSO",
    dataInizio: "2025-07-20T10:00:00",
    dataFine: "2025-07-20T12:00:00",
    luogo: "Online",
  });

  const updateEventRes = http.put(`${BASE_URL}/eventi/modificaEvento/${newEventId}`, updateEventPayload, authHeaders, { tags: { name: 'PUT_Evento_Modifica' } });
  check(updateEventRes, {
    'Update Event status is 2xx': (r) => r.status >= 200 && r.status < 300,
  });
  if (updateEventRes.status >= 400) {
      console.error(`ERRORE PUT_Evento_Modifica: Status ${updateEventRes.status}, Body: ${updateEventRes.body}`);
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Aggiornato Evento con ID: ${newEventId}, Status: ${updateEventRes.status}`);
  sleep(1);

  // 5.4 Elimina l'evento appena modificato (DELETE)
  const deleteEventRes = http.del(`${BASE_URL}/eventi/eliminaEvento/${newEventId}`, null, authHeaders, { tags: { name: 'DELETE_Evento_Elimina' } });
  check(deleteEventRes, {
    'Delete Event status is 2xx': (r) => r.status >= 200 && r.status < 300,
  });
  if (deleteEventRes.status >= 400) {
      console.error(`ERRORE DELETE_Evento_Elimina: Status ${deleteEventRes.status}, Body: ${deleteEventRes.body}`);
  }
  console.log(`VU ${__VU}, ITER ${__ITER}: Eliminato Evento con ID: ${newEventId}, Status: ${deleteEventRes.status}`);
  sleep(1);
}