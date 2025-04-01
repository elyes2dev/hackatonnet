package com.esprit.pi.services;

import com.esprit.pi.entities.UserQuizScore;

import java.util.List;

public interface IUserQuizScoreService {

        public List<UserQuizScore> getScoresByQuizId(Long quizId);

        public List<UserQuizScore> getScoresByUserId(Long userId);

        public List<UserQuizScore> getScoresByUserAndWorkshop(Long userId, Long workshopId);

}

