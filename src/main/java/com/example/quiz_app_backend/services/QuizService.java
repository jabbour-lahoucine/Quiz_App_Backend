package com.example.quiz_app_backend.services;

import com.example.quiz_app_backend.entities.Answer;
import com.example.quiz_app_backend.entities.Question;
import com.example.quiz_app_backend.entities.Quiz;
import com.example.quiz_app_backend.entities.User;
import com.example.quiz_app_backend.repositories.AnswerRepository;
import com.example.quiz_app_backend.repositories.QuestionRepository;
import com.example.quiz_app_backend.repositories.QuizRepository;
import com.example.quiz_app_backend.repositories.UserRepository;
import com.example.quiz_app_backend.util.AccessCodeGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Transactional
    public String createQuiz(Quiz quiz, Long creatorId) {
        // Fetch and set the creator
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        quiz.setCreator(creator);
        String accessCode = AccessCodeGenerator.generateAccessCode();
        quiz.setAccessCode(accessCode);
        // Persist each question and its answers
        List<Question> managedQuestions = new ArrayList<>();
        for (Question question : quiz.getQuestions()) {
            question.setQuiz(quiz); // Set the parent relationship

            List<Answer> managedAnswers = new ArrayList<>();
            for (Answer answer : question.getOptions()) {
                answer.setQuestion(question); // Set the parent relationship
                managedAnswers.add(answer);
            }
            question.setOptions(managedAnswers);
            managedQuestions.add(question);
        }
        quiz.setQuestions(managedQuestions);

        // Save the quiz along with cascaded relationships
        quizRepository.saveAndFlush(quiz);
        return accessCode;
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
        Quiz quiz = quizRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with access code: " + accessCode));
        return quiz;
    }

    @Transactional
    public Quiz getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));
        return quiz;
    }

    @Transactional
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<Quiz> getQuizzesByCategory(Long categoryId) {
        return quizRepository.findByCategory_Id(categoryId);
    }
}
