FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src/ src/
COPY .git/ .git/
RUN mvn package -B -DskipTests && rm -rf .git
RUN mkdir -p /app/expanded && cd /app/expanded && jar xf /app/target/toldyouso.war

FROM tomcat:10.1-jre21
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/expanded/ /usr/local/tomcat/webapps/ROOT/
EXPOSE 8080
CMD ["catalina.sh", "run"]
