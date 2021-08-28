package common.domain;

import com.akkaserverless.javasdk.eventsourcedentity.Snapshot;
import com.akkaserverless.javasdk.eventsourcedentity.SnapshotHandler;

public interface BotDomainEntity<S>{

    /**
     * Return current state when called to snapshot.
     */
    @Snapshot
    public S snapshot();

    /**
     * Set state to the most recent snapshot.
     */
    @SnapshotHandler
    public void handleSnapshot(S state);

}
