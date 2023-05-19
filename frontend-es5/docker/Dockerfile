FROM node:14.3-stretch as build

COPY package.json package-lock.json *.js /frontend/

WORKDIR /frontend
RUN npm install

COPY ./src/ ./src/

RUN npm run build

FROM nginx:alpine

COPY --from=build /frontend/dist /usr/share/nginx/html
COPY ./docker/entrypoint.sh entrypoint.sh

RUN ["chmod", "+x", "entrypoint.sh"]
CMD ["/bin/sh", "entrypoint.sh"]
