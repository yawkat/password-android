package at.yawk.password.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PasswordActivity extends FragmentActivity {
    private PasswordDatabase database;

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = ApplicationState.getDatabase(this);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);

        toolbar.setBackgroundResource(
                database.isLocalStorage() ?
                        R.color.toolbarColorWarning :
                        R.color.toolbarColorNormal
        );
        toolbar.setTitle("Passwords");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.clear();
    }
}
