package at.yawk.password.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * @author yawkat
 */
public abstract class ModalTask<Param> extends AsyncTask<Param, Void, Throwable> {
    private final Context context;
    private final String msg;
    private ProgressDialog progressDialog;

    public ModalTask(Context context, String msg) {
        this.context = context;
        this.msg = msg;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, msg, null, true);
    }

    @Override
    protected Throwable doInBackground(Param... params) {
        try {
            work(params);
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

    protected abstract void work(Param... params) throws Throwable;

    @Override
    protected void onPostExecute(Throwable throwable) {
        progressDialog.dismiss();
        if (throwable == null) {
            success();
        } else {
            Toast.makeText(context, throwable.toString(), Toast.LENGTH_LONG).show();
        }
    }

    protected void success() {}
}
