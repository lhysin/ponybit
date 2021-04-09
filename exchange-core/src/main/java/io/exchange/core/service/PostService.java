package io.exchange.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import io.exchange.core.dto.CommonDto;
import io.exchange.core.dto.PostDto;
import io.exchange.core.dto.ManualTransactionDto.ResManualTransaction;
import io.exchange.core.dto.PostDto.ReqPosts;
import io.exchange.core.dto.PostDto.ResPost;
import io.exchange.core.hibernate.repository.PostRepository;
import io.exchange.core.util.CustomPageable;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.PostType;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.common.Post;
import io.exchange.domain.hibernate.common.QPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class PostService {

    private final UserService userService;

    private final PostRepository postRepository;

    public Post getTopN1Post() {
        Page<Post> post = postRepository.findAllByDelDtmIsNullOrderByRegDtmDesc(CustomPageable.of(0, 1));
        if (post.getContent().size() <= 0) {
            return null;
        }
        return post.getContent().get(0);
    }

    public List<Post> getTopNPost(int size) {
        Page<Post> post = postRepository.findAllByDelDtmIsNullOrderByRegDtmDesc(CustomPageable.of(0, size));
        if (post.getContent().size() <= 0) {
            return new ArrayList<>();
        }
        return post.getContent();
    }

    public CommonDto.ResPage<ResPost> getAll(ReqPosts req) {

        QPost qpost = QPost.post;
        QBean<ResPost> select = Projections.bean(ResPost.class,
                qpost.id,
                qpost.title,
                qpost.content,
                qpost.regDtm);

        BooleanBuilder where = new BooleanBuilder();
        where.and(qpost.type.eq(req.getPostType()).and(qpost.delDtm.isNull()));

        Page<ResPost> result = this.postRepository.findAll(select, where, CustomPageable.of(req.getPage(), req.getSize()), qpost.regDtm.desc(), qpost.id.desc());

        CommonDto.ResPage<ResPost> res = CommonDto.ResPage.<ResPost>builder()
                .page(req.getPage())
                .size(req.getSize())
                .list(result.getContent())
                .totalPages(result.getTotalPages())
                .build();

        return res;
    }

    @Transactional
    public Post add(PostDto.ReqAdd req) {

        Post post = Post.builder()
                .userId(req.getUserId())
                .type(req.getType())
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(LocalDateTime.now())
                .title(req.getTitle())
                .content(req.getContent())
                .build();

        postRepository.save(post);

        return post;
    }

    @Transactional
    public void edit(PostDto.ReqEdit req) {

        Post.Pk pk = Post.Pk.builder()
                .id(req.getPostId())
                .type(req.getType())
                .build();

        Post existPost = postRepository.findById(pk)
                .orElseThrow(() -> new BusinessException(Code.POST_NOT_EXISTS));

        if(StringUtils.isNotEmpty(req.getTitle())) {
            existPost.setTitle(req.getTitle());
        }
        if(StringUtils.isNotEmpty(req.getContent())) {
            existPost.setContent(req.getContent());
        }
        if(req.getType() != null) {
            existPost.setType(req.getType());
        }

        existPost.setModDtm(LocalDateTime.now());
    }

    @Transactional
    public void del(PostDto.ReqEdit req) {

        Post.Pk pk = Post.Pk.builder()
                .id(req.getPostId())
                .type(req.getType())
                .build();

        Post existPost = postRepository.findById(pk)
                .orElseThrow(() -> new BusinessException(Code.POST_NOT_EXISTS));

        existPost.setDelDtm(LocalDateTime.now());
    }

    @Transactional
    public ResPost get(Long postId) {

        QPost qpost = QPost.post;
        QBean<ResPost> select = Projections.bean(ResPost.class,
                qpost.id,
                qpost.title,
                qpost.content,
                qpost.regDtm);
        BooleanBuilder where = new BooleanBuilder();
        where.and(qpost.id.eq(postId).and(qpost.delDtm.isNull()));

        ResPost resPost = this.postRepository.findOne(select, where)
                .orElseThrow(() -> new BusinessException(Code.POST_NOT_EXISTS));

        return resPost;
    }
}
