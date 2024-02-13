package com.example.steamgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameListFragment extends Fragment {

    private GameViewModel gameViewModel;
    private GameListAdapter gameListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        gameListAdapter = new GameListAdapter();
        recyclerView.setAdapter(gameListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Obserwuj zmiany w liście gier
        gameViewModel.getAllGames().observe(getViewLifecycleOwner(), new Observer<List<GameEntity>>() {
            @Override
            public void onChanged(List<GameEntity> games) {
                // Aktualizuj listę gier w adapterze
                gameListAdapter.setGames(games);
            }
        });

        // Pobierz dane przy pierwszym utworzeniu fragmentu
        downloadGameData();
    }

    private void downloadGameData() {
        // Tutaj wywołaj pobieranie danych z API, np. używając klasy APIDownload
        // Pamiętaj, aby użyć AsyncTask lub wątków do operacji sieciowych
        APIDownload apiDownload = new APIDownload(this, gameViewModel);
        apiDownload.execute();
    }
}
