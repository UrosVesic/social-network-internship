package com.levi9.internship.social.network.service.defaultservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;


@Service
public class PostGenService {

    private final ImageClient imageClient;
    private final ChatClient chatClient;

    private static final Logger log = LoggerFactory.getLogger(PostGenService.class);

    public PostGenService(ImageClient imageClient, ChatClient chatClient) {
        this.imageClient = imageClient;
        this.chatClient = chatClient;
    }

    public Image generateImage(String prompt) {
        ImageResponse imageResponse = imageClient.call(new ImagePrompt(prompt));
        log.info(imageResponse.getResult().getOutput().getUrl());
        return imageResponse.getResult().getOutput();
    }

    public String generateContent(String prompt){
        return chatClient.call(prompt);
    }
}
