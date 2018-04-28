/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.spec.testsuite.test;

import java.io.FileNotFoundException;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class Container extends AbstractTest {
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                + "<> a ldp:Container, ldp:BasicContainer;"
                                + "dcterms:title 'Container class Container' ;"
                                + "dcterms:description 'This is a test container for the Fedora API Test Suite.' . ";
    public String username;
    public String password;
    public String pythagorasContainer = "@prefix dc: <http://purl.org/dc/terms/> . "
                                        + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
                                        + "<> dc:title 'Pythagoras Collection'; "
                                        + "dc:abstract 'A collection of materials and facts about Pythagoras' .";
    public String personBody = "@prefix dc: <http://purl.org/dc/terms/> . "
                               + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
                               + "<> a foaf:Person; "
                               + "foaf:name \"Pythagoras\" ; "
                               + "foaf:based_near \"Croton\" ; "
                               + "foaf:interest [ dc:title \"Geometry\" ] .";
    public String portraitContainer = "@prefix ldp: <http://www.w3.org/ns/ldp#> . "
                                      + "@prefix dcterms: <http://purl.org/dc/terms/> . "
                                      + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
                                      + "<> a ldp:DirectContainer; "
                                      + "ldp:membershipResource <%person%>; "
                                      + "ldp:hasMemberRelation foaf:depiction; "
                                      + "dcterms:title \"Portraits of Pythagoras\" .";

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public Container(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 3.1.1-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void createLDPC(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.1-A", "createLDPC",
                                        "Implementations must support the creation and management of [LDP] Containers.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc", ps);
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .contentType("text/turtle")
                   .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                   .header("slug", info.getId())
                   .body(body)
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(201);
        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.1.1-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcContainmentTriples(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.1-B",
                                        "ldpcContainmentTriples",
                                        "LDP Containers must distinguish [containment triples]",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc",
                                        ps);
        final Response pythagoras =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("slug", info.getId())
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .when()
                       .body(pythagorasContainer)
                       .post(uri);
        final String pythagorasLocationHeader = pythagoras.getHeader("Location");

        final String person = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .contentType("text/turtle")
                                         .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                         .header("slug", "person")
                                         .when()
                                         .body(personBody)
                                         .post(pythagorasLocationHeader).asString();

        final Response portraits = RestAssured.given()
                                              .auth().basic(this.username, this.password)
                                              .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                              .contentType("text/turtle")
                                              .header("slug", "portraits")
                                              .when()
                                              .body(portraitContainer.replace("%person%", person))
                                              .post(pythagorasLocationHeader);
        final String portraitsLocationHeader = portraits.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("image/jpeg")
                   .header("slug", "JpgPortrait")
                   .when()
                   .post(portraitsLocationHeader).asString();

        final Response resP = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                         .log().all()
                                         .header("Prefer",
                                                 "return=representation; include=\"http://www" +
                                                 ".w3.org/ns/ldp#PreferContainment\"")
                                         .when()
                                         .get(portraitsLocationHeader);

        ps.append(resP.getStatusLine().toString() + "\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);
        ps.append("\n -Case End- \n").close();

        final boolean triple = TestSuiteGlobals.checkMembershipTriple(body);

        if (triple) {
            Assert.assertTrue(false, "FAIL");
        } else {
            if (body.contains("ldp:contains")) {
                Assert.assertTrue(true, "OK");
            }
        }
    }

    /**
     * 3.1.1-C
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcMembershipTriples(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.1-C", "ldpcMembershipTriples",
                                        "LDP Containers must distinguish [membership] triples.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc",
                                        ps);
        final Response pythagoras =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", info.getId())
                       .when()
                       .body(pythagorasContainer)
                       .post(uri);
        final String pythagorasLocationHeader = pythagoras.getHeader("Location");

        final String person = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .contentType("text/turtle")
                                         .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                         .header("slug", "person")
                                         .when()
                                         .body(personBody)
                                         .post(pythagorasLocationHeader).asString();

        final Response portraits = RestAssured.given()
                                              .auth().basic(this.username, this.password)
                                              .contentType("text/turtle")
                                              .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                              .header("slug", "portraits")
                                              .when()
                                              .body(portraitContainer.replace("%person%", person))
                                              .post(pythagorasLocationHeader);
        final String portraitsLocationHeader = portraits.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("image/jpeg")
                   .header("slug", "JpgPortrait")
                   .when()
                   .post(portraitsLocationHeader).asString();

        final Response resP = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                         .log().all()
                                         .header("Prefer",
                                                 "return=representation; include=\"http://www" +
                                                 ".w3.org/ns/ldp#PreferMembership\"")
                                         .when()
                                         .get(portraitsLocationHeader);

        ps.append(resP.getStatusLine().toString() + "\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);
        ps.append("\n -Case End- \n").close();

        if (body.contains("hasMemberRelation") && body.contains("membershipResource") &&
            !body.contains("ldp:contains")) {
            Assert.assertTrue(true, "OK");
        } else {
            Assert.assertTrue(false, "FAIL");
        }
    }

    /**
     * 3.1.1-D
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcMinimalContainerTriples(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.1-D", "ldpcMinimalContainerTriples",
                                        "LDP Containers must distinguish [minimal-container] triples.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc",
                                        ps);
        final Response pythagoras =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", info.getId())
                       .when()
                       .body(pythagorasContainer)
                       .post(uri);
        final String pythagorasLocationHeader = pythagoras.getHeader("Location");

        final String person = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .contentType("text/turtle")
                                         .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                         .header("slug", "person")
                                         .when()
                                         .body(personBody)
                                         .post(pythagorasLocationHeader).asString();

        final Response portraits = RestAssured.given()
                                              .auth().basic(this.username, this.password)
                                              .contentType("text/turtle")
                                              .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                              .header("slug", "portraits")
                                              .when()
                                              .body(portraitContainer.replace("%person%", person))
                                              .post(pythagorasLocationHeader);
        final String portraitsLocationHeader = portraits.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("image/jpeg")
                   .header("slug", "JpgPortrait")
                   .when()
                   .post(portraitsLocationHeader).asString();

        final Response resP = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                         .log().all()
                                         .header("Prefer",
                                                 "return=representation; include=\"http://www" +
                                                 ".w3.org/ns/ldp#PreferMinimalContainer\"")
                                         .when()
                                         .get(portraitsLocationHeader);

        ps.append(resP.getStatusLine().toString() + "\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);
        ps.append("\n -Case End- \n").close();

        final boolean triple = TestSuiteGlobals.checkMembershipTriple(body);

        if (!triple && !body.contains("ldp:contains")) {
            Assert.assertTrue(true, "OK");
        } else {
            Assert.assertTrue(false, "FAIL");
        }
    }

}
