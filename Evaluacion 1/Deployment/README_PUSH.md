Uso: construir y pushear imágenes a Docker Hub

1) Variables de entorno necesarias:
- `DOCKERHUB_USER` : tu usuario de Docker Hub
- `DOCKERHUB_PASS` : tu password o token de Docker Hub
- Opcional: `BACKEND_IMAGE_NAME` y `BACKEND_IMAGE_TAG` para sobreescribir el nombre y tag por defecto en `docker-compose.prod.yml`
- Opcional: `FRONTEND_IMAGE_NAME` y `FRONTEND_IMAGE_TAG`

2) Recomendación: en `docker-compose.prod.yml` deja `image` con la forma `usuario/repo:tag` (puedes establecer las variables anteriores antes de ejecutar).

3) Comandos:

```
export DOCKERHUB_USER=miusuario
export DOCKERHUB_PASS=mi_token_o_password
export BACKEND_IMAGE_NAME=miusuario/toolrent-backend
export BACKEND_IMAGE_TAG=latest
export FRONTEND_IMAGE_NAME=miusuario/toolrent-frontend
export FRONTEND_IMAGE_TAG=latest
bash Deployment/scripts/build-and-push.sh
```

Esto hará `docker compose build` y `docker compose push` para los servicios `toolrent-backend-1` y `toolrent-frontend-1` definidos en `Deployment/docker-compose.prod.yml`.
