package bot.exchange.access;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.kraken.service.KrakenAccountServiceRaw;
import org.knowm.xchange.kraken.service.KrakenTradeServiceRaw;

public class BotExchangeFactory {

    private Exchange exchange = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class);
    KrakenAccountServiceRaw krakenAccountServiceRaw = new KrakenAccountServiceRaw(exchange);

    static public <T extends Exchange> T createExchange(Class<T> exchangeClass) {
        return ExchangeFactory.INSTANCE.createExchange(exchangeClass);
    }
}
