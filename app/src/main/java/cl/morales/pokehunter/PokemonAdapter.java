package cl.morales.pokehunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class PokemonAdapter extends ArrayAdapter<Pokemon> {
    private Context mContext;
    private OnCaptureClickListener listener;

    public interface OnCaptureClickListener {
        void onCapture(Pokemon pokemon);
    }

    public PokemonAdapter(Context context, List<Pokemon> pokemonList, OnCaptureClickListener listener) {
        super(context, 0, pokemonList);
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pokemon, parent, false);
        }

        Pokemon pokemon = getItem(position);
        TextView tvName = convertView.findViewById(R.id.tvName);
        Button btnCapture = convertView.findViewById(R.id.btnCapture);

        if (pokemon != null) {
            tvName.setText(pokemon.name);
            btnCapture.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCapture(pokemon);
                }
            });
        }
        return convertView;
    }
}
