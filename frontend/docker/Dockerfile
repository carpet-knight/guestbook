FROM node:18 as build

COPY package.json package-lock.json *.js /frontend/

WORKDIR /frontend

COPY ./scripts/ ./scripts/
COPY ./config/ ./config/
COPY ./public/ ./public/
COPY ./src ./src

RUN npm install

#RUN npm run build

FROM nginx:alpine

COPY --from=build /frontend/dist /usr/share/nginx/html
COPY ./docker/entrypoint.sh entrypoint.sh

RUN ["chmod", "+x", "entrypoint.sh"]
CMD ["/bin/sh", "entrypoint.sh"]
