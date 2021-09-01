package bot.exchange.kraken.account;

import bot.account.BotAccountAPI;
import bot.exchange.kraken.account.KrakenAccountDomain;
import bot.exchange.kraken.account.KrakenAccountAPI;
import bot.account.BotAccountServiceClient;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KrakenAccountIntegrationHelper {

    private final BotAccountServiceClient botAccountServiceClient;
    private final KrakenAccountServiceClient krakenAccountServiceClient;

    public KrakenAccountIntegrationHelper(BotAccountServiceClient botAccountServiceClient, KrakenAccountServiceClient krakenAccountServiceClient) {
        this.botAccountServiceClient = botAccountServiceClient;
        this.krakenAccountServiceClient = krakenAccountServiceClient;
    }
    public void createAssociatedKrakenAccount(String krakenAccountId) throws ExecutionException, InterruptedException {

        final String botAccountId = UUID.randomUUID().toString();
        final String email = UUID.randomUUID().toString().concat("@").concat(UUID.randomUUID().toString().concat(".com"));

        createAssociatedKrakenAccount(krakenAccountId, botAccountId, email);

    }

    public void createAssociatedKrakenAccount(String krakenAccountId, String botAccountId, String email) throws ExecutionException, InterruptedException {
        botAccountServiceClient.registerAccount(BotAccountAPI.RegisterAccountCommand.newBuilder()
                .setId(botAccountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        krakenAccountServiceClient.associateAccount(KrakenAccountAPI.AssociateAccountCommand.newBuilder()
                .setBotAccountId(botAccountId)
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();
    }

    public void createAssociatedKrakenAccountWithAPIKeys(String krakenAccountId) throws ExecutionException, InterruptedException {

            createAssociatedKrakenAccount(krakenAccountId);

            krakenAccountServiceClient.assignAPIKeys(KrakenAccountAPI.AssignAPIKeysCommand.newBuilder()
                    .setKrakenAccountId(krakenAccountId)
                    .setApiKey(UUID.randomUUID().toString())
                    .setApiSecret(UUID.randomUUID().toString())
                    .build())
                    .toCompletableFuture().get();
        }

}
