# Console trader is a console app to automate your daily trading tasks

The goal of this project is to automate common trading tasks by running  a bunch of trading scripts. The project is fully transparent and no sensitive api keys are stored. The code is open sourced and everyone is welcome to contribute. Every trading task is encapsulated in one command line (for future scripting).

## Exchange support
For now, Console trader supports Binance and Bitfinex. Supporting more markets is possible as project uses 

## Project requirements
1. Git
2. IntelliJ IDEA (or other java environment supporting maven projects format. I'm assuming IntelliJ IDEA is used in the rest of this document)

## Getting started (for developers)
1. Open IntelliJ IDEA and open pom.xml from this repository as new project
2. Add new run configuration of "Application" type. Set org.consoletrader.MainKt as main class.
3. Compile and use

## Examples (program arguments)

### Listing assets (wallet)
Syntax:
```
-exchange:EXCHANGE_NAME -key:YOUR_BINANCE_KEY -secret:YOUR_BINANCE_SECRET -task:wallet
```

Examples:
```
-exchange:bitfinex -key:XxxxxXxxxxX -secret:XxxxxXxxxxX -task:wallet
-exchange:binance -key:XxxxxXxxxxX -secret:XxxxxXxxxxX -task:wallet
```

Sample output:
```
LTC: 0,00031897 = 0,08$
ETH: 0,00078940 = 0,91$
XLM: 0,93800000 = 0,55$
DONE
```
