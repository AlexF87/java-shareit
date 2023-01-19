FROM amazoncorretto:11-alpine-jdk
ENV TZ=Asia/Novosibirsk

RUN apk add --no-cache tzdata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY target/*.jar server.jar
ENTRYPOINT ["java", "-jar","/server.jar"]