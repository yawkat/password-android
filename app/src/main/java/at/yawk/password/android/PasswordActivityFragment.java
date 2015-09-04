package at.yawk.password.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PasswordActivityFragment extends Fragment {
    private static final Splitter SEARCH_SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings();

    private PasswordDatabase database;
    private ArrayAdapter<String> adapter;

    @Bind(R.id.passwords) ListView passwords;
    @Bind(R.id.search) EditText search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = ApplicationState.getDatabase(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        ButterKnife.bind(this, view);

        adapter = new ArrayAdapter<String>(getActivity(), 0, new ArrayList<>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.password_entry, parent, false);
                }
                new PasswordEntryDecorator(database, getItem(position)).decorate(convertView);
                return convertView;
            }
        };
        passwords.setAdapter(adapter);

        updateItems();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateItems();
            }
        });

        return view;
    }

    private void updateItems() {
        List<String> allKeys = database.getKeys();
        List<String> selectedKeys;

        List<String> terms = SEARCH_SPLITTER.splitToList(search.getText());
        if (!terms.isEmpty()) {
            selectedKeys = new ArrayList<>();
            for (String key : allKeys) {
                boolean accept = true;
                for (String term : terms) {
                    if (!containsIgnoreCase(key, term)) {
                        accept = false;
                        break;
                    }
                }
                if (accept) {
                    selectedKeys.add(key);
                }
            }
        } else {
            selectedKeys = allKeys;
        }

        Collections.sort(selectedKeys, String.CASE_INSENSITIVE_ORDER);

        adapter.setNotifyOnChange(false);
        adapter.clear();
        adapter.addAll(selectedKeys);
        adapter.notifyDataSetChanged();
    }

    private static boolean containsIgnoreCase(String haystack, String needle) {
        if (haystack.length() < needle.length()) { return false; }
        if (needle.isEmpty()) { return true; }

        outer:
        for (int i = 0; i <= haystack.length() - needle.length(); i++) {
            for (int j = 0; j < needle.length(); j++) {
                if (Character.toLowerCase(haystack.charAt(i + j)) !=
                    Character.toLowerCase(needle.charAt(j))) {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }
}
