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

ADD --chmod=644 https://repo1.maven.org/maven2/org/redisson/redisson-all/3.50.0/redisson-all-3.50.0.jar /usr/local/tomcat/lib/
ADD --chmod=644 https://repo1.maven.org/maven2/org/redisson/redisson-tomcat-10/3.50.0/redisson-tomcat-10-3.50.0.jar /usr/local/tomcat/lib/

COPY docker/context.xml /usr/local/tomcat/conf/context.xml
COPY docker/redisson.yaml /usr/local/tomcat/conf/redisson.yaml

COPY --from=build /app/expanded/ /usr/local/tomcat/webapps/ROOT/
EXPOSE 8080
CMD ["catalina.sh", "run"]
