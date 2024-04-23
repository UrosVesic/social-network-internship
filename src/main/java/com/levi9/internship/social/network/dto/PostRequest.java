package com.levi9.internship.social.network.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @NotEmpty(message = "Content field should not be empty.")
    private String content;

    @Pattern(regexp = "^(PRIVATE|PUBLIC)$", message = "Valid values for Visibility are PRIVATE or PUBLIC.")
    @NotEmpty(message = "Visibility field should not be empty.")
    private String visibility;

    private Long groupId;

    private String imageUrl;
}
