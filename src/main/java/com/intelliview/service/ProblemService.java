package com.intelliview.service;

import com.intelliview.dto.ProblemDTO;
import com.intelliview.model.Problem;
import com.intelliview.model.User;
import com.intelliview.repository.CodeSubmissionRepository;
import com.intelliview.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemService {

    private final ProblemRepository problemRepo;
    private final CodeSubmissionRepository submissionRepo;

    public Page<ProblemDTO> getProblems(int page, int size, String difficulty,
                                         String company, String search, User currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Problem> problems;
        if (difficulty != null && !difficulty.isBlank()) {
            try {
                Problem.Difficulty diff = Problem.Difficulty.valueOf(difficulty.toUpperCase());
                problems = problemRepo.findByDifficultyAndIsActiveTrue(diff, pageable);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid difficulty filter '{}', returning all problems.", difficulty);
                problems = problemRepo.findByIsActiveTrue(pageable);
            }
        } else if (company != null && !company.isBlank()) {
            problems = problemRepo.findByCompany(company, pageable);
        } else if (search != null && !search.isBlank()) {
            problems = problemRepo.searchProblems(search, pageable);
        } else {
            problems = problemRepo.findByIsActiveTrue(pageable);
        }

        return problems.map(p -> mapToDTO(p, currentUser));
    }

    public ProblemDTO getProblem(String slugOrId, User currentUser) {
        Problem problem;
        // Use regex to distinguish UUID from slug — avoids catching our own "not found" exception
        boolean looksLikeUUID = slugOrId != null &&
            slugOrId.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
        if (looksLikeUUID) {
            UUID id = UUID.fromString(slugOrId);
            problem = problemRepo.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Problem not found with id: " + slugOrId));
        } else {
            problem = problemRepo.findBySlug(slugOrId)
                    .orElseThrow(() -> new NoSuchElementException("Problem not found with slug: " + slugOrId));
        }
        return mapToDTO(problem, currentUser);
    }

    public List<Problem> getRandomProblems(String difficulty, int count) {
        return problemRepo.findRandomProblems(count);
    }

    @Transactional
    @CacheEvict(value = "problems", allEntries = true)
    public ProblemDTO createProblem(ProblemDTO dto) {
        String slug = dto.getTitle().toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");

        // Ensure unique slug
        String finalSlug = slug;
        int suffix = 1;
        while (problemRepo.findBySlug(finalSlug).isPresent()) {
            finalSlug = slug + "-" + suffix++;
        }

        Problem problem = Problem.builder()
                .title(dto.getTitle())
                .slug(finalSlug)
                .description(dto.getDescription())
                .difficulty(Problem.Difficulty.valueOf(dto.getDifficulty().toUpperCase()))
                .category(dto.getCategory())
                .tags(dto.getTags() != null ? dto.getTags() : new ArrayList<>())
                .companies(dto.getCompanies() != null ? dto.getCompanies() : new ArrayList<>())
                .constraints(dto.getConstraints())
                .examples(dto.getExamples())
                .hints(dto.getHints() != null ? dto.getHints() : new ArrayList<>())
                .solutionTemplate(dto.getSolutionTemplate())
                .timeComplexity(dto.getTimeComplexity())
                .spaceComplexity(dto.getSpaceComplexity())
                .build();

        problem = problemRepo.save(problem);
        return mapToDTO(problem, null);
    }

    private ProblemDTO mapToDTO(Problem p, User user) {
        boolean solved = false;
        String userStatus = null;

        if (user != null) {
            solved = submissionRepo.existsByUserAndProblemAndStatus(
                    user, p, com.intelliview.model.CodeSubmission.Status.ACCEPTED
            );
            userStatus = solved ? "SOLVED" : "ATTEMPTED";
        }

        return ProblemDTO.builder()
                .id(p.getId().toString())
                .title(p.getTitle())
                .slug(p.getSlug())
                .description(p.getDescription())
                .difficulty(p.getDifficulty().name())
                .category(p.getCategory())
                .tags(p.getTags())
                .companies(p.getCompanies())
                .constraints(p.getConstraints())
                .examples(p.getExamples())
                .hints(p.getHints())
                .solutionTemplate(p.getSolutionTemplate())
                .timeComplexity(p.getTimeComplexity())
                .spaceComplexity(p.getSpaceComplexity())
                .acceptanceRate(p.getAcceptanceRate() != null ? p.getAcceptanceRate().doubleValue() : 0)
                .totalSubmissions(p.getTotalSubmissions())
                .premiumOnly(p.getPremiumOnly())
                .createdAt(p.getCreatedAt())
                .solved(solved)
                .userStatus(userStatus)
                .build();
    }
}
