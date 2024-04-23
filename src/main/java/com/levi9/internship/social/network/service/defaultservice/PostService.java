package com.levi9.internship.social.network.service.defaultservice;

import com.levi9.internship.social.network.dao.*;
import com.levi9.internship.social.network.dto.*;
import com.levi9.internship.social.network.exceptions.BusinessException;
import com.levi9.internship.social.network.exceptions.ErrorCode;
import com.levi9.internship.social.network.model.*;
import com.levi9.internship.social.network.service.AuthService;
import com.levi9.internship.social.network.service.EmailService;
import com.levi9.internship.social.network.service.GroupService;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.ai.image.Image;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDao postDao;
    private final GroupDao groupDao;
    private final GroupMembershipDao groupMembershipDao;
    private final UserDao userDao;
    private final CommentDao commentDao;
    private final HiddenFromDao hiddenFromDao;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final GroupService groupService;
    private final AuthService authService;
    private final PostGenService postGenService;
    private final ImageService imageService;

    public PostResponse createProfilePost(final String userId, final PostRequest request) {
            log.info("Creating post for user {}", userId);
            final Post post = createPost(userId, request);
            Post saved = postDao.saveAndFlush(post);
            PostResponse postResponse = modelMapper.map(saved, PostResponse.class);
            postResponse.setPresignedUrl(imageService.createPresignedUrl(post.getS3FileKey()));
            return postResponse;

    }

    @Transactional
    public PostResponse createPostWithinGroup(final String userId, final PostRequest request) {
        try {
            log.info("Creating post for user {} in group {}: {}", userId, request.getGroupId(), request.toString());
            final Group group = groupDao.findGroupById(request.getGroupId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ERROR_GETTING_GROUP, "Group does not exist"));
            if (!isMemberOfPostGroup(userId, request.getGroupId())) {
                throw new BusinessException(
                        ErrorCode.ERROR_ACCESS_DENIED,
                        "User does not have privilege to create post in this group");
            }
            final Post post = createPost(userId, request);
            post.setGroup(group);
            post.setPrivate(group.isPrivate());
            postDao.saveAndFlush(post);

            log.info("Notifying other group members about new post via email");
            final List<MemberResponse> groupMembers = groupService.findMembersOfGroup(userId, group.getId());
            groupMembers.removeIf(member -> member.getUsername().equals(authService.getCurrentUser().getUsername()));
            emailService.notifyGroupMembersAboutNewPost(post, group, groupMembers);

            return modelMapper.map(post, PostResponse.class);
        } catch (final Exception e) {
            throw new BusinessException(ErrorCode.ERROR_CREATING_POST, e.getMessage());
        }
    }

    public void delete(final String userId, final Long id) {
        final Post post = postDao.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_POST, "Post does not exist."));

        if (post.getCreatedBy().getId().equals(userId)) {
            postDao.delete(post);
        } else if (post.getGroup() != null) {
            if (post.getGroup().getAdmin().getId().equals(userId)) {
                postDao.delete(post);
            } else {
                throw new AccessDeniedException("User does not have rights to delete this post");
            }
        }
    }

    public PostResponse getById(final String userId, final Long id) {
        try {
            final Optional<HiddenFrom> hiddenFrom = hiddenFromDao.findByUserAndPostId(userId, id);
            if (hiddenFrom.isPresent()) {
                throw new BusinessException("Post does not exist.");
            }
            final Post post = postDao.findById(id)
                    .orElseThrow(() -> new BusinessException("Post does not exist."));
            if (post.getGroup() != null && !isMemberOfPostGroup(userId, post.getGroup().getId())) {
                throw new BusinessException("Post does not exist");
            }
            return modelMapper.map(post, PostResponse.class);
        } catch (final Exception e) {
            throw new BusinessException(ErrorCode.ERROR_GETTING_POST, e.getMessage());
        }
    }

    public CommentResponse createComment(final String userId, final Long postId, final CommentRequest request) {
        try {
            log.info(
                    "DefaultPostService: Creating comment, userId:{%s}, with body: {%s}, for post {%d}".formatted(
                            userId,
                            request.toString(),
                            postId));
            final User user = userDao.findById(userId)
                    .orElseThrow(() -> new BusinessException("User does not exist."));

            final Post post = postDao.findById(postId)
                    .orElseThrow(() -> new BusinessException("Post does not exist."));
            final Comment comment = new Comment();
            comment.setContent(request.getContent());
            comment.setUser(user);
            comment.setPost(post);
            commentDao.saveAndFlush(comment);

            return modelMapper.map(comment, CommentResponse.class);
        } catch (final Exception e) {
            throw new BusinessException(ErrorCode.ERROR_CREATING_COMMENT, e.getMessage());
        }
    }

    public CommentResponse createCommentReply(
            final String userId,
            final Long postId,
            final Long commentId,
            final CommentRequest request) {
        try {
            log.info(
                    "DefaultPostService: Creating reply, userId:{%s}, with body: {%s}, for comment {%d}".formatted(
                            userId,
                            request.toString(),
                            commentId));
            final User user = userDao.findById(userId)
                    .orElseThrow(() -> new BusinessException("User does not exist."));

            final Post post = postDao.findById(postId)
                    .orElseThrow(() -> new BusinessException("Post does not exist."));
            final Comment parent = commentDao.findById(commentId)
                    .orElseThrow(() -> new BusinessException("Comment does not exist."));

            if (!Objects.equals(parent.getPost().getId(), postId)) {
                throw new BusinessException("Post id doesn't match with parent comments post id");
            }

            final Comment comment = new Comment();
            comment.setContent(request.getContent());
            comment.setUser(user);
            comment.setPost(post);
            comment.setParent(parent);
            commentDao.saveAndFlush(comment);
            return modelMapper.map(comment, CommentResponse.class);
        } catch (final Exception e) {
            throw new BusinessException(ErrorCode.ERROR_REPLYING_ON_COMMENT, e.getMessage());
        }
    }

    public void deleteComment(final String userId, final Long postId, final Long commentId) {
        log.info("DefaultPostService: Deleting comment, userId:{%s}, comment {%d}".formatted(userId, commentId));

        final Comment comment = commentDao.findByPost_IdAndId(postId, commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ERROR_DELETING_COMMENT, "Comment does not exist."));
        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new AccessDeniedException("User does not have rights to delete this comment");
        }
        commentDao.delete(comment);
    }

    public PostResponse generatePost(String topic) {
        Image image = postGenService.generateImage(topic);
        String content = postGenService.generateContent(topic);
        return createProfilePost(getCurrentUserId(), new PostRequest(content, "", null, image.getUrl()));
    }

    @Transactional
    @Scheduled(fixedRateString = "${FIXED_RATE_SCHEDULER:86400000}")
    public void deleteExpiredPost() {
        postDao.deleteExpiredPost(LocalDateTime.now());
    }

    public void hidePost(final Long id, final String userId, final String ownerId) {
        final User user = userDao.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ERROR_HIDING_POST, "User does not exist."));
        final Post post = postDao.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ERROR_HIDING_POST, "Post does not exist."));

        if (!post.getCreatedBy().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.ERROR_HIDING_POST, "User who wants to hide post is not its creator.");
        }

        if (userId.equals(ownerId)) {
            throw new BusinessException(ErrorCode.ERROR_HIDING_POST, "Post cannot be hidden from its creator.");
        }

        if (hiddenFromDao.findByUserAndPostId(userId, id).isPresent()) {
            throw new BusinessException(ErrorCode.ERROR_HIDING_POST, "Post is already hidden from given user.");
        }

        final HiddenFrom hiddenFrom = new HiddenFrom();
        hiddenFrom.setPost(post);
        hiddenFrom.setUser(user);
        hiddenFromDao.save(hiddenFrom);
    }

    protected boolean isMemberOfPostGroup(final String userId, final Long groupId) {
        final Optional<GroupMembership> membership = groupMembershipDao.findByUserIdAndGroupId(userId, groupId);
        return membership.isPresent();
    }

    protected Post createPost(final String userId, final PostRequest request) {
        final User user = userDao.findById(userId)
                .orElseThrow(() -> new BusinessException("User does not exist."));

        final Post post = new Post();
        post.setContent(request.getContent());
        post.setPrivate(request.getVisibility().equals("PRIVATE"));
        post.setCreatedBy(user);
        S3Resource save = imageService.save(imageService.download(request.getImageUrl()));
        post.setS3FileKey(save.getFilename());

        return post;
    }

    private String getCurrentUserId() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        final Jwt principal = (Jwt) authentication.getPrincipal();
        return principal.getClaimAsString("sub");
    }
}
