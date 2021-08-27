package bot.account;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.akkaserverless.javasdk.eventsourcedentity.CommandHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.google.protobuf.Empty;
import bot.account.AccountManagementDomain.*;
import bot.account.AccountManagementAPI.*;


/**
 * An Account entity.
 * <p>
 * As an entity, I will be seeded with my current state upon loading and thereafter will completely serve the
 * backend needs for a particular device. There is no practical limit to how many of these entities you can have,
 * across the application cluster. Look at each instance of this entity as being roughly equivalent to a row in a
 * database, only each one is completely addressable and in memory.
 * <p>
 * Event sourcing was selected in order to have complete traceability into the behavior of accounts for the purposes
 * of security, analytics and simulation.
 */
@EventSourcedEntity(entityType = "account")
public class AccountEntity {

    public static final String ACCOUNT_IS_ALREADY_REGISTERED = "Account is already registered";
    public static final String ACCOUNT_EMAIL_IS_NOT_A_VALID_EMAIL = "Account Email is not a valid email";
    /**
     * This section contains the private state variables necessary for this entity.
     */

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String accountId;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String email = "";

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private boolean isAdded = false;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private boolean isDisabled = false;

    /**
     * Constructor.
     *
     * @param accountId The entity id will be the accountId, the unique key for this entity.
     */
    public AccountEntity(@EntityId String accountId) {
        this.accountId = accountId;
    }

    // COMMANDS

    /**
     * This is the command handler for adding an account as defined in protobuf.
     *
     * @param registerAccountCommand the command message from protobuf
     * @param ctx                    the application context
     * @return Empty (unused)
     */
    @SuppressWarnings("UnusedReturnValue")
    @CommandHandler
    public Empty registerAccount(RegisterAccountCommand registerAccountCommand, CommandContext ctx) {
        if (isAdded) {
            //noinspection ThrowableNotThrown
            ctx.fail(ACCOUNT_IS_ALREADY_REGISTERED);
        } else if (!isValidEmail(registerAccountCommand.getEmail())) {
            //noinspection ThrowableNotThrown
            ctx.fail(ACCOUNT_EMAIL_IS_NOT_A_VALID_EMAIL);
        } else {
            AccountRegistered event = AccountRegistered.newBuilder()
                    .setId(AccountId.newBuilder().setId(registerAccountCommand.getAccountId()).build())
                    .setEmail(registerAccountCommand.getEmail())
                    .build();

            ctx.emit(event);
        }

        return Empty.getDefaultInstance();
    }

    private boolean isValidEmail(String id) {

        String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        return id.matches(EMAIL_REGEX);
    }

    // EVENTS

    /**
     * This is the event handler for registering an Account.
     * It is here we update current state due to successful storage to the Event Log.
     *
     * @param accountRegistered the event previously emitted in the command handler, now safely stored.
     */
    @EventHandler
    public void accountRegistered(AccountRegistered accountRegistered) {
        this.isAdded = true;
        this.isDisabled = false;
        this.email = accountRegistered.getEmail();
    }
}
