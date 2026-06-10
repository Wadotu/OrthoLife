package wadotu.orthodoxy.ortholife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import wadotu.orthodoxy.ortholife.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCalendar();

        Button btn_holybible = (Button) findViewById(R.id.btn_holybible);

        btn_holybible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lang = LocaleHelper.getLanguage(MainActivity.this);
                if ("ko".equals(lang)) {
                    Intent intent = new Intent(MainActivity.this, BookListActivity.class);
                    startActivity(intent);
                } else {
                    String url;
                    switch (lang) {
                        case "el":
                            url = "https://www.saint.gr/bible.aspx";
                            break;
                        case "cu":
                            url = "https://smartbible.app/ru/bible/syno";
                            break;
                        default:
                            url = "https://www.orthodoxbibleonline.com/";
                            break;
                    }
                    Intent intent = new Intent(MainActivity.this, BibleWebViewActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            }
        });

        Button btn_kombo = (Button) findViewById(R.id.btn_kombo);

        btn_kombo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KomboskiniActivity.class);
                startActivity(intent);
            }
        });

        Button btn_prayers = (Button) findViewById(R.id.btn_prayers);

        btn_prayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Prayers.class);
                startActivity(intent);
            }
        });

        Button btn_icon = (Button) findViewById(R.id.icon);

        btn_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Icon.class);
                startActivity(intent);
            }
        });

        Button btn_setting = (Button) findViewById(R.id.btn_setting);

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AppSetting.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.calendarContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailedCalendarActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setupCalendar() {
        OrthoCalendar.getCalendarInfo(this, new OrthoCalendar.CalendarCallback() {
            @Override
            public void onDataLoaded(OrthoCalendar.CalendarInfo info) {
                TextView tvDate = findViewById(R.id.tv_date);
                TextView tvFeast = findViewById(R.id.tv_feast);
                TextView tvTone = findViewById(R.id.tv_tone);
                TextView tvFasting = findViewById(R.id.tv_fasting);
                TextView tvVerse = findViewById(R.id.tv_daily_verse);
                ImageView ivIcon = findViewById(R.id.iv_main_icon);

                tvDate.setText(info.date);
                tvFeast.setText(info.feast);
                
                if (info.tone != null && !info.tone.isEmpty()) {
                    tvTone.setText(info.tone);
                    tvTone.setVisibility(View.VISIBLE);
                } else {
                    tvTone.setVisibility(View.GONE);
                }

                tvFasting.setText(info.fasting);
                tvVerse.setText(info.dailyVerse);
            }

            @Override
            public void onError(String message) {
                // Fallback or error message
                TextView tvFeast = findViewById(R.id.tv_feast);
                tvFeast.setText(message);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCalendar(); // Refresh calendar info when returning to main screen
    }

}
