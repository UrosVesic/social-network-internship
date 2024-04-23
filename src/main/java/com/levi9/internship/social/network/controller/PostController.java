package com.levi9.internship.social.network.controller;

import com.levi9.internship.social.network.dto.*;
import com.levi9.internship.social.network.service.defaultservice.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Log4j2
@RestController
@RequestMapping(value = "/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(final PostService postService) {
        this.postService = postService;
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(
            final Principal principal,
            @RequestBody @Valid final PostRequest request) {
        if (request.getGroupId() == null) {
            return postService.createProfilePost(principal.getName(), request);
        } else {
            return postService.createPostWithinGroup(principal.getName(), request);
        }
    }

    @PostMapping(value = "/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse generatePost(@RequestParam String topic) {
        return postService.generatePost(topic);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(path = "/{postId}")
    public void delete(final Principal principal, @PathVariable final Long postId) {
        postService.delete(principal.getName(), postId);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/{id}")
    public PostResponse getById(@PathVariable final Long id, final Principal principal) {
        return postService.getById(principal.getName(), id);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/{postId}/comment")
    public CommentResponse createComment(
            final Principal principal,
            @PathVariable final Long postId,
            @RequestBody @Valid final CommentRequest request) {
        return postService.createComment(principal.getName(), postId, request);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/{postId}/comments/{commentId}/reply")
    public CommentResponse createCommentReply(
            final Principal principal,
            @PathVariable final Long postId,
            @PathVariable final Long commentId,
            @RequestBody @Valid final CommentRequest request) {
        return postService.createCommentReply(principal.getName(), postId, commentId, request);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(value = "/{postId}/comments/{commentId}")
    public void deleteComment(
            @PathVariable final Long postId,
            @PathVariable final Long commentId,
            final Principal principal) {
        postService.deleteComment(principal.getName(), postId, commentId);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/{id}/hide")
    public void hidePost(
            @PathVariable final Long id,
            @RequestBody @Valid final HidePostRequest requestBody,
            final Principal principal) {
        postService.hidePost(id, requestBody.getUserId(), principal.getName());
    }
}
