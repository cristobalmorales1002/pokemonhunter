package cl.morales.pokehunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class PokedexAdapter extends ArrayAdapter<Pokemon> {
    private Context mContext;
    private OnPokedexClickListener listener;

    public interface OnPokedexClickListener {
        void onStatsClick(Pokemon pokemon);
        void onSoundClick(Pokemon pokemon);
    }

    public PokedexAdapter(Context context, List<Pokemon> pokemonList, OnPokedexClickListener listener) {
        super(context, 0, pokemonList);
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pokedex, parent, false);
        }

        Pokemon pokemon = getItem(position);
        TextView tvName = convertView.findViewById(R.id.tvNamePokedex);
        Button btnStats = convertView.findViewById(R.id.btnStats);
        Button btnSound = convertView.findViewById(R.id.btnSound);

        if (pokemon != null) {
            tvName.setText(pokemon.name);
            btnStats.setOnClickListener(v -> {
                if (listener != null) listener.onStatsClick(pokemon);
            });
            btnSound.setOnClickListener(v -> {
                if (listener != null) listener.onSoundClick(pokemon);
            });
        }
        return convertView;
    }
}
