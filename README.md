# Console trader is a console app to automate your daily trading tasks

The goal of this project is to automate common trading tasks by running  a bunch of trading scripts. The project is fully transparent and no sensitive api keys are stored. The code is open sourced and everyone is welcome to contribute. Every trading task is encapsulated in one command line (for future scripting).

## Exchange support
For now, Console trader supports Binance and Bitfinex. Supporting more exchanges is possible as project uses XChange wrapper to more than 60 cryptocurrency exchanges.
If you want to add another exchange, create PR and start from expanding ExchangeMatcher class.

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
-exchange:EXCHANGE_NAME -key:YOUR_EXCHANGE_KEY -secret:YOUR_EXCHANGE_SECRET -task:wallet
```

##### Example:
```
-exchange:bitfinex -key:XxxxxXxxxxX -secret:XxxxxXxxxxX -task:wallet
```

Sample output:
```
LTC: 0,00031897 = 0,08$
ETH: 0,00078940 = 0,91$
XLM: 0,93800000 = 0,55$
DONE
```

### Buying
```
-exchange:EXCHANGE_NAME -key:YOUR_EXCHANGE_KEY -secret:YOUR_EXCHANGE_SECRET -task:marketbuy(pair|amount)
```

##### Example:

```
   -exchange:bitfinex -key:XxxxxXxxxxX -secret:XxxxxXxxxxX -task:marketbuy(XRP/USD|10XRP)
```
Places market order to buy 10XRP

```
   -exchange:bitfinex -key:XxxxxXxxxxX -secret:XxxxxXxxxxX -task:marketbuy(XRP/USD|100USD)
```
Places market order to buy ripple for about 100USD (the amount of XRP is calculated from market price in the moment)

### Watching RSI
```
   -exchange:bitfinex -key:XxxxxXxxxxX -secret:XxxxxXxxxxX -task:watchrsibelow(pair|rsi)
   -exchange:bitfinex -key:XxxxxXxxxxX -secret:XxxxxXxxxxX -task:watchrsiabove(pair|rsi)
```
##### Example 1:
```
   -exchange:bitfinex -key:XxxxxXxxxxX -secret:XxxxxXxxxxX -task:watchrsibelow(XRP/USD|30)
```
Watches RSI of XRP/USD pair and completes when RSI is below 30. Example output:
```
2018-01-15 10:07:11: RSI: 55.41
2018-01-15 10:12:11: RSI: 45.30
2018-01-15 10:17:11: RSI: 35.40
TASK COMPLETED
```

##### Example 2:
```
   -exchange:bitfinex -key:XxxxxXxxxxX -secret:XxxxxXxxxxX -task:watchrsiabove(XRP/USD|70)
```
Watches RSI of XRP/USD pair and completes when RSI is above 70. Example output:
```
2018-01-15 10:07:11: RSI: 45.40
2018-01-15 10:12:11: RSI: 55.30
2018-01-15 10:17:11: RSI: 65.41
TASK COMPLETED
```

## Push messaging configuration ##
If you want to have push messaging you need to configure account on https://pushover.net/. This website allow to sending pushes for 7 days free, after that you need to buy premium for 5 usd(single payment, any subscription) or create a new account
1. Create account on https://pushover.net/
2. Install pushover Android or iOS app and login into this app
3. Create application project https://pushover.net/apps/build (you can put random values)
4. Now you can use your userId and apiKey in Console Trader app