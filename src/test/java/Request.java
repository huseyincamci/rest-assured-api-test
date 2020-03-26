import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class Request {

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost:3000/";
    }

    @Test
    public void requestScenario() {
        int newUserId = getLastUserId() + 1;
        getRequest();
        postRequest(newUserId, "Jane", "Doe", "janedoe@gmail.com");

        int randomUserId = getValueFromList(getUserIds());
        putRequest(randomUserId, "Yeni Ad", "Yeni Soyad", "adsoyad@gmail.com");

        int randomUserIdForDelete = getValueFromList(getUserIds());
        deleteRequest(randomUserIdForDelete);

    }


    public static void getRequest() {
        Response response =
                given()
                        .log().all()
                        .when()
                        .get("users").prettyPeek()
                        .then()
                        .statusCode(200).extract().response();
    }

    public static void postRequest(int userId, String firstName, String lastName, String email) {
        Response response =
                given()
                        .contentType("application/json")
                        .body(String.format("{\n" +
                                "    \"id\": %d,\n" +
                                "    \"first_name\": \"%s\",\n" +
                                "    \"last_name\": \"%s\",\n" +
                                "    \"email\": \"%s\"\n" +
                                "}", userId, firstName, lastName, email))
                        .log().all()
                        .when()
                        .post("users").prettyPeek()
                        .then()
                        .statusCode(201).extract().response();
        System.out.println(String.format("%d id'li yeni bir kullanıcı eklendi.", userId));
    }

    public static void putRequest(int userId, String firstName, String lastName, String email) {
        Response response =
                given()
                        .contentType("application/json")
                        .body(String.format("{\n" +
                                "    \"id\": %d,\n" +
                                "    \"first_name\": \"%s\",\n" +
                                "    \"last_name\": \"%s\",\n" +
                                "    \"email\": \"%s\"\n" +
                                "}", userId, firstName, lastName, email))
                        .log().all()
                        .when()
                        .put("/users/" + userId).prettyPeek()
                        .then()
                        .statusCode(200).extract().response();
        System.out.println(String.format("%d id'li kullanıcı güncellendi.", userId));
    }

    public static void deleteRequest(int userId) {
        Response response =
                given()
                        .log().all()
                        .when()
                        .delete("/users/" + userId).prettyPeek()
                        .then()
                        .statusCode(200).extract().response();
        System.out.println(String.format("%d id'li kullanıcı silindi.", userId));
    }

    public static ArrayList<Integer> getUserIds() {
        Response response =
                given()
                        .log().all()
                        .when()
                        .get("users").prettyPeek()
                        .then()
                        .statusCode(200).extract().response();

        String userIdString = response.jsonPath().getString("id");
        String[] userIds = userIdString.replace("[", "")
                .replace("]", "").split(",");

        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (String id :
                userIds) {
            ids.add(Integer.parseInt(id.trim()));
        }
        return ids;
    }

    public static int getLastUserId() {
        ArrayList<Integer> ids = getUserIds();
        return ids.get(ids.size() - 1);
    }

    public static int getUserIdSize(Response response) {
        int userIdSize = Integer.parseInt(response.jsonPath().getString("id.size()"));
        return userIdSize;
    }

    public static int getRandomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random rnd = new Random();
        int randomInt = rnd.nextInt((max - min) + 1) + min;
        return randomInt;
    }

    public static int getValueFromList(ArrayList<Integer> list) {
        int randomIndex = getRandomInt(0, list.size() - 1);
        return list.get(randomIndex);
    }
}
