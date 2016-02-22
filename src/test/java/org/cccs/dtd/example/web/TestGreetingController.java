package org.cccs.dtd.example.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

/**
 * Created by boycook on 2/02/2016.
 */
public class TestGreetingController {

    private GreetingController servlet;
    private Model model;

    @Before
    public void setup() {
        model = mock(Model.class);
        servlet = new GreetingController();
    }

    @Test
    public void getGreetingShouldWork() throws ServletException, IOException {
//        servlet.greeting(anyString(), model);
    }
}
