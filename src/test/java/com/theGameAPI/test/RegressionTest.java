package com.theGameAPI.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utilities.Constants;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;

public class RegressionTest extends Constants{
	
	@Test
	public void registerUser() {
		RestAssured.baseURI = baseURI;
		query = "{\r\n"
				+ "  \"username\": \""+newUser+"\",\r\n"
				+ "  \"password\": \""+password+"\"\r\n"
				+ "}";
		
		given()
			.contentType("application/json")
			.header("Authorization", apiKey)
			.body(query)
		.when().log().all()
			.post("/auth/user/register")
		.then().log().all()
			.assertThat()
			.statusCode(200);
	}
	
	@Test
	public void reRegisterUser() {
		RestAssured.baseURI = baseURI;
		query = "{\r\n"
				+ "  \"username\": \""+existingUsername+"\",\r\n"
				+ "  \"password\": \""+password+"\"\r\n"
				+ "}";
		
		given()
			.contentType("application/json")
			.header("Authorization", apiKey)
			.body(query)
		.when().log().all()
			.post("/auth/user/register")
		.then().log().all()
			.assertThat()
			.statusCode(400)
			.and()
			.body("error.detail", equalTo("Key (username)=("+existingUsername+") already exists."));
	}
	
	
	@Test
	public void userLogin() {
		RestAssured.baseURI = baseURI;
		query = "{\r\n"
				+ "  \"username\": \""+existingUsername+"\",\r\n"
				+ "  \"password\": \""+password+"\"\r\n"
				+ "}";
		
		given()
			.contentType("application/json")
			.header("Authorization", apiKey)
			.body(query)
		.when().log().all()
			.post("/auth/user/login")
		.then().log().all()
			.assertThat()
			.statusCode(200);		
	}
	
	@Test
	public void userLoginIncorrectPassword() {
		RestAssured.baseURI = baseURI;
		query = "{\r\n"
				+ "  \"username\": \""+existingUsername+"\",\r\n"
				+ "  \"password\": \""+incorrectPassword+"\"\r\n"
				+ "}";
		
		given()
			.contentType("application/json")
			.header("Authorization", apiKey)
			.body(query)
		.when().log().all()
			.post("/auth/user/login")
		.then().log().all()
			.assertThat()
			.statusCode(400)
			.and()
			.body("error", equalTo("Username or Password is incorrect"));
	}
	
	@Test
	public void getUsers() {
		RestAssured.baseURI = baseURI;
		
		given()
			.contentType("application/json")
			.header("Authorization", apiKey)
		.when().log().all()
			.get("/v1/user")
		.then().log().all()
			.assertThat()
			.statusCode(200);
	}
	
	@Test(dependsOnMethods = "registerUser")
	public void addUser() {
		RestAssured.baseURI = baseURI;
		query = "{\r\n"
				+ "  \"username\": \""+newUser+"\",\r\n"
				+ "  \"score\": "+score+"\r\n"
				+ "}";
		
		given()
			.contentType("application/json")
			.header("Authorization", apiKey)
			.body(query)
		.when().log().all()
			.post("/v1/user")
		.then().log().all()
			.assertThat()
			.statusCode(201)
			.and()
			.body("message", equalTo("User added."));
	}
	
	@Test(dependsOnMethods = "addUser")
	public void updateUser() {
		RestAssured.baseURI = baseURI;
		query = "{\r\n"
				+ "  \"username\": \""+existingUsername+"\",\r\n"
				+ "  \"score\": "+scoreUpdate+"\r\n"
				+ "}";
		
		given()
			.contentType("application/json")
			.header("Authorization", apiKey)
			.body(query)
		.when().log().all()
			.put("/v1/user")
		.then().log().all()
			.assertThat()
			.statusCode(204);
	}

	
	@Test
	public void getUserByName() {
		RestAssured.baseURI = baseURI;
		query = "{\r\n"
				+ "  \"username\": \""+existingUsername+"\",\r\n"
				+ "  \"password\": \""+password+"\"\r\n"
				+ "}";
		
		Response response = given()
				.contentType("application/json")
				.header("Authorization", apiKey)
				.body(query)
			.when().log().all()
				.post("/auth/user/login");
		
		String json = response.asString();
		System.out.println(json);
		
		String tokenTemp = JsonPath.read(json, ".token").toString();
		
		token = tokenTemp.substring(2, tokenTemp.length()-2);
		
		given().log().all()
		.contentType("application/json")
		.header("Authorization", token)
	.when().log().all()
		.get("/v1/user/"+existingUsername+"")
	.then().log().all()
		.assertThat()
		.statusCode(200);
		
		
	}
}
