FROM gradle:8.4-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle installDist -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/install/kursovaya /app
EXPOSE 8080
ENTRYPOINT ["/app/bin/kursovaya"]