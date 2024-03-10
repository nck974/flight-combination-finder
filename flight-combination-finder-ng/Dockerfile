# docker build -f Dockerfile -t nck974/flight-combination-finder-ng:1.0.4 . --no-cache
# docker push nck974/flight-combination-finder-ng:1.0.4
#################
# Build the app #
#################
FROM node:20-alpine AS build

WORKDIR /app

RUN npm install -g @angular/cli

COPY package.json package-lock.json ./
RUN npm install --fetch-timeout=600000 
COPY . .

RUN ng build --configuration production --output-path=/dist

################
# Run in NGINX #
################
FROM nginx:alpine

COPY --from=build /dist/browser /usr/share/nginx/html

# When the container starts, replace the env.js with values from environment variables
CMD ["/bin/sh",  "-c",  "envsubst < /usr/share/nginx/html/assets/env.template.js > /usr/share/nginx/html/assets/env.js && exec nginx -g 'daemon off;'"]
