package com.esprit.pi.repositories;

import com.esprit.pi.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post,Long> {
    @EntityGraph(attributePaths = {"postedBy", "hackathon", "comments", "likes"})
    Page<Post> findByHackathonIdOrderByCreatedAtDesc(@Param("hackathonId") Long hackathonId, Pageable pageable);
    List<Post> findAllByOrderByCreatedAtDesc();

}
