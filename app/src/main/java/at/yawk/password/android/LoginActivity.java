package at.yawk.password.android;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.lambdaworks.crypto.SCrypt;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
public class LoginActivity extends FragmentActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        toolbar.setBackgroundResource(R.color.toolbarColorNormal);
        toolbar.setTitle("Login");

        try {
            SCrypt.scryptN(new byte[1], new byte[1], 1, 1, 1, 1);
            log.info("Native scrypt available");
        } catch (Throwable t) {
            log.warn("Native scrypt not available", t);
        }
    }
}
