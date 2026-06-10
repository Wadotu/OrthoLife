package wadotu.orthodoxy.ortholife;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AppSetting extends AppCompatActivity {

    private SharedPreferences prefs;
    private static final String PREF_TEXT_SIZE = "text_size_mode"; // 0: Small, 1: Medium, 2: Large
    private static final String PREF_CALENDAR = "calendar_mode"; // 0: Revised Julian, 1: Julian

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);

        // --- Language Button Logic ---
        Button langButton = findViewById(R.id.btn_lang);
        updateLangButton(langButton);
        langButton.setOnClickListener(v -> {
            String current = LocaleHelper.getLanguage(this);
            String next;
            switch (current) {
                case "en":
                    next = "el";
                    break;
                case "el":
                    next = "ko";
                    break;
                case "ko":
                    next = "cu";
                    break;
                default:
                    next = "en";
                    break;
            }
            LocaleHelper.setLocale(this, next);
            // updateLangButton(langButton); // setApplicationLocales will trigger activity recreation
        });

        // --- Calendar Button Logic ---
        Button calButton = findViewById(R.id.btn_chngcal);
        updateCalButton(calButton);
        calButton.setOnClickListener(v -> {
            int current = prefs.getInt(PREF_CALENDAR, 0);
            int next = (current + 1) % 2;
            prefs.edit().putInt(PREF_CALENDAR, next).apply();
            updateCalButton(calButton);
        });
    }

    private void updateLangButton(Button btn) {
        String langCode = LocaleHelper.getLanguage(this);
        String langName;
        switch (langCode) {
            case "el": langName = "Ελληνικά"; break;
            case "ko": langName = "한국어"; break;
            case "cu": langName = "Церковнославянский"; break;
            default: langName = "English"; break;
        }
        btn.setText(getString(R.string.language, langName));
    }

    private void updateCalButton(Button btn) {
        int mode = prefs.getInt(PREF_CALENDAR, 0);
        String calName = (mode == 0) ? getString(R.string.revised_julian) : getString(R.string.julian);
        String date = (mode == 0) ? "12/25" : "1/7";
        String calendarLabel = getString(R.string.calendar, calName);
        String christmasLabel = getString(R.string.christmas_day, date);
        btn.setText(calendarLabel + christmasLabel);
    }
}
