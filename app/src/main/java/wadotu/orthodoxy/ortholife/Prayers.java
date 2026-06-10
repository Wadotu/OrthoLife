package wadotu.orthodoxy.ortholife;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Prayers extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScrollView contentScroll;
    private TextView contentTitle, contentText;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prayers);

        recyclerView = findViewById(R.id.prayer_list);
        contentScroll = findViewById(R.id.prayer_content_scroll);
        contentTitle = findViewById(R.id.prayer_title);
        contentText = findViewById(R.id.prayer_text);
        btnBack = findViewById(R.id.btn_back_to_list);

        String lang = LocaleHelper.getLanguage(this);

        if ("ko".equals(lang)) {
            setupKoreanPrayers();
        } else {
            routeToInternationalPrayers(lang);
        }
    }

    private void setupKoreanPrayers() {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        List<PrayerItem> prayerList = loadLocalPrayers();
        recyclerView.setAdapter(new PrayerAdapter(prayerList, this::showPrayerContent));

        btnBack.setOnClickListener(v -> {
            contentScroll.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }

    private void showPrayerContent(PrayerItem item) {
        recyclerView.setVisibility(View.GONE);
        contentScroll.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        
        contentTitle.setText(item.title);
        contentText.setText(item.content);
    }

    private List<PrayerItem> loadLocalPrayers() {
        try {
            InputStream is = getResources().openRawResource(R.raw.prayers_ko);
            InputStreamReader reader = new InputStreamReader(is);
            Type listType = new TypeToken<ArrayList<PrayerItem>>() {}.getType();
            return new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void routeToInternationalPrayers(String lang) {
        String url;
        switch (lang) {
            case "el":
                url = "https://prayer.enavasi.gr/";
                break;
            case "cu":
                url = "https://www.molitvoslov.com/";
                break;
            default:
                url = "https://www.goarch.org/chapel/prayers";
                break;
        }
        
        Intent intent = new Intent(this, BibleWebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
        finish();
    }

    public static class PrayerItem {
        public int id;
        public String title;
        public String content;
    }

    private static class PrayerAdapter extends RecyclerView.Adapter<PrayerAdapter.ViewHolder> {
        private final List<PrayerItem> items;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(PrayerItem item);
        }

        public PrayerAdapter(List<PrayerItem> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prayer, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PrayerItem item = items.get(position);
            holder.title.setText(item.title);
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.tv_prayer_item_title);
            }
        }
    }
}
