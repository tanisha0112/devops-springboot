package com.myapp.spring.integration;


import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.spring.model.Product;
import com.myapp.spring.repository.ProductRepository;


@SpringBootTest

@AutoConfigureMockMvc
public class ProductIntegrationTest {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private MockMvc mockMvc;
	private static File DATA_JSON= Paths.get("src","test","resources","products.json").toFile();
	
	@BeforeEach
	public void setup() throws JsonParseException, JsonMappingException, IOException {
	
	Product products []=new ObjectMapper().readValue(DATA_JSON,Product[].class);
	
	//save each product to database
	Arrays.stream(products).forEach(repository::save);
	}

@AfterEach
public void cleanUp() {
	repository.deleteAll();
}
	
	@Test

	@DisplayName("Test Product by ID - GET /api/v1/products/")
	public void testGetProductsById() throws Exception {
		

		// Perform GET request
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/{id}",37))
		// Validate Status should be 200 OK and JSON response received
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		
		// Validate Response Body
		
		.andExpect(jsonPath("$.productId" , is(37)))
		.andExpect(jsonPath("$.productName" , is("OnePlus")))
		.andExpect(jsonPath("$.description" , is("OnePlus9Pro")))
		.andExpect(jsonPath("$.price" , is(70000.00)))
		.andExpect(jsonPath("$.starRating" , is(4.5)));
		
		
		
		
	}
	
	@Test

	@DisplayName("Test All Products /api/v1/products/")
	public void testGetAllProducts() throws Exception {
		
		// Prepare Mock Service Method
		
		Product product1 = new Product("OnePlus", "OnePlus9Pro", 70000.00, 4.5);
		product1.setProductId(37);
		
		Product product2 = new Product("OnePlus", "OnePlus8Pro", 60000.00, 4.5);
		product2.setProductId(38);
		
		List<Product>products = new ArrayList<>();
		products.add(product1);
		products.add(product2);
		
		//prepare Mock service Method
		
		doReturn(products).when(repository).findAll();
				
		// Perform GET request
		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
		// Validate Status should be 200 OK and JSON response received
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		
		// Validate Response Body
		
		.andExpect(jsonPath("$[0].productId" , is(38)))
		.andExpect(jsonPath("$[0].productName" , is("OnePlus")))
		.andExpect(jsonPath("$[0].description" , is("OnePlus9Pro")))
		.andExpect(jsonPath("$[0].price" , is(70000.00)))
		.andExpect(jsonPath("$[0].starRating" , is(4.5)))
		
	    .andExpect(jsonPath("$[1].productId" , is(39)))
		.andExpect(jsonPath("$[1].productName" , is("OnePlus")))
		.andExpect(jsonPath("$[1].description" , is("OnePlus8Pro")))
		.andExpect(jsonPath("$[1].price" , is(60000.00)))
		.andExpect(jsonPath("$[1].starRating" , is(4.5)));
		
		
		
	}
	
	@Test
	@DisplayName("Test Add New Product")
	public void testAddNewProduct() throws Exception {
		
		// Prepare Mock Service Method
		
		Product newproduct = new Product("OnePlus", "OnePlus9Pro", 70000.00, 4.5);
		
		Product mockproduct = new Product("OnePlus", "OnePlus9Pro", 70000.00, 4.5);
		mockproduct.setProductId(50);
		
		//prepare Mock service Method
		
		doReturn(mockproduct).when(repository).save(ArgumentMatchers.any());
		
		
		// Perform POST request
		
		mockMvc.perform(post("/api/v1/product")
		// Validate Status should be 200 OK and JSON response received
		
		.contentType(MediaType.APPLICATION_JSON_VALUE)
		.content(new ObjectMapper().writeValueAsString(newproduct)))
		// Validate Response Body
		
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(jsonPath("$.productId" , is(50)))
		.andExpect(jsonPath("$.productName" , is("OnePlus")))
		.andExpect(jsonPath("$.description" , is("OnePlus9Pro")))
		.andExpect(jsonPath("$.price" , is(70000.00)))
		.andExpect(jsonPath("$.starRating" , is(4.5)));
		
		
		
		
	}
	
	
	

}
