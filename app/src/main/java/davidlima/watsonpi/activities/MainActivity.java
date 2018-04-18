package davidlima.watsonpi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import davidlima.watsonpi.http.JsonCallBack;
import davidlima.watsonpi.http.JsonPost;
import davidlima.watsonpi.http.JsonParser;
import davidlima.watsonpi.R;

public class MainActivity extends AppCompatActivity {

    public static final String JSON_RESPONSE = "davidlima.watsonpi.JSON_RESPONSE";

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null && "text/plain".equals(type)) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null) {
                editText.setText(sharedText);
            }
        }
    }

    public void analyzeClicked(final View view) {
        String userText = editText.getText().toString();
        String payload = "{\"contentItems\": [{\"content\":"+JSONObject.quote(userText)+",\"contenttype\":\"text/plain\"}]}";
        new JsonPost(new JsonCallBack() {
            @Override
            public void success(String jsonResponse) {
                Intent intent = new Intent(MainActivity.this, ResponseActivity.class);
                intent.putExtra(JSON_RESPONSE, jsonResponse);
                startActivity(intent);
            }
            @Override
            public void failed(String error) {
                Toast.makeText(view.getContext(), error, Toast.LENGTH_LONG).show();
            }
        }, "https://gateway.watsonplatform.net/personality-insights/api/v3/profile?version=2017-10-13", payload).execute();
    }
}
