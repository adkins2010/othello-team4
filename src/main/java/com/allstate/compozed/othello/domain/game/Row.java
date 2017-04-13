
package com.allstate.compozed.othello.domain.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import com.allstate.compozed.othello.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "row"
})
@Entity
public class Row {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gameboard_id")
    @JsonIgnore
    private GameBoard gameBoard = new GameBoard();

    @JsonProperty("row")
    @ElementCollection(targetClass=String.class)
    private List<String> row = new ArrayList<>();

    public void setRow(){
      for (int i =0; i < 8; i++)
        this.row.add("X");
    }

    public List<String> getRow() {
        return row;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

}
