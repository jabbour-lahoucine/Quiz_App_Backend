package com.example.quiz_app_backend.services;

import com.example.quiz_app_backend.dto.AnswerDTO;
import com.example.quiz_app_backend.dto.LeaderboardEntryDTO;
import com.example.quiz_app_backend.dto.PerformanceReportDTO;
import com.example.quiz_app_backend.dto.QuizDTO;
import com.example.quiz_app_backend.entities.*;
import com.example.quiz_app_backend.enums.Difficulty;
import com.example.quiz_app_backend.enums.QuestionType;
import com.example.quiz_app_backend.enums.TypeQuiz;
import com.example.quiz_app_backend.enums.Visibility;
import com.example.quiz_app_backend.repositories.*;
import com.example.quiz_app_backend.util.AccessCodeGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private UserQuizStatsRepository userQuizStatsRepository;

    @Transactional
    public Quiz createQuiz(Long userId, QuizDTO quizDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(quizDTO.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        quiz.setDifficulty(Difficulty.valueOf(quizDTO.getDifficulty()));
        quiz.setVisibility(Visibility.valueOf(quizDTO.getVisibility()));
        quiz.setAccessCode(quizDTO.getAccessCode());
        quiz.setTimeLimit(quizDTO.getTimeLimit());
        quiz.setMaxAttempts(quizDTO.getMaxAttempts());
        quiz.setType(TypeQuiz.valueOf(quizDTO.getType()));
        quiz.setCreator(user);
        quiz.setCategory(category);

        List<Question> questions = quizDTO.getQuestions().stream().map(questionDTO -> {
            Question question = new Question();
            question.setContent(questionDTO.getContent());
            question.setType(QuestionType.valueOf(questionDTO.getType()));
            question.setQuiz(quiz);

            List<Answer> answers = questionDTO.getOptions().stream().map(answerDTO -> {
                Answer answer = new Answer();
                answer.setContent(answerDTO.getContent());
                answer.setCorrect(answerDTO.isCorrect());
                answer.setQuestion(question);
                return answer;
            }).collect(Collectors.toList());

            question.setOptions(answers);
            return question;
        }).collect(Collectors.toList());

        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }
    @Transactional
    public Quiz updateQuiz(Long id, Quiz updatedQuiz) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));

        existingQuiz.setTitle(updatedQuiz.getTitle());
        existingQuiz.setDescription(updatedQuiz.getDescription());
        existingQuiz.setDifficulty(updatedQuiz.getDifficulty());
        existingQuiz.setVisibility(updatedQuiz.getVisibility());
        existingQuiz.setAccessCode(updatedQuiz.getAccessCode());
        existingQuiz.setTimeLimit(updatedQuiz.getTimeLimit());
        existingQuiz.setMaxAttempts(updatedQuiz.getMaxAttempts());
        existingQuiz.setType(updatedQuiz.getType());
        existingQuiz.setCategory(updatedQuiz.getCategory());

        List<Question> existingQuestions = existingQuiz.getQuestions();
        List<Question> updatedQuestions = updatedQuiz.getQuestions();

        for (Question updatedQuestion : updatedQuestions) {
            if (updatedQuestion.getId() == null) {
                updatedQuestion.setQuiz(existingQuiz);
                existingQuestions.add(updatedQuestion);
            } else {
                Question existingQuestion = existingQuestions.stream()
                        .filter(q -> q.getId().equals(updatedQuestion.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + updatedQuestion.getId()));

                existingQuestion.setContent(updatedQuestion.getContent());
                existingQuestion.setType(updatedQuestion.getType());

                List<Answer> existingAnswers = existingQuestion.getOptions();
                List<Answer> updatedAnswers = updatedQuestion.getOptions();

                for (Answer updatedAnswer : updatedAnswers) {
                    if (updatedAnswer.getId() == null) {
                        updatedAnswer.setQuestion(existingQuestion);
                        existingAnswers.add(updatedAnswer);
                    } else {
                        Answer existingAnswer = existingAnswers.stream()
                                .filter(a -> a.getId().equals(updatedAnswer.getId()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Answer not found with id: " + updatedAnswer.getId()));

                        existingAnswer.setContent(updatedAnswer.getContent());
                        existingAnswer.setCorrect(updatedAnswer.isCorrect());
                        existingAnswer.setSelected(updatedAnswer.isSelected());
                    }
                }

                existingAnswers.removeIf(answer -> updatedAnswers.stream()
                        .noneMatch(a -> a.getId() != null && a.getId().equals(answer.getId())));
            }
        }

        existingQuestions.removeIf(question -> updatedQuestions.stream()
                .noneMatch(q -> q.getId() != null && q.getId().equals(question.getId())));

        return quizRepository.save(existingQuiz);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));

        quizRepository.delete(quiz);
    }


    @Transactional
    public Quiz participateQuizByAccessCode(String accessCode) {
        return quizRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with access code: " + accessCode));
    }

    @Transactional
    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));
    }

    @Transactional
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<Quiz> getQuizzesByCategory(Long categoryId) {
        return quizRepository.findByCategory_Id(categoryId);
    }

    @Transactional
    public void startQuiz(Long userId, Long quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        UserQuizStats userQuizStats = new UserQuizStats();
        userQuizStats.setUser(user);
        userQuizStats.setQuiz(quiz);
        userQuizStats.setStartTime(new Date());

        userQuizStatsRepository.save(userQuizStats);
    }

    @Transactional
    public int submitQuiz(Long userId, Long quizId, List<AnswerDTO> submittedAnswers) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        int score = 0;
        for (AnswerDTO submittedAnswer : submittedAnswers) {
            Answer answer = answerRepository.findById(submittedAnswer.getId())
                    .orElseThrow(() -> new RuntimeException("Answer not found"));
            if (answer.isCorrect() == submittedAnswer.isCorrect()) {
                score++;
            }
        }

        UserQuizStats userQuizStats = userQuizStatsRepository.findByUserAndQuiz(user, quiz)
                .orElseThrow(() -> new RuntimeException("Quiz start time not found"));
        userQuizStats.setScore(score);
        userQuizStats.setTimeTaken(calculateTimeTaken(userQuizStats.getStartTime(), new Date())); // Calculate time taken
        userQuizStats.setAttempts(userQuizStats.getAttempts() + 1);
        userQuizStats.setAttemptDate(new Date());
        userQuizStats.setEndTime(new Date()); // Set end time to current date

        userQuizStatsRepository.save(userQuizStats);

        return score;
    }

    private int calculateTimeTaken(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return 0;
        }
        long differenceInMillis = endTime.getTime() - startTime.getTime();
        return (int) (differenceInMillis / 1000); // Convert milliseconds to seconds
    }

    //the Ranking of users in a specific quiz
    public List<LeaderboardEntryDTO> getLeaderboard(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<UserQuizStats> stats = userQuizStatsRepository.findByQuizOrderByScoreDesc(quiz);

        return stats.stream().map(LeaderboardEntryDTO::new).collect(Collectors.toList());
    }

    //return the public quizzes only for the bibliotheque of quizzes
    public List<Quiz> getPublicQuizzes() {
        return quizRepository.findByVisibility(Visibility.PUBLIC);
    }

    //return the rapport about the performance of the user in the app
    @Transactional
    public PerformanceReportDTO getPerformanceReport(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<UserQuizStats> userQuizStatsList = userQuizStatsRepository.findByUser(user);

        List<PerformanceReportDTO.QuizPerformanceDTO> quizPerformances = userQuizStatsList.stream().map(stats -> {
            PerformanceReportDTO.QuizPerformanceDTO quizPerformance = new PerformanceReportDTO.QuizPerformanceDTO();
            quizPerformance.setQuizTitle(stats.getQuiz().getTitle());
            quizPerformance.setScore(stats.getScore());
            quizPerformance.setTimeTaken(stats.getTimeTaken());
            quizPerformance.setAttemptDate(stats.getAttemptDate());

            List<PerformanceReportDTO.QuestionPerformanceDTO> questionPerformances = stats.getQuiz().getQuestions().stream().map(question -> {
                PerformanceReportDTO.QuestionPerformanceDTO questionPerformance = new PerformanceReportDTO.QuestionPerformanceDTO();
                questionPerformance.setQuestionContent(question.getContent());
                questionPerformance.setCorrect(question.getOptions().stream().anyMatch(answer -> answer.isCorrect() && answer.isSelected()));

                return questionPerformance;
            }).collect(Collectors.toList());

            quizPerformance.setQuestionPerformances(questionPerformances);

            return quizPerformance;
        }).collect(Collectors.toList());

        PerformanceReportDTO report = new PerformanceReportDTO();
        report.setUsername(user.getUsername());
        report.setQuizPerformances(quizPerformances);

        return report;
    }
}
