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
        return  new Image("https://oaidalleapiprodscus.blob.core.windows.net/private/org-PmOcxvUQ6KlkYpwCRnGKwXQB/user-wjnH5EVWH6g3IOo7M8Lts9IT/img-bWZzZujqCs1kJvBAC4zwhEKI.png?st=2024-04-23T15%3A49%3A18Z&se=2024-04-23T17%3A49%3A18Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2024-04-23T04%3A10%3A10Z&ske=2024-04-24T04%3A10%3A10Z&sks=b&skv=2021-08-06&sig=M78v8xt55Uk%2B%2BLzce4263VT2iODt%2BbnLXyTjdpTr7UY%3D", null);
//        ImageResponse imageResponse = imageClient.call(new ImagePrompt(prompt));
//        log.info(imageResponse.getResult().getOutput().getUrl());
//        return imageResponse.getResult().getOutput();
    }

    public String generateContent(String prompt){
        return chatClient.call(prompt);
    }
}
