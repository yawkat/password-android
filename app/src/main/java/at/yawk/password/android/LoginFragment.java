package at.yawk.password.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LoginFragment extends Fragment {
    private PasswordDatabase database;

    @Bind(R.id.host) TextView host;
    @Bind(R.id.password) TextView password;
    @Bind(R.id.open) View open;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = ApplicationState.getDatabase(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        password.setText("");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String host = null;
        if (savedInstanceState != null) {
            host = savedInstanceState.getString("host", null);
        }
        if (host == null) {
            host = preferences.getString("host", null);
        }
        this.host.setText(host);

        password.requestFocus();

        open.setOnClickListener(v -> {
            preferences.edit()
                    .putString("host", this.host.getText().toString())
                    .commit();

            new ModalTask<CharSequence>(getActivity(), "Loading Database") {
                @Override
                protected void work(CharSequence... params) throws Throwable {
                    String host1 = params[0].toString();
                    CharBuffer password1 = CharBuffer.wrap(params[1]);

                    ByteBuffer buffer = StandardCharsets.UTF_8.encode(password1);
                    byte[] passwordBytes;
                    if (buffer.capacity() == buffer.limit()) {
                        passwordBytes = buffer.array();
                    } else {
                        passwordBytes = Arrays.copyOf(buffer.array(), buffer.limit());
                    }
                    database.unlock(host1, passwordBytes);
                }

                @Override
                protected void success() {
                    getActivity().startActivity(new Intent(getActivity(), PasswordActivity.class));
                }
            }.execute(this.host.getText(), password.getText());
        });

        return view;
    }
}
