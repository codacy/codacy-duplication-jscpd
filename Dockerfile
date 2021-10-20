ARG alpine_version=3.14.2

FROM alpine:$alpine_version as builder

RUN apk add --no-cache npm
COPY package*.json ./
RUN npm install --production

FROM alpine:$alpine_version as base

RUN apk add --no-cache nodejs bash
COPY --from=builder /node_modules /node_modules

FROM base as dev

RUN apk add --no-cache openjdk11
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
