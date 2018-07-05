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
package org.fcrepo.spec.testsuite.versioning;

import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;

/**
 * Tests for GET requests on LDP Version Container Resources
 *
 * @author Daniel Bernstein
 */
public class LdpcvHttpGet extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public LdpcvHttpGet(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.3.1-A
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvMustSupportGet(final String uri) {
        final TestInfo info = setupTest("4.3.1-A", "ldpcvMustSupportGet",
                                        "LDPCv must support GET, as is the case for any LDPR",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);

        //create a versioned resource
        //get the timemap
        //perform a GET and verify that it is successful.
    }

    /**
     * 4.3.1-B
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvMustHaveTimeMapLinkHeader(final String uri) {
        final TestInfo info = setupTest("4.3.1-B", "ldpcvMustHaveTimeMapLinkHeader",
                                        "LDPCv contain TimeMap type link header.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);

        //create a versioned resource
        //get the timemap
        //perform a GET and verify it has the proper header.
    }

    /**
     * 4.3.1-C
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvMustRespondToGetWithApplicationLinkAcceptHeader(final String uri) {
        final TestInfo info = setupTest("4.3.1-C", "ldpcvMustRespondToGetWithApplicationLinkAcceptHeader",
                                        "An LDPCv must respond to GET Accept: application/link-format as " +
                                        "indicated in [ RFC7089 ] section 5 and specified in [ RFC6690 ] section 7.3.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);


    }

    /**
     * 4.3.1-D
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void lpcvMustIncludeAllowHeader(final String uri) {
        final TestInfo info = setupTest("4.3.1-D", "lpcvMustIncludeAllowHeader",
                                        "LDPCv resources must include the Allow header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);


    }

    /**
     * 4.3.1-E
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvMustIncludeAcceptPostIfPostAllowed(final String uri) {
        final TestInfo info = setupTest("4.3.1-E", "ldpcvMustIncludeAcceptPostIfPostAllowed",
                                        "If an LDPCv supports POST, then it must include the Accept-Post header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);
        //check if LDPCv allows "POST"
        //If so,  check for presence of Accept-Post header.

    }

    /**
     * 4.3.1-F
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvMustIncludeAcceptPatchIfPatchAllowed(final String uri) {
        final TestInfo info = setupTest("4.3.1-F", "ldpcvMustIncludeAcceptPatchIfPatchAllowed",
                                        "If an LDPCv supports PATCH, then it must include the Accept-Patch header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);
        //check if LDPCv allows "PATCH"
        //If so,  check for presence of Accept-Patch header.

    }

    /**
     * 4.3.1-G
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvMustHaveContainerLinkHeader(final String uri) {
        final TestInfo info = setupTest("4.3.1-G", "ldpcvMustHaveContainerLinkHeader",
                                        "An LDPCv, being a container must have a \"Link: <http://www" +
                                        ".w3.org/ns/ldp#Container>;rel=\"type\"\"",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);

        //create a versioned resource
        //get the timemap
        //perform a GET and verify it has the proper header.
    }



}
