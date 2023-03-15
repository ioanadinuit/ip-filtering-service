package iprules.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iprules.rest.dto.IpAllowedDTO;
import iprules.rest.dto.IpFilterRuleDTO;
import iprules.service.IpFilterRuleService;
import iprules.service.exception.IpAddressValidatorException;
import iprules.service.exception.IpRuleMissingException;
import iprules.service.exception.SqlErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static iprules.service.Constants.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(IpFilterRuleController.class)
public class IpFilterRuleControllerTest {
    private static final String URI = "/ip-filter-rules";
    private static final String ALLOW_URI = "/ip-filter-rules/allowed";
    private static final String SOURCE_IP_PARAM = "sourceIp";
    private static final String DESTINATION_IP_PARAM = "destinationIp";

    @Autowired
    MockMvc mvc;

    @MockBean
    private IpFilterRuleService service;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenCreateNewSubnetRuleShouldReturnOk() throws Exception {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceSubnetIp(SOURCE_SUBNET_IP)
                .destinationSubnetIp(DESTINATION_SUBNET_IP)
                .allow(true)
                .build();

        when(service.save(ruleDTO)).thenReturn(ruleDTO);

        mvc.perform(post(URI)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ruleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sourceSubnetIp").value(SOURCE_SUBNET_IP))
                .andExpect(jsonPath("$.destinationSubnetIp").value(DESTINATION_SUBNET_IP))
                .andExpect(jsonPath("$.allow").value(true));
    }

    @Test
    void whenCreateNewRangeRuleShouldReturnOk() throws Exception {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceStartIp(SOURCE_START_IP)
                .sourceEndIp(SOURCE_END_IP)
                .destinationStartIp(DESTINATION_START_IP)
                .destinationEndIp(DESTINATION_END_IP)
                .allow(true)
                .build();

        when(service.save(ruleDTO)).thenReturn(ruleDTO);

        mvc.perform(post(URI)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ruleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sourceStartIp").value(SOURCE_START_IP))
                .andExpect(jsonPath("$.sourceEndIp").value(SOURCE_END_IP))
                .andExpect(jsonPath("$.destinationStartIp").value(DESTINATION_START_IP))
                .andExpect(jsonPath("$.destinationEndIp").value(DESTINATION_END_IP))
                .andExpect(jsonPath("$.allow").value(true));
    }

    @Test
    void whenCreateInvalidRangeRuleShouldReturnBadRequest() throws Exception {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .allow(true)
                .build();

        when(service.save(ruleDTO)).thenThrow(new IpAddressValidatorException(""));

        mvc.perform(post(URI)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ruleDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDeleteByIdShouldReturnOk() throws Exception {
        doNothing().when(service).deleteById(1l);
        mvc.perform(delete(URI.concat("/1"))).andExpect(status().isOk());
    }

    @Test
    void whenDeleteByIdGetsExceptionShouldReturnNotFound() throws Exception {
        doThrow(new IpRuleMissingException("Not found rule id: 1"))
                .when(service).deleteById(1l);
        mvc.perform(delete(URI.concat("/1"))).andExpect(status().isNotFound());
    }

    @Test
    void whenGetByIdShouldReturnOk() throws Exception {
        IpFilterRuleDTO ruleDTO = IpFilterRuleDTO.builder()
                .sourceSubnetIp(SOURCE_SUBNET_IP)
                .destinationSubnetIp(DESTINATION_SUBNET_IP)
                .allow(true)
                .build();
        when(service.findById(1l)).thenReturn(ruleDTO);
        mvc.perform(get(URI.concat("/1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceSubnetIp").value(SOURCE_SUBNET_IP))
                .andExpect(jsonPath("$.destinationSubnetIp").value(DESTINATION_SUBNET_IP))
                .andExpect(jsonPath("$.allow").value(true));;
    }

    @Test
    void whenGetByIdGivenExceptionOccursShouldReturnNotFound() throws Exception {
        when(service.findById(1l)).thenThrow(new IpRuleMissingException(""));
        mvc.perform(get(URI.concat("/1"))).andExpect(status().isNotFound());
    }

    @Test
    void whenGetIsAllowedGivenValidDataShouldReturnOk() throws Exception {
        IpAllowedDTO ipAllowedDTO = new IpAllowedDTO(true);
        when(service.isAllowed(SOURCE_IP, DESTINATION_IP)).thenReturn(ipAllowedDTO);
        mvc.perform(get(buildUri()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ipsAllowed").value(true));
    }

    @Test
    void whenGetIsAllowedGivenExceptionShouldReturnServerError() throws Exception {
        when(service.isAllowed(SOURCE_IP, DESTINATION_IP)).thenThrow(new SqlErrorException(""));
        mvc.perform(get(buildUri()))
                .andExpect(status().isInternalServerError());
    }

    private String buildUri() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(SOURCE_IP_PARAM, SOURCE_IP);
        queryParams.add(DESTINATION_IP_PARAM, DESTINATION_IP);
        return UriComponentsBuilder.fromUriString(ALLOW_URI)
                .queryParams(queryParams).toUriString();
    }
}
