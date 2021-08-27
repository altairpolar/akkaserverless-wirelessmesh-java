package bot.exchange.kraken.account;

import  bot.exchange.kraken.account.ExchangeAccountKrakenDomain.*;
import  bot.exchange.kraken.account.ExchangeAccountKrakenAPI.*;
import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.akkaserverless.javasdk.eventsourcedentity.CommandHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.google.protobuf.Empty;

/**
 * A Kraken Exchange Account entity.
 * <p>
 * As an entity, I will be seeded with my current state upon loading and thereafter will completely serve the
 * backend needs for a particular device. There is no practical limit to how many of these entities you can have,
 * across the application cluster. Look at each instance of this entity as being roughly equivalent to a row in a
 * database, only each one is completely addressable and in memory.
 * <p>
 * Event sourcing was selected in order to have complete traceability into the behavior of accounts for the purposes
 * of security, analytics and simulation.
 */
@EventSourcedEntity(entityType = "exchange-kraken-account")
public class KrakenExchangeAccountEntity {

    public static final String ACCOUNT_IS_ALREADY_REGISTERED = "Account is already registered on this Exchange";

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String exchangeId;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String accountId;

    private boolean isAdded = false;

    /**
     * Constructor.
     *
     * @param accountId The entity id will be the accountId, the unique key for this entity.
     */
    public KrakenExchangeAccountEntity(@EntityId String exchangeId, @EntityId String accountId) {
        this.exchangeId = exchangeId;
        this.accountId = accountId;
    }


    // COMMANDS

    /**
     * This is the command handler for adding an account as defined in protobuf.
     *
     * @param associateAccountCommand the command message from protobuf
     * @param ctx                    the application context
     * @return Empty (unused)
     */
    @CommandHandler
    public Empty associateAccount(ExchangeAccountKrakenAPI.AssociateAccountCommand associateAccountCommand, CommandContext ctx) {
        if (isAdded) {
            ctx.fail(ACCOUNT_IS_ALREADY_REGISTERED);
        } else {
            KrakenAccountAssociated event = KrakenAccountAssociated.newBuilder()
                    .setExchangeId(associateAccountCommand.getExchangeId())
                    .setAccountId(associateAccountCommand.getAccountId())
                    .build();

            ctx.emit(event);
        }

        return Empty.getDefaultInstance();
    }

    // EVENTS

    /**
     * This is the event handler for registering an Account.
     * It is here we update current state due to successful storage to the Event Log.
     *
     * @param accountRegistered the event previously emitted in the command handler, now safely stored.
     */
    @EventHandler
    public void accountRegistered(KrakenAccountAssociated accountRegistered) {
        this.isAdded = true;
    }

}
