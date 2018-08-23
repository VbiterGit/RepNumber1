package mypackage.taskjson;

/**
 * Активити о программе
 *
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView tvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvAbout = (TextView) findViewById(R.id.tvAbout);
        tvAbout.setText((char)169 + BuildConfig.APPLICATION_ID + " " + BuildConfig.VERSION_NAME);
    }
}
