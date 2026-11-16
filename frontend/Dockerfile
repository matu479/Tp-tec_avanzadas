# Dockerfile multi-stage del frontend React.
# Se construye con el contexto en la carpeta frontend/: docker compose build frontend

# ---------- Etapa 1: compilar con Node ----------
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# ---------- Etapa 2: nginx sirve dist/ y hace de proxy al core ----------
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
