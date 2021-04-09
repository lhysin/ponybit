package io.exchange.test.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.exchange.web.controller.view.UserController;

//@RunWith(SpringRunner.class)
//@WebMvcTest(UserController.class)
//@ActiveProfiles({"dev", "prd"})
public class UserControllerTest {

//    /**
//     * view test
//     */
//    
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Before
//    public void init() {
//    }
//
//    @Test
//    public void home() throws Exception {
//        this.mvc.perform(get("/").accept(MediaType.TEXT_PLAIN))
//        .andExpect(status().isOk());
//    }
}
