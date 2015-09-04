package at.yawk.password.android;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.yawk.password.model.PasswordEntry;
import butterknife.Bind;
import butterknife.ButterKnife;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * @author yawkat
 */
@NotThreadSafe
public class PasswordEntryDecorator {
    private final PasswordDatabase database;
    private final String key;

    @Bind(R.id.entryName) TextView entryName;

    public PasswordEntryDecorator(PasswordDatabase database, String key) {
        this.database = database;
        this.key = key;
    }

    public void decorate(View view) {
        ButterKnife.bind(this, view);

        entryName.setText(key);
        view.setOnClickListener(v -> {
            PasswordEntry entry = database.findEntry(key);
            if (entry == null) {
                Toast.makeText(v.getContext(), "Entry not found", Toast.LENGTH_LONG).show();
                return;
            }
            String value = entry.getValue().split("\n")[0];
            ((ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE))
                    .setPrimaryClip(ClipData.newPlainText("Password", value));
            Toast.makeText(v.getContext(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        });
        view.setOnLongClickListener(v -> {
            PasswordEntry entry = database.findEntry(key);
            if (entry == null) {
                Toast.makeText(v.getContext(), "Entry not found", Toast.LENGTH_LONG).show();
                return true;
            }

            Dialog dialog = new Dialog(v.getContext());
            dialog.setTitle(key);
            dialog.setContentView(R.layout.password_editor);
            EditText editor = (EditText) dialog.findViewById(R.id.password_editor_text);
            editor.setText(entry.getValue());
            dialog.findViewById(R.id.save).setOnClickListener(w -> {
                entry.setValue(editor.getText().toString());
                new ModalTask<Void>(v.getContext(), "Saving Database") {
                    @Override
                    protected void work(Void... voids) throws Throwable {
                        database.save();
                    }
                }.execute();
            });
            dialog.findViewById(R.id.cancel).setOnClickListener(w -> dialog.dismiss());

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            dialog.show();
            return true;
        });
    }
}
