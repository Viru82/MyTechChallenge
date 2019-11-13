/*
 * Project: RevolutionIT Tech Test
 * Created By: Virendra Brahmbhatt
 * Date: 13 Nov 2019
 *
 * Feature: 
 * 			As a weather enthusiast I would like to know the number of days in Sydney where the temperature is predicated to be above 20 degrees (at the time of calling the API) in the next 5 days, 
 *			(from the current days date), or whichever period the free subscription will allow. 
 *			I would also like to know how many days it is predicted to be sunny in the same time period
 *
 */

package ApiTestingTest.com.revolutionIT;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

public class CheckWeather extends ConfigValues {

	public static Response response;
	public static String jsonAsString;

	@BeforeClass
	public static void setupURL() {
		// Setup the default URL and API base path to use throughout the tests
		RestAssured.baseURI = "http://api.openweathermap.org/data/2.5";
	}

	@Test
	public void checkWeatherTempraturAndSunnyDays() {

		float currentTemprature;
		int sunnyWeatherId;
		
		// Declaring 2 HashSet variables to store the temperature above 20 Degrees and Weather IDs 800 for Sunny sky in next 5 days  
		Set<String> tempratureAbove20Degree = new HashSet<>();
		Set<String> sunnyDays = new HashSet<>();

		// OpenweatherMap API call to get SYDNEY's whether in CELSIUS for next 5 days in 3 Hours frequency from the current date & time
		response = given()
				.parameter("q", super.SYDNEY)
				.parameter("units", super.CELSIUS)
				.parameter("appid", super.API_KEY).when()
				.get("http://api.openweathermap.org/data/2.5/forecast")
				.then()
				.contentType(ContentType.JSON).extract()
				.response();

		// Get the number of list elements from the Response returned by previous call 
		List<Object> listResponse = response.jsonPath().getList("list");

		
		// Loop through the list elements to check the required conditions
		for (int i = 0; i < listResponse.size(); i++) {
			
			String currentDateTime = response.jsonPath().getString("list[" + i + "].dt_txt");
			String date = currentDateTime.substring(0, 10);

			// Get the temperature to validate if its above 20 degrees then add date in HashSet
			currentTemprature = Float.parseFloat(response.jsonPath().getString("list[" + i + "].main.temp_min"));
			if (currentTemprature > 20) {
				tempratureAbove20Degree.add(date);
			}

			// Get the Weather ID to validate if its Sunny/Clear sky, then add date in HashSet
			sunnyWeatherId = Integer.parseInt(response.jsonPath().getString("list[" + i + "].weather[0].id"));
			if (sunnyWeatherId == 800) {
				sunnyDays.add(date);
			}
		}
		
		// Number of elements in tempratureAbove20Degree HashSet 
		System.out.println("Number of days temprature predicted to be above 20 Degrees in next 5 days :  " + tempratureAbove20Degree.size());
		tempratureAbove20Degree.forEach(System.out::println);

		// Number of elements in sunnyDays HashSet 
		System.out.println("Number of Sunny days predicted in next 5 days :  " + sunnyDays.size());
		sunnyDays.forEach(System.out::println);
	}

}
