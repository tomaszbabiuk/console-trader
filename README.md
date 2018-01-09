# Console trader is just simple console app for crypto currencies trading

The goal of this project is to automate common trading tasks by running  a bunch of trading scripts. The project goal is to have full transparency to its users and contributors (the code is open source and everybody can contribute).
For security reasons no API keys are stored (you need to pass it as program arguments every time). Every trading task is encapsulated in one command line (for future scripting).

## Supported markets
So far project only support Binance (as binance java api is a good start).

## Project requirements
1. Install Apache Maven
2. Install git

## Getting started
1. Clone local java-binance-api
```
git clone https://github.com/joaopsilva/binance-java-api.git
```

2. Install library into your Maven's local repository
```
cd binance-java-api
mvn install
```

3. Open pom.xml from this repository as new project (assuming you're using IntelliJ IDEA)
4. Add new run configuration of "Application" type. Set org.consoletrader.MainKt as main class.
5. Compile and use

