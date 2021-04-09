package io.exchange.web.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.exchange.core.annotation.RequestQuery;
import io.exchange.core.dto.CommonDto.ResPage;
import io.exchange.core.dto.PostDto.ReqAdd;
import io.exchange.core.dto.PostDto.ReqEdit;
import io.exchange.core.dto.PostDto.ReqPosts;
import io.exchange.core.dto.PostDto.ResPost;
import io.exchange.core.service.PostService;
import io.exchange.domain.enums.PostType;
import io.exchange.web.service.CustomValidationService;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final CustomValidationService customValidationService;
    private final PostService postService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> getAllResPostsByReq(@RequestQuery ReqPosts req) {

        // forced
        req.setPostType(PostType.NOTICE);

        customValidationService.validationObject(req);

        ResPage<ResPost> res = this.postService.getAll(req);

        return StaticWebUtils.defaultSuccessResponseEntity(res);
    }

    @GetMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<Object> getResPostByReq(@PathVariable("postId") Long postId) {
        return StaticWebUtils.defaultSuccessResponseEntity(this.postService.get(postId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void addPost(@RequestBody ReqAdd req) {

        req.setType(PostType.NOTICE);
        customValidationService.validationObject(req);

        this.postService.add(req);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void editPost(@RequestBody ReqEdit req) {

        customValidationService.validationObject(req);

        this.postService.edit(req);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(@RequestBody ReqEdit req) {

        customValidationService.validationObject(req);

        this.postService.del(req);
    }
}
