package bot.exchange.kraken.account;

import  bot.exchange.kraken.account.KrakenAccountDomain.*;
import  bot.exchange.kraken.account.KrakenAccountAPI.*;
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
public class KrakenAccountEntity {

    public static final String ACCOUNT_IS_ALREADY_REGISTERED = "Account is already registered on this Exchange";
    public static final String ACCOUNT_IS_ALREADY_DISSOCIATED = "Account is already dissociated";

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String accountId;

    private boolean isAdded = false;

    private boolean isDissociated = false;

    /**
     * Constructor.
     *
     * @param accountId The entity id will be the accountId, the unique key for this entity.
     */
    public KrakenAccountEntity(@EntityId String accountId) {
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
    @SuppressWarnings("ThrowableNotThrown")
    @CommandHandler
    public Empty associateAccount(AssociateAccountCommand associateAccountCommand, CommandContext ctx) {
        if (isAdded) {
            ctx.fail(ACCOUNT_IS_ALREADY_REGISTERED);
        } else {
            KrakenAccountAssociated event = KrakenAccountAssociated.newBuilder()
                    .setBotAccountId(associateAccountCommand.getBotAccountId())
                    .build();

            ctx.emit(event);
        }

        return Empty.getDefaultInstance();
    }

    @SuppressWarnings("ThrowableNotThrown")
    @CommandHandler
    public Empty dissociateAccount(DissociateAccountCommand dissociateAccountCommand, CommandContext ctx) {
        if (isDissociated) {
            ctx.fail(ACCOUNT_IS_ALREADY_DISSOCIATED);
        } else {
            KrakenAccountDissociated event = KrakenAccountDissociated.newBuilder()
                    .setBotAccountId(dissociateAccountCommand.getBotAccountId())
                    .build();

            ctx.emit(event);
        }

        return Empty.getDefaultInstance();
    }

    // EVENTS

    /**
     * This is the event handler for associating a Kraken Account with a Bot Account.
     * It is here we update current state due to successful storage to the Event Log.
     *
     * @param krakenAccountAssociated the event previously emitted in the command handler, now safely stored.
     */
    @EventHandler
    public void accountAssociated(@SuppressWarnings("unused") KrakenAccountAssociated krakenAccountAssociated) {
        this.isAdded = true;
    }

    /**
     * This is the event handler for dissociating a Kraken Account with a Bot Account.
     * It is here we update current state due to successful storage to the Event Log.
     *
     * @param krakenAccountDissociated the event previously emitted in the command handler, now safely stored.
     */
    @EventHandler
    public void accountDissociated(@SuppressWarnings("unused") KrakenAccountDissociated krakenAccountDissociated) {
        this.isDissociated = true;
    }

}
