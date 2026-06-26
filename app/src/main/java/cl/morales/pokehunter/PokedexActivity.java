package cl.morales.pokehunter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PokedexActivity extends AppCompatActivity {
    private ListView listView;
    private TextView tvEmpty;
    private List<Pokemon> pokemonList = new ArrayList<>();
    private PokedexAdapter adapter;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        listView = findViewById(R.id.listViewPokedex);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new PokedexAdapter(this, pokemonList, new PokedexAdapter.OnPokedexClickListener() {
            @Override
            public void onStatsClick(Pokemon pokemon) {
                showStatsDialog(pokemon);
            }

            @Override
            public void onSoundClick(Pokemon pokemon) {
                playSound(pokemon.cryUrl);
            }
        });
        listView.setAdapter(adapter);

        loadCaughtPokemons();
    }

    private void loadCaughtPokemons() {
        SharedPreferences prefs = getSharedPreferences("pokedex", Context.MODE_PRIVATE);
        String savedData = prefs.getString("caught_pokemon", "[]");
        try {
            JSONArray array = new JSONArray(savedData);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                pokemonList.add(new Pokemon(
                        obj.getString("name"),
                        obj.getInt("hp"),
                        obj.getInt("attack"),
                        obj.getInt("defense"),
                        obj.getString("cryUrl")
                ));
            }
            adapter.notifyDataSetChanged();

            if (pokemonList.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStatsDialog(Pokemon pokemon) {
        String msg = "PS: " + pokemon.hp + "\n" +
                     "Ataque: " + pokemon.attack + "\n" +
                     "Defensa: " + pokemon.defense;

        new AlertDialog.Builder(this)
                .setTitle("Stats de " + pokemon.name.toUpperCase())
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    private void playSound(String url) {
        if (!NetworkUtils.isInternetAvailable(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Sin conexión")
                    .setMessage("Necesitas internet para reproducir el sonido.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            );
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(PokedexActivity.this, "Error reproduciendo sonido", Toast.LENGTH_SHORT).show();
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al preparar sonido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
