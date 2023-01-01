[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9b4cb2fec91146649dcf514278f24eab)](https://www.codacy.com/gh/codacy/codacy-duplication-jscpd?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=codacy/codacy-duplication-jscpd&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/9b4cb2fec91146649dcf514278f24eab)](https://www.codacy.com/gh/codacy/codacy-duplication-jscpd?utm_source=github.com&utm_medium=referral&utm_content=codacy/codacy-duplication-jscpd&utm_campaign=Badge_Coverage)
[![Build Status](https://circleci.com/gh/codacy/codacy-duplication-jscpd.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/codacy-duplication-jscpd)
[![Docker Version](https://images.microbadger.com/badges/version/codacy/codacy-duplication-jscpd.svg)](https://microbadger.com/images/codacy/codacy-duplication-jscpd "Get your own version badge on microbadger.com")

# Codacy Duplication jscpd

This is the duplication docker we use at Codacy to have [jscpd](https://github.com/kucherenko/jscpd) support.
You can also create a docker to integrate the tool and language of your choice!

## Usage

You can create the docker by doing:

```bash
sbt graalvm-native-image:packageBin
docker build -t codacy-duplication-jscpd .
```

You can run the docker with the following command:

```bash
docker run -it -v $srcDir:/src codacy-duplication-jscpd
```

## Test

For a faster development loop you can create a Docker image based on the JVM instead of creating a native-image:

```bash
sbt universal:stage
docker build -t codacy-duplication-jscpd --target dev .
```

We use the [codacy-plugins-test](https://github.com/codacy/codacy-plugins-test) to test our external tools integration.
You can follow the instructions there to make sure your tool is working as expected.

## What is Codacy?

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features:

- Identify new Static Analysis issues
- Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
- Auto-comments on Commits and Pull Requests
- Integrations with Slack, HipChat, Jira, YouTrack
- Track issues in Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

### Free for Open Source

Codacy is free for Open Source projects.
test
