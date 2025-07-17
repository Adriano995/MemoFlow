import http from 'k6/http';
import { check } from 'k6';

//10 Utenti fittizi per il test, 10 secondi di durata
/*export const options = {
  vus: 20, // utenti virtuali
  duration: '20s', // durata test
};*/

export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '20s', target: 50 },
    { duration: '10s', target: 100 },
    { duration: '40s', target: 60 },
    { duration: '10s', target: 0 }, // riduce a 0 per terminare il test
  ],
};


//Test per il login
/*export default function () {
  const url = 'http://localhost:8080/auth/login'; // aggiorna se diverso
  const payload = JSON.stringify({
    email: 'adriano@email.com',
    password: 'banana123',
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  http.post(url, payload, params);
}*/

//Test per la lista utenti
/*export default function () {
  http.get('http://localhost:8080/utente/listaUtenti');
}*/

//Test cattivo e randomico
/*export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '20s', target: 30 },
    { duration: '10s', target: 0 },
  ],
};*/

export default function () {
  // 🔐 Login per ottenere token
  const loginRes = http.post('http://localhost:8080/auth/login', JSON.stringify({
    email: 'adriano@email.com',
    password: 'banana123',
  }), {
    headers: { 'Content-Type': 'application/json' },
  });

  // ✅ Estrai token
  const token = loginRes.json('token');

  // 🛑 Verifica che ci sia
  if (!token) {
    console.error('Token non ricevuto!');
    return;
  }

  const headers = {
    headers: { 'Authorization': `Bearer ${token}` },
  };

  const endpoints = [
    'http://localhost:8080/nota/listaTutte',
    'http://localhost:8080/utente/listaUtenti',
  ];

  const randomUrl = endpoints[Math.floor(Math.random() * endpoints.length)];

  const res = http.get(randomUrl, headers);

  check(res, {
    'status is 200': (r) => r.status === 200,
    'response not empty': (r) => r.body && r.body.length > 0,
  });
}