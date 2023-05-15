#BASE IMAGE
FROM bellsoft/liberica-openjdk-alpine-musl:17 AS TCPserver-build
COPY . /docker
WORKDIR /docker
RUN javac Server_TCP_app.java
CMD ["java", "Server_TCP_app"]

#BASE IMAGE
FROM bellsoft/liberica-openjdk-alpine-musl:17 AS TCPclient-build
COPY . /docker
WORKDIR /docker
RUN javac Client_TCP_app.java
CMD ["java", "Client_TCP_app"]
