package com.jpa.demo.configuration;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class EnvironmentPostProcessorExample implements EnvironmentPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(EnvironmentPostProcessorExample.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String,String> map = new HashMap<>();
        JSONObject props = null;
        try {
            RestTemplate template = new RestTemplate();
            props  = new JSONObject(template.getForObject("http://127.0.0.1:8089/props",String.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("test env"+ environment.getPropertySources());
        environment.getPropertySources().forEach((propertySource -> log.info(propertySource.toString())));



    }
}
