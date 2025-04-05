package com.esprit.pi.controllers;

import com.esprit.pi.entities.Question;
import com.esprit.pi.entities.Quiz;
import com.esprit.pi.services.IQuestionService;
import com.esprit.pi.services.IQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {
    @Autowired
    private IQuizService quizService;

    @Autowired
    private IQuestionService questionService;
    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(quiz));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quiz) {
        return ResponseEntity.ok(quizService.updateQuiz(id, quiz));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/workshops/{workshopId}/quizzes")
    public ResponseEntity<List<Quiz>> getQuizzesByWorkshop(@PathVariable Long workshopId) {
        List<Quiz> quizzes = quizService.getQuizzesByWorkshop(workshopId);
        return ResponseEntity.ok(quizzes);
    }


    // Endpoint to get questions by quizId
    @GetMapping("/quiz/{quizId}")
    public List<Question> getQuestionsByQuizId(@PathVariable Long quizId) {
        return questionService.getQuestionsByQuizId(quizId);
    }

}
