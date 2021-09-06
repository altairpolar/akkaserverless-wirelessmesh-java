package bot.exchange.kraken.access;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.kraken.service.KrakenAccountServiceRaw;
import org.knowm.xchange.kraken.service.KrakenTradeServiceRaw;

public class BotKrakenExchangeFactory {

    static public KrakenAccountServiceRaw createKrakenAccountServiceRaw(Exchange exchange) {
        return new KrakenAccountServiceRaw(exchange);
    }
}
