package bot.exchange.kraken.access.typed;

import bot.exchange.kraken.access.typed.result.AccountBalanceResult;
import bot.exchange.kraken.access.typed.result.AssetPairsResult;
import bot.exchange.kraken.access.typed.result.AssetsInformationResult;
import bot.exchange.kraken.access.typed.result.ClosedOrdersResult;
import bot.exchange.kraken.access.typed.result.LedgersInformationResult;
import bot.exchange.kraken.access.typed.result.OHLCResult;
import bot.exchange.kraken.access.typed.result.OpenOrdersResult;
import bot.exchange.kraken.access.typed.result.OpenPositionsResult;
import bot.exchange.kraken.access.typed.result.OrderBookResult;
import bot.exchange.kraken.access.typed.result.OrdersInformationResult;
import bot.exchange.kraken.access.typed.result.RecentSpreadResult;
import bot.exchange.kraken.access.typed.result.RecentTradeResult;
import bot.exchange.kraken.access.typed.result.Result;
import bot.exchange.kraken.access.typed.result.ServerTimeResult;
import bot.exchange.kraken.access.typed.result.TickerInformationResult;
import bot.exchange.kraken.access.typed.result.TradeBalanceResult;
import bot.exchange.kraken.access.typed.result.TradeVolumeResult;
import bot.exchange.kraken.access.typed.result.TradesHistoryResult;
import bot.exchange.kraken.access.typed.result.TradesInformationResult;

/**
 * HttpApiClient factory
 *
 * @author Stéphane Bouclier
 */
public class HttpApiClientFactory {

    @SuppressWarnings("rawtypes")
    public HttpApiClient<? extends Result> getHttpApiClient(KrakenApiMethod method) {
        switch(method) {
            case SERVER_TIME:
                return new HttpApiClient<ServerTimeResult>();
            case ASSET_INFORMATION:
                return new HttpApiClient<AssetsInformationResult>();
            case ASSET_PAIRS:
                return new HttpApiClient<AssetPairsResult>();
            case TICKER_INFORMATION:
                return new HttpApiClient<TickerInformationResult>();
            case OHLC:
                return new HttpApiClient<OHLCResult>();
            case ORDER_BOOK:
                return new HttpApiClient<OrderBookResult>();
            case RECENT_TRADES:
                return new HttpApiClient<RecentTradeResult>();
            case RECENT_SPREADS:
                return new HttpApiClient<RecentSpreadResult>();
            default:
                throw new IllegalArgumentException("Unknown Kraken API method");
        }
    }

    @SuppressWarnings("rawtypes")
    public HttpApiClient<? extends Result> getHttpApiClient(String apiKey, String apiSecret, KrakenApiMethod method) {
        switch(method) {
            case ACCOUNT_BALANCE:
                return new HttpApiClient<AccountBalanceResult>(apiKey, apiSecret);
            case TRADE_BALANCE:
                return new HttpApiClient<TradeBalanceResult>(apiKey, apiSecret);
            case OPEN_ORDERS:
                return new HttpApiClient<OpenOrdersResult>(apiKey, apiSecret);
            case CLOSED_ORDERS:
                return new HttpApiClient<ClosedOrdersResult>(apiKey, apiSecret);
            case ORDERS_INFORMATION:
                return new HttpApiClient<OrdersInformationResult>(apiKey, apiSecret);
            case TRADES_HISTORY:
                return new HttpApiClient<TradesHistoryResult>(apiKey, apiSecret);
            case TRADES_INFORMATION:
                return new HttpApiClient<TradesInformationResult>(apiKey, apiSecret);
            case OPEN_POSITIONS:
                return new HttpApiClient<OpenPositionsResult>(apiKey, apiSecret);
            case LEDGERS_INFORMATION:
                return new HttpApiClient<LedgersInformationResult>(apiKey, apiSecret);
            case QUERY_LEDGERS:
                return new HttpApiClient<LedgersInformationResult>(apiKey, apiSecret);
            case TRADE_VOLUME:
                return new HttpApiClient<TradeVolumeResult>(apiKey, apiSecret);
            default:
                throw new IllegalArgumentException("Unknown Kraken API method");
        }
    }
}