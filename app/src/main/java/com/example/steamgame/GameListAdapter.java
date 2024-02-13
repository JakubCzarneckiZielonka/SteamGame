package com.example.steamgame;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    private List<GameEntity> games;

    public void setGames(List<GameEntity> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        if (games != null) {
            GameEntity currentGame = games.get(position);
            holder.bind(currentGame);
        }
    }

    @Override
    public int getItemCount() {
        return games != null ? games.size() : 0;
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        private final TextView gameNameTextView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameNameTextView = itemView.findViewById(R.id.gameNameTextView);
        }

        public void bind(GameEntity game) {
            gameNameTextView.setText(game.gameName);
        }
    }
}
