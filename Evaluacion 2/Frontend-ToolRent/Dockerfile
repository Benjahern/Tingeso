# 1. Usar una imagen base de Node.js (LTS version recomendada)
# 'alpine' es una versión ligera de Linux
FROM node:20-alpine

# 2. Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# 3. Copiar los archivos de dependencias primero (para aprovechar la caché de Docker)
COPY package*.json ./

# 4. Instalar las dependencias
RUN npm install

# 5. Copiar el resto del código fuente
COPY . .

# 6. Exponer el puerto que usa tu framework (Vite suele usar 5173, React/Next 3000)
# Ajusta este número según lo que diga tu consola al correr npm run dev localmente
EXPOSE 5173 

# 7. Ejecutar el comando de desarrollo
CMD ["npm", "run", "dev", "--", "--host"]