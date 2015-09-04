package at.yawk.password.android;

import at.yawk.password.LocalStorageProvider;
import at.yawk.password.client.ClientValue;
import at.yawk.password.client.PasswordClient;
import at.yawk.password.model.PasswordBlob;
import at.yawk.password.model.PasswordEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Value;

/**
 * @author yawkat
 */
public class PasswordDatabase {
    private final LocalStorageProvider storageProvider;

    @Nullable private Holder holder = null;

    public PasswordDatabase(LocalStorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

    public synchronized void unlock(String host, byte[] password) throws Exception {
        PasswordClient client = new PasswordClient(host, storageProvider, password);
        ClientValue<PasswordBlob> blob = client.load();
        if (blob.getValue() == null) { throw new IOException("No database"); }
        this.holder = new Holder(client, blob.getValue(), blob.isFromLocalStorage());
    }

    public List<String> getKeys() {
        Holder holder = this.holder;
        if (holder == null) { throw new IllegalStateException(); }
        List<String> keys = new ArrayList<>(holder.getBlob().getPasswords().size());
        for (PasswordEntry entry : holder.getBlob().getPasswords()) {
            keys.add(entry.getName());
        }
        return keys;
    }

    @Nullable
    public PasswordEntry findEntry(String key) {
        Holder holder = this.holder;
        if (holder == null) { throw new IllegalStateException(); }
        for (PasswordEntry entry : holder.getBlob().getPasswords()) {
            if (entry.getName().equals(key)) {
                return entry;
            }
        }
        return null;
    }

    public void save() throws Exception {
        Holder holder = this.holder;
        if (holder == null) { throw new IllegalStateException(); }
        // todo: warning if from local storage
        holder.getClient().save(holder.getBlob());
    }

    public void addAndSave(PasswordEntry entry) throws Exception {
        Holder holder = this.holder;
        if (holder == null) { throw new IllegalStateException(); }
        holder.getBlob().getPasswords().add(entry);
        save();
    }

    public void clear() {
        holder = null;
    }

    public boolean isLocalStorage() {
        Holder holder = this.holder;
        if (holder == null) { throw new IllegalStateException(); }
        return holder.isLocalStorage();
    }

    @Value
    private static final class Holder {
        private final PasswordClient client;
        private final PasswordBlob blob;
        private final boolean localStorage;
    }
}
