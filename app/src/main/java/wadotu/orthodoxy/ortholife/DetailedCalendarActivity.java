package wadotu.orthodoxy.ortholife;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailedCalendarActivity extends AppCompatActivity {

    private Calendar currentViewDate = Calendar.getInstance();
    private String apiMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_calendar);

        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int calendarMode = prefs.getInt("calendar_mode", 0);
        apiMode = (calendarMode == 0) ? "gregorian" : "julian";

        setupNavigation();
        loadData();
    }

    private void setupNavigation() {
        findViewById(R.id.btn_prev_day).setOnClickListener(v -> {
            currentViewDate.add(Calendar.DAY_OF_YEAR, -1);
            loadData();
        });

        findViewById(R.id.btn_next_day).setOnClickListener(v -> {
            currentViewDate.add(Calendar.DAY_OF_YEAR, 1);
            loadData();
        });

        findViewById(R.id.date_picker_trigger).setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                currentViewDate.set(Calendar.YEAR, year);
                currentViewDate.set(Calendar.MONTH, month);
                currentViewDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                loadData();
            }, currentViewDate.get(Calendar.YEAR), currentViewDate.get(Calendar.MONTH), currentViewDate.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void loadData() {
        int y = currentViewDate.get(Calendar.YEAR);
        int m = currentViewDate.get(Calendar.MONTH) + 1;
        int d = currentViewDate.get(Calendar.DAY_OF_MONTH);

        OrthoCalendar.getFullCalendarDay(this, apiMode, y, m, d, new OrthoCalendar.FullCalendarCallback() {
            @Override
            public void onDataLoaded(OrthoDay day) {
                updateUI(day);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(DetailedCalendarActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI(OrthoDay day) {
        TextView tvDate = findViewById(R.id.tv_det_date);
        TextView tvFeast = findViewById(R.id.tv_det_feast);
        TextView tvTone = findViewById(R.id.tv_det_tone);
        TextView tvFasting = findViewById(R.id.tv_det_fasting);
        TextView tvCommemorations = findViewById(R.id.tv_det_commemorations);
        TextView tvReadings = findViewById(R.id.tv_det_readings);
        TextView tvStories = findViewById(R.id.tv_det_stories);

        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
        tvDate.setText(sdf.format(currentViewDate.getTime()));

        // --- [Feast Selection Logic] ---
        String bestTitle = "";
        if (day.feasts != null && day.feasts.length > 0) {
            for (String f : day.feasts) {
                String translated = OrthoCalendar.translateApiTitle(this, f);
                if (translated != null && !translated.isEmpty()) {
                    bestTitle = translated;
                    break;
                }
            }
        }
        if (bestTitle.isEmpty() && day.titles != null) {
            for (String t : day.titles) {
                String translated = OrthoCalendar.translateApiTitle(this, t);
                if (translated != null && !translated.isEmpty()) {
                    bestTitle = translated;
                    break;
                }
            }
        }
        if (bestTitle.isEmpty()) bestTitle = getString(R.string.no_feast);
        
        if (day.feast_level >= 7 && day.feast_level_description != null && !day.feast_level_description.isEmpty()) {
            tvFeast.setText(bestTitle + " (" + day.feast_level_description + ")");
        } else {
            tvFeast.setText(bestTitle);
        }

        if (day.tone > 0) {
            tvTone.setText(getString(R.string.tone, day.tone));
            tvTone.setVisibility(View.VISIBLE);
        } else {
            tvTone.setVisibility(View.GONE);
        }

        // --- [Fasting Localization] ---
        tvFasting.setText(OrthoCalendar.translateFasting(this, day));

        if (day.titles != null) {
            StringBuilder sb = new StringBuilder();
            for (String title : day.titles) {
                String translated = OrthoCalendar.translateApiTitle(this, title);
                if (translated != null && !translated.isEmpty()) {
                    sb.append("• ").append(translated).append("\n\n");
                }
            }
            tvCommemorations.setText(sb.toString().trim());
        }

        if (day.readings != null) {
            // ✅ 복음경 → 사도경 → 사도행전 우선순위 정렬
            List<OrthoDay.Reading> sortedReadings = new java.util.ArrayList<>(day.readings);
            List<String> priority = Arrays.asList("epistle", "apostle", "acts", "gospel");
            java.util.Collections.sort(sortedReadings, (a, b) -> {
                String da = a.description != null ? a.description.toLowerCase(Locale.ENGLISH) : "";
                String db = b.description != null ? b.description.toLowerCase(Locale.ENGLISH) : "";
                int ia = Integer.MAX_VALUE, ib = Integer.MAX_VALUE;
                for (int i = 0; i < priority.size(); i++) {
                    if (da.contains(priority.get(i))) { ia = i; break; }
                }
                for (int i = 0; i < priority.size(); i++) {
                    if (db.contains(priority.get(i))) { ib = i; break; }
                }
                return Integer.compare(ia, ib);
            });

            StringBuilder sb = new StringBuilder();
            String currentLang = LocaleHelper.getLanguage(this);
            for (OrthoDay.Reading reading : sortedReadings) {
                String sourceLabel = reading.source;
                if (sourceLabel != null) {
                    String lowerSource = sourceLabel.toLowerCase(Locale.ENGLISH);
                    if (lowerSource.contains("gospel"))                                sourceLabel = getString(R.string.gospel);
                    else if (lowerSource.contains("acts"))                             sourceLabel = getString(R.string.acts);
                    else if (lowerSource.contains("epistle") || lowerSource.contains("apostle")) sourceLabel = getString(R.string.apostles);
                }
                String display = reading.display;
                if ("ko".equals(currentLang)) display = OrthoCalendar.translateReadingToKorean(display);
                sb.append("<b>").append(sourceLabel).append(":</b> ").append(display).append("\n\n");
            }
            tvReadings.setText(android.text.Html.fromHtml(sb.toString().trim(), android.text.Html.FROM_HTML_MODE_COMPACT));
        }

        if (day.stories != null) {
            StringBuilder sb = new StringBuilder();
            for (OrthoDay.Story storyObj : day.stories) {
                String translatedStoryTitle = OrthoCalendar.translateApiTitle(this, storyObj.title);
                if (translatedStoryTitle != null && !translatedStoryTitle.isEmpty()) {
                    sb.append("<b>").append(translatedStoryTitle).append("</b><br>");
                }
                sb.append(storyObj.story).append("<br><br>");
            }
            tvStories.setText(android.text.Html.fromHtml(sb.toString().trim(), android.text.Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvStories.setText("");
        }
    }
}
