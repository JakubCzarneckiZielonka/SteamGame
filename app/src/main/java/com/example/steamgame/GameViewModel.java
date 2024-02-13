package com.example.steamgame;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GameViewModel extends AndroidViewModel {
    private GameDao gameDao;
    private LiveData<List<GameEntity>> allGames;

    public GameViewModel(Application application) {
        super(application);
        GameDatabase database = GameDatabase.getDatabase(application);
        gameDao = database.gameDao();
        allGames = gameDao.getAllGames();
    }

    public LiveData<List<GameEntity>> getAllGames() {
        return allGames;
    }

    public void insertGames(List<GameEntity> games) {
        new InsertGamesAsyncTask(gameDao).execute(games);
    }

    private static class InsertGamesAsyncTask extends AsyncTask<List<GameEntity>, Void, Void> {
        private GameDao asyncTaskDao;

        InsertGamesAsyncTask(GameDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<GameEntity>... params) {
            asyncTaskDao.insertGames(params[0]);
            return null;
        }
    }
}

