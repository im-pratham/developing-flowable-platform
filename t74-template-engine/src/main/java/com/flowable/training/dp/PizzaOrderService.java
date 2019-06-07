package com.flowable.training.dp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.content.api.ContentItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flowable.template.api.TemplateService;

@Service
public class PizzaOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PizzaOrderService.class);

    private final TemplateService templateService;
    private final ObjectMapper objectMapper;

    public PizzaOrderService(TemplateService templateService, ObjectMapper objectMapper) {
        this.templateService = templateService;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> simplePizzaOrder(ArrayNode pizzas) {
        List<Map<String, Object>> simplePizzaOrders = new ArrayList<>();
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
        for (JsonNode jsonNode : pizzas) {
            Map<String, Object> e = objectMapper.convertValue(jsonNode, typeRef);
            simplePizzaOrders.add(e);
        }
        return simplePizzaOrders;
    }

    public void sendPizzaOrder(List<ContentItem> pizzaOrder) {
        LOGGER.info("TODO: Send PDF to pizza shop. For now, view the PDF in work!");
    }

    public void sendPizzaNotifications(String language, ArrayNode pizzas) {
        for (JsonNode pizza : pizzas) {
            String pizzaName = pizza.get("pizza").asText();
            for (JsonNode users : pizza.get("users")) {
                String name = users.get("name").asText();
                String mail = users.get("mail").asText();
                sendNotifiation(language, pizzaName, name, mail);
            }
        }
    }

    private void sendNotifiation(String language, String pizzaName, String name, String mail) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("pizzaName", pizzaName);
        payload.put("name", name);

        Map<String, Object> variants = new HashMap<>();
        variants.put("language", language);

        variants.put("messagePart", "subject");
        String subject = templateService.processTemplate("pizza-notification", variants, payload).getProcessedContent();

        variants.put("messagePart", "body");
        String body = templateService.processTemplate("pizza-notification", variants, payload).getProcessedContent();

        LOGGER.info("Sending mail with Subject: '{}' and Body: '{}' to '{}'", subject, body, mail);
    }

}
