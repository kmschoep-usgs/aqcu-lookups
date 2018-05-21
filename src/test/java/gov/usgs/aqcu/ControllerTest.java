package gov.usgs.aqcu;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;

/*
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ControllerTest {
	@Autowired
	private Gson gson;
	private Controller controller;

	@Before
	public void setup() {
	}
}
*/