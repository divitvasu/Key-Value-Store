#BASE IMAGE
FROM bellsoft/liberica-openjdk-alpine-musl:17 AS UDPserver-build
COPY . /docker
WORKDIR /docker
RUN javac Server_UDP_app.java
CMD ["java", "Server_UDP_app"]

#BASE IMAGE
FROM bellsoft/liberica-openjdk-alpine-musl:17 AS UDPclient-build
COPY . /docker
WORKDIR /docker
RUN javac Client_UDP_app.java
CMD ["java", "Client_UDP_app"]
