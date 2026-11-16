// Configuración de Vite.
// En desarrollo (npm run dev) el servidor de Vite reenvía /api y /graphql al core-service
// (mismo truco que el nginx de Docker: el navegador habla con un solo origen, sin CORS).
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const puertoCore = process.env.CORE_PUERTO || '8080';
const core = `http://localhost:${puertoCore}`;

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': core,
      '/graphql': core,
    },
  },
});
