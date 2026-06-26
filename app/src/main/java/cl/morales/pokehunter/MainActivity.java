package cl.morales.pokehunter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnPokedex;
    private List<Pokemon> pokemonList = new ArrayList<>();
    private PokemonAdapter adapter;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnPokedex = findViewById(R.id.btnPokedex);

        adapter = new PokemonAdapter(this, pokemonList, this::capturePokemon);
        listView.setAdapter(adapter);

        btnPokedex.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PokedexActivity.class)));

        loadPokemons();
    }

    private void loadPokemons() {
        if (!NetworkUtils.isInternetAvailable(this)) {
            showNoInternetDialog();
            return;
        }

        executorService.execute(() -> {
            try {
                String profJson = NetworkUtils.fetchJson(ApiConfig.BASE_URL);
                JSONArray profArray = new JSONArray(profJson);
                
                List<Pokemon> temp = new ArrayList<>();
                for (int i = 0; i < profArray.length(); i++) {
                    JSONObject obj = profArray.getJSONObject(i);
                    String name = obj.getString("name");
                    
                    // Fetch from PokeAPI
                    String pokeJson = NetworkUtils.fetchJson(ApiConfig.POKEAPI_URL + name);
                    JSONObject pokeObj = new JSONObject(pokeJson);
                    
                    int hp = 0, attack = 0, defense = 0;
                    JSONArray stats = pokeObj.getJSONArray("stats");
                    for (int j = 0; j < stats.length(); j++) {
                        JSONObject statObj = stats.getJSONObject(j);
                        int baseStat = statObj.getInt("base_stat");
                        String statName = statObj.getJSONObject("stat").getString("name");
                        if ("hp".equals(statName)) hp = baseStat;
                        else if ("attack".equals(statName)) attack = baseStat;
                        else if ("defense".equals(statName)) defense = baseStat;
                    }
                    
                    String cryUrl = pokeObj.getJSONObject("cries").getString("latest");
                    temp.add(new Pokemon(name, hp, attack, defense, cryUrl));
                }
                
                new Handler(Looper.getMainLooper()).post(() -> {
                    pokemonList.clear();
                    pokemonList.addAll(temp);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(MainActivity.this, "Error cargando datos de la API", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void capturePokemon(Pokemon pokemon) {
        if (!NetworkUtils.isInternetAvailable(this)) {
            showNoInternetDialog();
            return;
        }

        List<String> misPokemon = Arrays.asList(ApiConfig.MIS_POKEMON);
        if (misPokemon.contains(pokemon.name.toLowerCase())) {
            savePokemon(pokemon);
            Toast.makeText(this, "Captura exitosa!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Este pokémon le pertenece a otro entrenador", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePokemon(Pokemon pokemon) {
        SharedPreferences prefs = getSharedPreferences("pokedex", Context.MODE_PRIVATE);
        String savedData = prefs.getString("caught_pokemon", "[]");
        try {
            JSONArray array = new JSONArray(savedData);
            
            // Avoid duplicates
            boolean alreadyCaught = false;
            for(int i=0; i<array.length(); i++) {
                if (array.getJSONObject(i).getString("name").equals(pokemon.name)) {
                    alreadyCaught = true;
                    break;
                }
            }
            if (alreadyCaught) return;

            JSONObject obj = new JSONObject();
            obj.put("name", pokemon.name);
            obj.put("hp", pokemon.hp);
            obj.put("attack", pokemon.attack);
            obj.put("defense", pokemon.defense);
            obj.put("cryUrl", pokemon.cryUrl);
            array.put(obj);
            
            prefs.edit().putString("caught_pokemon", array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sin conexión")
                .setMessage("No se detectó conexión a internet.")
                .setPositiveButton("OK", null)
                .show();
    }
}