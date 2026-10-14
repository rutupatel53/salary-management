FROM node:24-alpine AS build
WORKDIR /workspace
COPY package*.json ./
RUN npm ci
COPY . .
ARG BUILD_CONFIGURATION=production
RUN npm run build -- --configuration=${BUILD_CONFIGURATION}

FROM build AS unit-tests
RUN npm test

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/salary-management/browser /usr/share/nginx/html
EXPOSE 80
