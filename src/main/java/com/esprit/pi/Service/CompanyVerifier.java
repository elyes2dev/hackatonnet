package com.esprit.pi.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class CompanyVerifier {

    private static final String API_URL = "https://api.opencorporates.com/v0.4/companies/search?q=";

    // TEMP: Simulated result until API key is available
    public boolean verifyCompany(String companyName) {
        // Fake logic for demo purposes
        return companyName.toLowerCase().contains("sagemcom");
    }
}
