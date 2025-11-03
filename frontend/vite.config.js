import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/upload': 'http://localhost:8080',
      '/upload-multi': 'http://localhost:8080',
      '/upload-tax-document': 'http://localhost:8080',
      '/upload-tax-documents': 'http://localhost:8080',
      '/download': 'http://localhost:8080',
      '/download-tax-documents': 'http://localhost:8080'
    }
  }
})
