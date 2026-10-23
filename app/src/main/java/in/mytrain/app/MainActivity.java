package in.mytrain.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String NAVY = "#12304A";
    private static final String BLUE = "#1769AA";
    private static final String BG = "#F4F7FA";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private EditText trainInput, backendInput;
    private TextView resultView;
    private Button statusButton;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor(NAVY));
        getWindow().setNavigationBarColor(Color.parseColor(NAVY));
        buildScreen();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.parseColor(BG));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(22), dp(20), dp(24));
        scroll.addView(page);

        TextView brand = text("MY TRAIN", 13, BLUE, true);
        brand.setLetterSpacing(0.12f);
        page.addView(brand);
        TextView title = text("Your journey,\nsimplified.", 30, NAVY, true);
        title.setPadding(0, dp(12), 0, dp(8));
        page.addView(title);
        page.addView(text("Train enquiries with live status through your configured data provider.", 15, "#5C6B78", false));

        LinearLayout searchCard = card(page);
        searchCard.addView(text("TRAIN NUMBER", 12, "#536779", true));
        trainInput = new EditText(this);
        trainInput.setSingleLine(true);
        trainInput.setTextSize(17);
        trainInput.setHint("Five-digit train number, e.g. 12565");
        trainInput.setInputType(2);
        trainInput.setPadding(dp(12), dp(10), dp(12), dp(10));
        trainInput.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(-1, dp(54));
        inputParams.topMargin = dp(10);
        searchCard.addView(trainInput, inputParams);

        searchCard.addView(text("YOUR API BACKEND HTTPS URL", 12, "#536779", true));
        backendInput = new EditText(this);
        backendInput.setSingleLine(true);
        backendInput.setTextSize(14);
        backendInput.setHint("https://your-service.example");
        backendInput.setInputType( UriInputType() );
        backendInput.setPadding(dp(12), dp(10), dp(12), dp(10));
        backendInput.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams backendParams = new LinearLayout.LayoutParams(-1, dp(50));
        backendParams.topMargin = dp(8);
        searchCard.addView(backendInput, backendParams);
        SharedPreferences prefs = getSharedPreferences("my_train", MODE_PRIVATE);
        backendInput.setText(prefs.getString("backend_url", ""));

        statusButton = button("Check live train status");
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, dp(50));
        statusParams.topMargin = dp(12);
        searchCard.addView(statusButton, statusParams);
        statusButton.setOnClickListener(v -> fetchLiveStatus());

        resultView = text("Live results will appear here after you deploy the backend and configure its URL.", 14, "#526675", false);
        resultView.setPadding(dp(2), dp(16), dp(2), dp(4));
        searchCard.addView(resultView);

        page.addView(sectionTitle("Official enquiries"));
        addActionCard(page, "NTES live train status", "Verify reported running information on the official enquiry site", "https://enquiry.indianrail.gov.in/mntes/");
        addActionCard(page, "Indian Railways", "Official railway information and passenger services", "https://www.indianrail.gov.in/");

        TextView note = text("My Train uses an unofficial third-party data provider. Reported position can be delayed and is not guaranteed GPS tracking. Verify important details with official railway sources.", 13, "#657583", false);
        note.setPadding(dp(2), dp(16), dp(2), 0);
        page.addView(note);
        TextView footer = text("Independent project · Not affiliated with Indian Railways or Google", 11, "#84919B", false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(22), 0, 0);
        page.addView(footer);
        setContentView(scroll);
    }

    private int UriInputType() {
        return android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI;
    }

    private void fetchLiveStatus() {
        String train = trainInput.getText().toString().trim();
        String base = backendInput.getText().toString().trim().replaceAll("/+$", "");
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(trainInput.getWindowToken(), 0);
        if (!train.matches("[0-9]{5}")) {
            Toast.makeText(this, "Enter a five-digit train number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!base.startsWith("https://")) {
            Toast.makeText(this, "Enter your deployed HTTPS backend URL first", Toast.LENGTH_LONG).show();
            return;
        }
        getSharedPreferences("my_train", MODE_PRIVATE).edit().putString("backend_url", base).apply();
        statusButton.setEnabled(false);
        statusButton.setText("Checking...");
        resultView.setText("Requesting the latest available provider report...");
        executor.execute(() -> {
            String message;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(base + "/api/live/" + train).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("Accept", "application/json");
                int code = connection.getResponseCode();
                InputStream stream = code >= 200 && code < 400 ? connection.getInputStream() : connection.getErrorStream();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int count;
                while (stream != null && (count = stream.read(buffer)) != -1) output.write(buffer, 0, count);
                if (stream != null) stream.close();
                connection.disconnect();
                String body = output.toString(StandardCharsets.UTF_8.name());
                if (code >= 200 && code < 300) {
                    JSONObject json = new JSONObject(body);
                    message = json.toString(2);
                    if (json.has("Message")) message = json.optString("Message") + "\n\n" + message;
                    else if (json.has("error")) message = json.optString("error") + "\n\n" + message;
                } else {
                    message = "Request failed (HTTP " + code + ").\n" + body;
                }
            } catch (Exception e) {
                message = "Could not get train status. Check your backend URL, internet connection, and API configuration.\n" + e.getMessage();
            }
            final String display = message;
            runOnUiThread(() -> {
                statusButton.setEnabled(true);
                statusButton.setText("Check live train status");
                resultView.setText(display);
            });
        });
    }

    private LinearLayout card(LinearLayout parent) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(16), dp(16), dp(16));
        box.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(22);
        parent.addView(box, p);
        return box;
    }

    private void addActionCard(LinearLayout parent, String heading, String subtitle, String url) {
        LinearLayout box = card(parent);
        box.addView(text(heading, 17, NAVY, true));
        TextView s = text(subtitle, 13, "#657583", false);
        s.setPadding(0, dp(5), 0, dp(12));
        box.addView(s);
        Button open = button("Open official site");
        box.addView(open, new LinearLayout.LayoutParams(-1, dp(44)));
        open.setOnClickListener(v -> openUrl(url));
    }

    private TextView sectionTitle(String value) {
        TextView t = text(value, 20, NAVY, true);
        t.setPadding(0, dp(26), 0, 0);
        return t;
    }

    private TextView text(String value, int size, String color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.parseColor(color));
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button button(String value) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(BLUE)));
        return b;
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Could not open the enquiry page", Toast.LENGTH_LONG).show();
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
