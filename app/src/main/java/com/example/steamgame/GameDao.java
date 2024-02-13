package com.example.steamgame;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGame(GameEntity game);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGames(List<GameEntity> games);

    @Query("SELECT * FROM games")
    LiveData<List<GameEntity>> getAllGames();
}
