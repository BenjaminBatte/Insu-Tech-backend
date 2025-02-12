package com.insurance.policy.insutech.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AutoPolicyControllerIT {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldCreatePolicy() throws Exception {
        String uniquePolicyNumber = "AP-" + System.nanoTime();

        mockMvc.perform(post("/api/v1/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "policyNumber": "%s",
                            "firstName": "John",
                            "lastName": "Doe",
                            "policyType": "COMP",
                            "status": "ACT",
                            "vehicleMake": "Toyota",
                            "vehicleModel": "Camry",
                            "vehicleYear": 2022,
                            "premiumAmount": 500.00,
                            "startDate": "2024-01-01",
                            "endDate": "2025-01-01"
                        }
                        """.formatted(uniquePolicyNumber)))
                .andExpect(status().isCreated());
    }



    @Test
    void shouldGetAllPolicies() throws Exception {
        mockMvc.perform(get("/api/v1/policies"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFilterPolicies() throws Exception {
        mockMvc.perform(get("/api/v1/policies/filter")
                        .param("startDate", "2023-01-01")
                        .param("status", "ACT")
                        .param("type", "COMP")
                        .param("firstName", "John")
                        .param("vehicleMake", "Toyota"))
                .andExpect(status().isOk());
    }
}
