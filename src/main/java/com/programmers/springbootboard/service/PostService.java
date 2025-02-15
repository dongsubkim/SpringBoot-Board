package com.programmers.springbootboard.service;

import com.programmers.springbootboard.dto.PostRequestDto;
import com.programmers.springbootboard.dto.PostResponseDto;
import com.programmers.springbootboard.entity.Post;
import com.programmers.springbootboard.entity.User;
import com.programmers.springbootboard.handler.NotFoundException;
import com.programmers.springbootboard.repository.PostRepository;
import com.programmers.springbootboard.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDto createPost(@NonNull PostRequestDto dto) {
        log.info("createPost called with PostRequestDto: " + dto);
        Assert.notNull(dto.getUsername(), "Username must be provided.");

        User user = userRepository.findByName(dto.getUsername())
                .orElseThrow(() -> new NotFoundException("No User found with username : " + dto.getUsername()));

        return PostResponseDto.of(postRepository.save(Post.of(dto, user)));
    }

    @Transactional(readOnly = true)
    public PostResponseDto readPost(@NonNull Long id) {
        log.info("readPost called with id: " + id);
        Post post = postRepository.findWithUserById(id)
                .orElseThrow(() -> new NotFoundException("No Post found with id: " + id));

        return PostResponseDto.of(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> readPostPage(@NonNull Pageable pageable) {
        log.info("readPostPage called with Pageable offset: " + pageable.getOffset() + ", size: " + pageable.getPageSize());
        Page<Post> postList = postRepository.findAllWithUser(pageable);
        if (!postList.hasContent()) throw new NotFoundException("No Post found");

        return postList.map(PostResponseDto::of);
    }

    @Transactional
    public PostResponseDto updatePost(@NonNull PostRequestDto dto) {
        log.info("updatePost is called with PostRequestDto: " + dto);
        Assert.notNull(dto.getId(), "Post Id must be provided.");

        Post post = postRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("No Post found with id: " + dto.getId()));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        return PostResponseDto.of(post);
    }
}
