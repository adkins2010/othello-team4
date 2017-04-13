package com.allstate.compozed.othello.controller;

import com.allstate.compozed.othello.domain.game.GameBoard;
import com.allstate.compozed.othello.domain.game.Row;
import com.allstate.compozed.othello.domain.user.User;
import com.allstate.compozed.othello.repository.GameBoardRepository;
import com.allstate.compozed.othello.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by localadmin on 4/3/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OthelloControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    GameBoardRepository gameBoardRepository;

    @Autowired
    MockMvc mockMvc;

    User user;

    @Before
    public void setup() {
        user = new User();
        user.setEmailAddress("zquinn@allstate.com");
        user.setPassword("allstate");
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @Rollback
    public void testRegisterUserEndpoint() throws Exception {
        MockHttpServletRequestBuilder request = post("/users/").contentType(MediaType.APPLICATION_JSON)
                .content("{\"emailAddress\": \"zquinn@allstate.com\",\"password\":\"allstate\"}");

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress", equalTo("zquinn@allstate.com")))
                .andExpect(jsonPath("$.password", equalTo("allstate")));
    }

    @Test
    @Transactional
    @Rollback
    public void testRegisterUserDatabase() throws Exception {
        MockHttpServletRequestBuilder request = post("/users/").contentType(MediaType.APPLICATION_JSON)
                .content("{\"emailAddress\": \"zquinn@allstate.com\",\"password\":\"allstate\"}");

        this.mockMvc.perform(request)
                .andExpect(status().isOk());

        assertEquals(1, this.userRepository.findAll().spliterator().getExactSizeIfKnown());
    }

    // @Test
    // @Transactional
    // @Rollback
    // public void testRecoverPasswordSuccessful() throws Exception {
    //     userRepository.save(user);
    //     MockHttpServletRequestBuilder request = post("/users/recover/").contentType(MediaType.APPLICATION_JSON)
    //             .content("{\"emailAddress\": \"zquinn@allstate.com\"}");
    //
    //     this.mockMvc.perform(request)
    //             .andExpect(status().is2xxSuccessful());
    // }

    @Test
    @Transactional
    @Rollback
    public void testRecoverPasswordFailed() throws Exception {
        userRepository.save(user);
        MockHttpServletRequestBuilder request = post("/users/recover/").contentType(MediaType.APPLICATION_JSON)
                .content("{\"emailAddress\": \"madkk@allstate.com\"}");

        this.mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    @Rollback
    public void testLogin() throws Exception {

        userRepository.save(user);
        MockHttpServletRequestBuilder request = post("/users/login/").contentType(MediaType.APPLICATION_JSON)
                .content("{\"emailAddress\": \"zquinn@allstate.com\",\"password\":\"allstate\"}");

        this.mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @Rollback
    public void testLoginFailed() throws Exception {
        userRepository.save(user);
        MockHttpServletRequestBuilder request = post("/users/login/").contentType(MediaType.APPLICATION_JSON)
                .content("{\"emailAddress\": \"zquinn@allstate.com\",\"password\":\"thiswillfail\"}");

        this.mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSaveGameBoard() throws Exception {
        userRepository.save(user);

        GameBoard gameBoard = new GameBoard();

        gameBoard.setUser(user);

        Row row = new Row();
        JSONArray expectedRowsArray = new JSONArray();
        JSONObject expectedRowObj = new JSONObject();
        JSONArray expectedInitRowArray = new JSONArray();
        JSONArray expectedRowArray = new JSONArray();
        for (int i =0;i<8;i++){
          expectedInitRowArray.put("X");
        }
        expectedRowArray.put("X");
        expectedRowArray.put("X");
        expectedRowArray.put("Larry");
        expectedRowArray.put("X");
        expectedRowArray.put("Zach");
        expectedRowArray.put("X");
        expectedRowArray.put("X");
        expectedRowArray.put("X");
        for (int i =0;i<8;i++)
        {
            row.setRow();
            row.setGameBoard(gameBoard);
            gameBoard.getRows().add(row);

            if (i == 3) {
              expectedRowObj.put("row", expectedRowArray);
            } else {
              expectedRowObj.put("row",expectedInitRowArray);
            }
            expectedRowObj.put("id", i+20);
            expectedRowsArray.put(expectedRowObj);

            row = new Row();
            expectedRowObj = new JSONObject();

        }

        JSONObject expected = new JSONObject();
        expected.put("rows", expectedRowsArray);
        expected.put("id", 1L);

        gameBoardRepository.save(gameBoard);

        System.out.println("=============================================");
        System.out.println(expected.toString());
        System.out.println("=============================================");
        MockHttpServletRequestBuilder request = put("/games/" + gameBoard.getId() + "/").contentType(MediaType.APPLICATION_JSON)
                  .content(expected.toString());

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows[3].row", contains("X","X","Larry","X","Zach","X","X","X")))
                .andExpect(jsonPath("$.rows[7].row", contains("X","X","X","X","X","X","X","X")))
                .andExpect(jsonPath("$.id", equalTo(expected.getInt("id"))));
                // .andExpect(assertArrayEquals(jsonPath("$.rows[3].row","VALUE"),
                //   expected.getJSONArray("rows").getJSONObject(3).getJSONArray("row")));

    }

    @Test
    public void testGetGameBoard() throws Exception {

        userRepository.save(user);
        GameBoard gameBoard = new GameBoard();

        gameBoard.setUser(user);

        Row row = new Row();

        for (int i =0;i<8;i++)
        {
            row.setRow();
            row.setGameBoard(gameBoard);
            gameBoard.getRows().add(row);

            row = new Row();
        }

        gameBoardRepository.save(gameBoard);

        MockHttpServletRequestBuilder request = get("/games/" + gameBoard.getId() + "/")
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows[0].row[0]", equalTo("X")))
                .andExpect(jsonPath("$.rows[1].row[7]", equalTo("X")))
                .andExpect(jsonPath("$.rows[7].row[7]", equalTo("X")));

    }

    // @Test
    // @Transactional
    // @Rollback
    // public void testGetGameBoards() throws Exception {
    //
    //     userRepository.save(user);
    //     GameBoard gameBoard = new GameBoard();
    //
    //     gameBoard.setUser(user);
    //
    //     Row row = new Row();
    //
    //     for (int i =0;i<8;i++)
    //     {
    //         row.initRow();
    //         row.setGameBoard(gameBoard);
    //         gameBoard.getRowList().add(row);
    //
    //         row = new Row();
    //     }
    //
    //     GameBoard gameBoard1 = new GameBoard();
    //
    //     gameBoard1.setUser(user);
    //
    //     for (int i =0;i<8;i++)
    //     {
    //         row.initRow();
    //         row.setGameBoard(gameBoard);
    //         gameBoard.getRowList().add(row);
    //
    //         row = new Row();
    //     }
    //
    //     gameBoardRepository.save(gameBoard);
    //     gameBoardRepository.save(gameBoard1);
    //
    //     MockHttpServletRequestBuilder request = get( "/" + user.getId() + "/games/" )
    //             .contentType(MediaType.APPLICATION_JSON);
    //
    //     this.mockMvc.perform(request)
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$", hasSize(2)));;
    // }

//    @Test
//    @Transactional
//    @Rollback
//    public void testSaveGameBoardUsingGameID() throws Exception {
//        userRepository.save(user);
//        MockHttpServletRequestBuilder request = post("/user/"+user.getId()+"/game/").contentType(MediaType.APPLICATION_JSON)
//                .content("{\"emailAddress\": \"zquinn@allstate.com\",\"password\":\"thiswillfail\"}");
//
//    }
}
