package in.mytrain.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends Activity {
    private static final String NAVY = "#12304A";
    private static final String BLUE = "#1769AA";
    private static final String BG = "#F4F7FA";
    private EditText trainInput;

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
        page.addView(text("Find train information and open official railway enquiries.", 15, "#5C6B78", false));

        LinearLayout searchCard = card(page);
        searchCard.addView(text("TRAIN NUMBER OR NAME", 12, "#536779", true));
        trainInput = new EditText(this);
        trainInput.setSingleLine(true);
        trainInput.setTextSize(17);
        trainInput.setHint("e.g. 12565 or Bihar Sampark Kranti");
        trainInput.setHintTextColor(Color.parseColor("#8997A3"));
        trainInput.setPadding(dp(12), dp(10), dp(12), dp(10));
        trainInput.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(-1, dp(54));
        inputParams.topMargin = dp(10);
        searchCard.addView(trainInput, inputParams);
        Button status = button("Check train status");
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, dp(50));
        statusParams.topMargin = dp(12);
        searchCard.addView(status, statusParams);
        status.setOnClickListener(v -> openNtes());

        page.addView(sectionTitle("Quick enquiries"));
        addActionCard(page, "Live train status", "Open official NTES train enquiry", "https://enquiry.indianrail.gov.in/mntes/");
        addActionCard(page, "PNR status", "Open Indian Railways enquiry services", "https://www.indianrail.gov.in/");
        addActionCard(page, "Train schedules", "Look up route and scheduled stops", "https://enquiry.indianrail.gov.in/mntes/");
        TextView note = text("Live results inside My Train need a train-data API. Until one is configured, this app opens official railway enquiry pages and does not show invented train locations.", 13, "#657583", false);
        note.setPadding(dp(2), dp(16), dp(2), 0);
        page.addView(note);
        TextView footer = text("Independent project · Not affiliated with Indian Railways or Google", 11, "#84919B", false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(22), 0, 0);
        page.addView(footer);
        setContentView(scroll);
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
        Button open = button("Open enquiry");
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

    private void openNtes() {
        String query = trainInput.getText().toString().trim();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(trainInput.getWindowToken(), 0);
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter a train number or name first", Toast.LENGTH_SHORT).show();
            trainInput.requestFocus();
            return;
        }
        openUrl("https://enquiry.indianrail.gov.in/mntes/");
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
}
