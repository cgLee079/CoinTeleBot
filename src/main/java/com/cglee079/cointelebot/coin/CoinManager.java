package com.cglee079.cointelebot.coin;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.cglee079.cointelebot.constants.ID;
import com.cglee079.cointelebot.constants.SET;
import com.cglee079.cointelebot.exception.ServerErrorException;
import com.cglee079.cointelebot.log.Log;
import com.cglee079.cointelebot.model.TimelyInfoVo;
import com.cglee079.cointelebot.service.CoinMarketParamService;

public class CoinManager {
	@Autowired
	private CoinMarketParamService coinMarketParamService;
	
	@Autowired
	private CoinonePooler coinonePooler;

	@Autowired
	private BithumbPooler bithumbPooler;
	
	@Autowired
	private UpbitPooler upbitPooler;
	
	@Autowired
	private CoinnestPooler coinnestPooler;
	
	@Autowired
	private KorbitPooler korbitPooler;
	
	@Autowired
	private GopaxPooler gopaxPooler;
	
	@Autowired
	private BitfinexPooler bitfinexPooler;

	@Autowired
	private BittrexPooler bittrexPooler;
	
	@Autowired
	private PoloniexPooler poloniexPooler;
	
	@Autowired
	private BinancePooler binancePooler;
	
	@Autowired
	private HuobiPooler huobiPooler;
	
	@Autowired
	private HadaxPooler hadaxPooler;
	
	@Autowired
	private OkexPooler okexPooler;
	
	@Autowired
	private ExchangePooler exchangePooler;

	@PostConstruct
	public void init() {
		coinonePooler.setCoinParam(coinMarketParamService.get(ID.MARKET_COINONE));
		bithumbPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_BITHUMB));
		upbitPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_UPBIT));
		coinnestPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_COINNEST));
		korbitPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_KORBIT));
		gopaxPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_GOPAX));
		bitfinexPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_BITFINEX));
		bittrexPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_BITTREX));
		poloniexPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_POLONIEX));
		binancePooler.setCoinParam(coinMarketParamService.get(ID.MARKET_BINANCE));
		huobiPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_HUOBI));
		hadaxPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_HADAX));
		okexPooler.setCoinParam(coinMarketParamService.get(ID.MARKET_OKEX));
	}
	
	private double exchangeRate = 1068;

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	///
	@Scheduled(cron = "00 01 00 * * *")
	private void updateExchangeRate() {
		try {
			this.exchangeRate = exchangePooler.usd2krw();
		} catch (ServerErrorException e) {
			Log.i(e.log());
			e.printStackTrace();
		}
	}

	public JSONObject getCoin(String coin, String market) throws ServerErrorException{
		JSONObject coinObj = null;
		
		switch(market) {
		case ID.MARKET_COINONE 		: coinObj = coinonePooler.getCoin(coin); break;
		case ID.MARKET_BITHUMB 		: coinObj = bithumbPooler.getCoin(coin); break;
		case ID.MARKET_UPBIT 		: coinObj = upbitPooler.getCoin(coin); break;
		case ID.MARKET_COINNEST		: coinObj = coinnestPooler.getCoin(coin); break;
		case ID.MARKET_KORBIT 		: coinObj = korbitPooler.getCoin(coin); break;
		case ID.MARKET_GOPAX 		: coinObj = gopaxPooler.getCoin(coin); break;
		case ID.MARKET_BITFINEX 	: coinObj = bitfinexPooler.getCoin(coin); break;
		case ID.MARKET_BITTREX 		: coinObj = bittrexPooler.getCoin(coin); break;
		case ID.MARKET_POLONIEX 	: coinObj = poloniexPooler.getCoin(coin); break;
		case ID.MARKET_BINANCE 		: coinObj = binancePooler.getCoin(coin); break;
		case ID.MARKET_HUOBI 		: coinObj = huobiPooler.getCoin(coin); break;
		case ID.MARKET_HADAX 		: 
			if(coin.equals(ID.COIN_BTC)) { coinObj = huobiPooler.getCoin(coin); } 
			else { coinObj = hadaxPooler.getCoin(coin); }
			break;
		case ID.MARKET_OKEX 		: coinObj = okexPooler.getCoin(coin); break;
		}
		
		return coinObj;
	}
	
	public Double getCoinLast(String myCoin, String market, boolean isInBtc) {
		try {
			double last = -1;
			JSONObject coinObj = null;
			coinObj = this.getCoin(myCoin, market);
			
			last = coinObj.getDouble("last");
			if(isInBtc) {
				last = this.getMoney(coinObj, market).getDouble("last");
			}
			return last;
		} catch (ServerErrorException e) {
			Log.i(e.log());
			e.printStackTrace();
			return -1.0;
		}
	}
	
	public JSONObject getMoney(TimelyInfoVo timelyInfo, String market){
		JSONObject coinObj = new JSONObject();
		coinObj.put("last", timelyInfo.getLast());
		coinObj.put("first", 0);
		coinObj.put("high", timelyInfo.getHigh());
		coinObj.put("low", timelyInfo.getLow());
		return this.getMoney(coinObj, market);
	}
	
	public JSONObject getMoney(JSONObject coinObj, String market){
		JSONObject btcObj;
		try {
			btcObj = this.getCoin(ID.COIN_BTC, market);
		} catch (ServerErrorException e) {
			Log.i(e.log());
			e.printStackTrace();
			return null;
		}
		
		double last = coinObj.getDouble("last") * btcObj.getDouble("last");
		double first = coinObj.getDouble("first") * btcObj.getDouble("last");
		double high = coinObj.getDouble("high") * btcObj.getDouble("last");
		double low = coinObj.getDouble("low") * btcObj.getDouble("last");
		
		JSONObject coinKRW = new JSONObject();
		coinKRW.put("last", last);
		coinKRW.put("first", first);
		coinKRW.put("high", high);
		coinKRW.put("low", low);
		
		return coinKRW;
	}
}

