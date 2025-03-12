package com.esprit.pi.services;

import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.entities.Post;
import com.esprit.pi.repositories.IHackathonRepository;
import com.esprit.pi.repositories.IPostRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService implements IPostService{

    @Autowired
    IPostRepository postRepository;

    @Autowired
    IHackathonRepository hackathonRepository;

    @Override
    public List<Post> getAllPosts() {
        return (List<Post>) postRepository.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public Post createPost(Post post) {
        if (post.getHackathon() == null || post.getHackathon().getId() == null) {
            throw new RuntimeException("Hackathon is required for a post.");
        }

        Optional<Hackathon> hackathon = hackathonRepository.findById(post.getHackathon().getId());

        if (hackathon.isEmpty()) {
            throw new RuntimeException("Hackathon with ID " + post.getHackathon().getId() + " not found.");
        }

        post.setHackathon(hackathon.get());
        return postRepository.save(post);
    }



    @Override
    public Post updatePost(Long id, Post post) {
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);

    }
}
