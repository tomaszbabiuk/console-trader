# Console trader is console app that to automate your daily trading tasks

The goal of this project is to automate common trading tasks by running  a bunch of trading scripts. The project is fully transparent and no sensitive api keys are stored. The code is open sourced and everyone is welcome to contribute. Every trading task is encapsulated in one command line (for future scripting).

## Markets support
For now, Console trader supports Binance only (as binance-java-api is a good start). Supporting more markets is possible.

## Project requirements
1. Apache Maven
2. Git
3. IntelliJ IDEA (or other java environment supporting maven projects format. I'm assuming IntelliJ IDEA is used in the rest of this document)

## Getting started (for developers)
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

## Examples (program arguments)

### Listing porfolio
Command:
```
-market:binance -key:YOUR_BINANCE_KEY -secret:YOUR_BINANCE_SECRET -task:portfolio
```

Sample output:
```
Connected to Binance, server time is Tue Jan 09 14:47:45 CET 2018
LTC: 0,00031897 = 0,08$
ETH: 0,00078940 = 0,91$
XLM: 0,93800000 = 0,55$
DONE
```

### Listing orders
Command:
```
-market:binance -key:YOUR_BINANCE_KEY -secret:YOUR_BINANCE_SECRET -task:orders
```

Sample output:
```
Connected to Binance, server time is Tue Jan 09 15:21:00 CET 2018
SELL XVGETH: 20.0 * 1.0, NEW, LIMIT
DONE

Process finished with exit code 0


```
