
package com.allstate.compozed.othello.domain.game;

import com.allstate.compozed.othello.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "rows"
})
@Entity
public class GameBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user = new User();


    @JsonProperty("rows")
    @OneToMany(mappedBy = "gameBoard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Row> rowList = new ArrayList<>();

    @JsonProperty("rows")
    public List<Row> getRows() {
        return rowList;
    }

    @JsonProperty("row")
    public void setRows(List<Row> rows) {
        this.rowList = rows;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // public List<Row> getRowList() {
    //     return rowList;
    // }
    //
    // public void setRowList(List<Row> rowList) {
    //     this.rowList = rowList;
    // }
}
