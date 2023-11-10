FROM krmp-d2hub-idock.9rum.cc/goorm/gradle:7.3.1-jdk17 AS builder

WORKDIR project

COPY . .

RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

CMD pwd

CMD ls -al /home/gradle/project/build/libs/