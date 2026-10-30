# Build stage: API_URL is read at build time, when Next.js writes the /api rewrite (L34, point 1).
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
ARG API_URL=http://backend:8080
ENV API_URL=$API_URL
RUN npm run build

# Run stage: production dependencies and the built app.
FROM node:22-alpine
WORKDIR /app
ENV NODE_ENV=production
ARG API_URL=http://backend:8080
ENV API_URL=$API_URL
COPY package.json package-lock.json ./
RUN npm ci --omit=dev
COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public
COPY --from=build /app/next.config.ts ./next.config.ts
EXPOSE 3000
CMD ["npm", "start"]
