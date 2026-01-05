ARG alpine_version=3.22

FROM alpine:$alpine_version AS builder

RUN apk add --no-cache npm
COPY package*.json ./
RUN npm install --production

FROM alpine:$alpine_version AS base

RUN apk add --no-cache nodejs bash
COPY --from=builder /node_modules /node_modules

FROM base AS dev

RUN apk add --no-cache openjdk17
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
