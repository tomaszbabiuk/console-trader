# Console trader is just a console app that will help you automate your daily trading tasks

The goal of this project is to automate common trading tasks by running  a bunch of trading scripts. The project is fully transparent and no sensitive api keys are stored. The code is open sourced and everyone is welcome to contribute. Every trading task is encapsulated in one command line (for future scripting).

## Markets support
For now, Console trader supports Binance only (as binance-java-api is a good start). Supporting more markets is possible.

## Project requirements
1. Apache Maven
2. Git
3. IntelliJ IDEA (or other java environment supporting maven projects format. I'm assuming IntelliJ IDEA is used in the rest of this document)

## Getting started
1. Clone local java-binance-api
```
git clone https://github.com/joaopsilva/binance-java-api.git
```

2. Install binance-java-api library into your Maven's local repository
```
cd binance-java-api
mvn install
```

3. Open IntelliJ IDEA and open pom.xml from this repository as new project
4. Add new run configuration of "Application" type. Set org.consoletrader.MainKt as main class.
5. Compile and use

