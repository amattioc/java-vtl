package no.ssb.vtl.tools.rest.controllers;

/*-
 * ========================LICENSE_START=================================
 * Java VTL Utility connectors
 * %%
 * Copyright (C) 2017 Statistics Norway and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import no.ssb.vtl.connectors.PxApiConnector;
import no.ssb.vtl.script.VTLScriptEngine;
import no.ssb.vtl.tools.rest.representations.DatasetRepresentation;
import no.ssb.vtl.tools.rest.representations.ExecutionRepresentation;
import no.ssb.vtl.tools.rest.representations.ResultRepresentation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ExecutorControllerTest2 {
    
    private ExecutorController controller;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws IOException {
        PxApiConnector connector = new PxApiConnector("");
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(connector.getRestTemplate());
        controller = new ExecutorController(new VTLScriptEngine(connector));

        InputStream responseStream = Resources.getResource(this.getClass(), "/pxApiResponse.json").openStream();
        InputStream queryStream = Resources.getResource(this.getClass(), "/pxApiQuery.json").openStream();
        JsonNode queryJson = mapper.readTree(queryStream);

        mockServer.expect(requestTo("https://jboss-utv.ssb.no/api/v0/no/table/KostraTesting0001"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(queryJson.toString()))
                .andRespond(withSuccess(
                        new InputStreamResource(responseStream),
                        MediaType.APPLICATION_JSON)
                );
    }

    @Test
    public void testVtlPxApi() throws IOException, ScriptException {
        InputStream requestStream = Resources.getResource(this.getClass(), "/executorRequest.json").openStream();
        ExecutionRepresentation request = mapper.readValue(requestStream, ExecutionRepresentation.class);
        ResultRepresentation result = (ResultRepresentation) controller.execute(request);
        DatasetRepresentation resultDataset = result.getResultDataset();
        System.out.println(mapper.writeValueAsString(resultDataset));
        assertThat(resultDataset.getStructure()).extracting("name").containsExactly("Tid", "KOKkommuneregion0000", "KOSnokkel0000");
        assertThat(resultDataset.getData())
                .containsExactly(Lists.newArrayList("2015", "ekg", 320.39240506329113924050632911392d));

    }
}