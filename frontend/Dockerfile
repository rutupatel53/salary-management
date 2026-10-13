FROM node:24-alpine AS build
WORKDIR /workspace
COPY package*.json ./
RUN npm ci
COPY . .
ARG NG_APP_API_BASE=/api
ENV NG_APP_API_BASE=${NG_APP_API_BASE}
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/salary-management/browser /usr/share/nginx/html
EXPOSE 80
