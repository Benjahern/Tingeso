import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Vite dev server config: this proxies API and Auth routes to backend and keycloak
// so the frontend can use relative /api and /auth paths during local development.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // Forward /api/* to local spring backend (adjust port if needed)
      '/api': {
        target: 'http://toolrent-backend-1:8090',
        changeOrigin: true,
        secure: false,
        // keep /api in the forwarded path
        rewrite: (path) => path.replace(/^\/api/, '/api')
      },
      // Forward Keycloak endpoints
      '/auth': {
        target: 'http://keycloak:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/auth/, '/auth')
      },
      '/realms': {
        target: 'http://keycloak:8080',
        changeOrigin: true,
        secure: false
      }
    }
  ,
    hmr: {
      protocol: 'ws',
      host: 'localhost',
      port: 5173,
      clientPort: 5173
    }
  }
})
