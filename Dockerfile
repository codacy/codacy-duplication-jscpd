FROM alpine:3.12 as base

RUN apk add --no-cache npm bash
COPY package*.json ./
RUN npm install

FROM base as dev

RUN apk add openjdk11
COPY docs /docs
RUN adduser --uid 2004 --disabled-password --gecos "" docker
COPY target/universal/stage/ /workdir/
RUN chmod +x /workdir/bin/codacy-duplication-jscpd
USER docker
WORKDIR /src
ENTRYPOINT ["/workdir/bin/codacy-duplication-jscpd"]

FROM base

COPY docs /docs
RUN adduser --uid 2004 --disabled-password --gecos "" docker
COPY target/graalvm-native-image/codacy-duplication-jscpd /workdir/
USER docker
WORKDIR /src
ENTRYPOINT ["/workdir/codacy-duplication-jscpd"]
