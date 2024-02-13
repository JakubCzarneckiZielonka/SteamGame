package com.example.steamgame;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "games")
public class GameEntity {
    @PrimaryKey
    public int appId;

    public String gameName;

    // Add any other fields you want to store
}
