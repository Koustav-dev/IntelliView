package com.intelliview.config;

import com.intelliview.model.*;
import com.intelliview.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final ProblemRepository problemRepo;
    private final BehavioralQuestionRepository bqRepo;
    private final CompanyPatternRepository companyRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0) {
            seedAdminUser();
        }
        if (problemRepo.count() == 0) {
            seedProblems();
        }
        if (bqRepo.count() == 0) {
            seedBehavioralQuestions();
        }
        if (companyRepo.count() == 0) {
            seedCompanies();
        }
        log.info("✅ Data seeding complete.");
    }

    private void seedAdminUser() {
        User admin = User.builder()
                .fullName("IntelliView Admin")
                .username("admin")
                .email("admin@intelliview.dev")
                .password(passwordEncoder.encode("Admin@123"))
                .role(User.Role.ADMIN)
                .isVerified(true)
                .experienceLevel(User.ExperienceLevel.EXPERT)
                .build();
        userRepo.save(admin);

        User demo = User.builder()
                .fullName("Demo User")
                .username("demo_user")
                .email("demo@intelliview.dev")
                .password(passwordEncoder.encode("Demo@123"))
                .role(User.Role.USER)
                .isVerified(true)
                .experienceLevel(User.ExperienceLevel.INTERMEDIATE)
                .streakCount(7)
                .totalProblemsSolved(42)
                .totalInterviews(12)
                .build();
        userRepo.save(demo);
        log.info("👤 Seeded admin and demo users.");
    }

    private void seedProblems() {
        List<Problem> problems = new ArrayList<>();

        // Problem 1 - Two Sum
        problems.add(Problem.builder()
                .title("Two Sum")
                .slug("two-sum")
                .description("""
                    Given an array of integers `nums` and an integer `target`, return *indices of the two numbers such that they add up to `target`*.
                    
                    You may assume that each input would have **exactly one solution**, and you may not use the same element twice.
                    
                    You can return the answer in any order.
                    """)
                .difficulty(Problem.Difficulty.EASY)
                .category("Arrays & Hashing")
                .tags(Arrays.asList("Array", "Hash Table"))
                .companies(Arrays.asList("Amazon", "Google", "Microsoft", "Facebook", "Apple"))
                .constraints("""
                    - 2 <= nums.length <= 10^4
                    - -10^9 <= nums[i] <= 10^9
                    - -10^9 <= target <= 10^9
                    - Only one valid answer exists.
                    """)
                .examples(List.of(
                        Map.of("input", "nums = [2,7,11,15], target = 9", "output", "[0,1]", "explanation", "Because nums[0] + nums[1] == 9, we return [0, 1]."),
                        Map.of("input", "nums = [3,2,4], target = 6", "output", "[1,2]", "explanation", "nums[1] + nums[2] == 6"),
                        Map.of("input", "nums = [3,3], target = 6", "output", "[0,1]", "explanation", "The two 3s add to 6.")
                ))
                .hints(Arrays.asList(
                        "A brute force approach is to try every pair — O(n²).",
                        "Can you do better with a hash map? Think complement = target - nums[i].",
                        "Look up each number's complement in a HashMap for O(n) time."
                ))
                .solutionTemplate(Map.of(
                        "java", "class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        // Write your solution here\n        \n    }\n}",
                        "python", "class Solution:\n    def twoSum(self, nums: List[int], target: int) -> List[int]:\n        # Write your solution here\n        pass",
                        "javascript", "/**\n * @param {number[]} nums\n * @param {number} target\n * @return {number[]}\n */\nvar twoSum = function(nums, target) {\n    // Write your solution here\n    \n};",
                        "cpp", "class Solution {\npublic:\n    vector<int> twoSum(vector<int>& nums, int target) {\n        // Write your solution here\n        \n    }\n};"
                ))
                .timeComplexity("O(n)")
                .spaceComplexity("O(n)")
                .acceptanceRate(new BigDecimal("49.5"))
                .build());

        // Problem 2 - Valid Parentheses
        problems.add(Problem.builder()
                .title("Valid Parentheses")
                .slug("valid-parentheses")
                .description("""
                    Given a string `s` containing just the characters `'('`, `')'`, `'{'`, `'}'`, `'['` and `']'`, determine if the input string is valid.
                    
                    An input string is valid if:
                    1. Open brackets must be closed by the same type of brackets.
                    2. Open brackets must be closed in the correct order.
                    3. Every close bracket has a corresponding open bracket of the same type.
                    """)
                .difficulty(Problem.Difficulty.EASY)
                .category("Stack")
                .tags(Arrays.asList("String", "Stack"))
                .companies(Arrays.asList("Amazon", "Facebook", "Google", "Bloomberg"))
                .constraints("""
                    - 1 <= s.length <= 10^4
                    - s consists of parentheses only '()[]{}'.
                    """)
                .examples(List.of(
                        Map.of("input", "s = \"()\"", "output", "true"),
                        Map.of("input", "s = \"()[]{}\"", "output", "true"),
                        Map.of("input", "s = \"(]\"", "output", "false")
                ))
                .hints(Arrays.asList("Use a stack data structure.", "Push opening brackets, pop and match when you see a closing bracket."))
                .solutionTemplate(Map.of(
                        "java", "class Solution {\n    public boolean isValid(String s) {\n        \n    }\n}",
                        "python", "class Solution:\n    def isValid(self, s: str) -> bool:\n        pass",
                        "javascript", "var isValid = function(s) {\n    \n};"
                ))
                .timeComplexity("O(n)")
                .spaceComplexity("O(n)")
                .acceptanceRate(new BigDecimal("40.2"))
                .build());

        // Problem 3 - Reverse Linked List
        problems.add(Problem.builder()
                .title("Reverse Linked List")
                .slug("reverse-linked-list")
                .description("""
                    Given the `head` of a singly linked list, reverse the list, and return *the reversed list*.
                    """)
                .difficulty(Problem.Difficulty.EASY)
                .category("Linked List")
                .tags(Arrays.asList("Linked List", "Recursion"))
                .companies(Arrays.asList("Amazon", "Microsoft", "Apple", "Google", "TCS"))
                .constraints("- The number of nodes in the list is the range [0, 5000].\n- -5000 <= Node.val <= 5000")
                .examples(List.of(
                        Map.of("input", "head = [1,2,3,4,5]", "output", "[5,4,3,2,1]"),
                        Map.of("input", "head = [1,2]", "output", "[2,1]"),
                        Map.of("input", "head = []", "output", "[]")
                ))
                .hints(Arrays.asList("Can you do it iteratively? Use prev/curr/next pointers.", "Can you do it recursively?"))
                .solutionTemplate(Map.of(
                        "java", "class Solution {\n    public ListNode reverseList(ListNode head) {\n        \n    }\n}",
                        "python", "class Solution:\n    def reverseList(self, head: Optional[ListNode]) -> Optional[ListNode]:\n        pass"
                ))
                .timeComplexity("O(n)")
                .spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("73.5"))
                .build());

        // Problem 4 - Binary Search
        problems.add(Problem.builder()
                .title("Binary Search")
                .slug("binary-search")
                .description("""
                    Given an array of integers `nums` which is sorted in ascending order, and an integer `target`, write a function to search `target` in `nums`. If `target` exists, then return its index. Otherwise, return `-1`.
                    
                    You must write an algorithm with `O(log n)` runtime complexity.
                    """)
                .difficulty(Problem.Difficulty.EASY)
                .category("Binary Search")
                .tags(Arrays.asList("Array", "Binary Search"))
                .companies(Arrays.asList("Google", "Amazon", "Facebook", "Microsoft"))
                .constraints("- 1 <= nums.length <= 10^4\n- -10^4 < nums[i], target < 10^4\n- All the integers in nums are unique.\n- nums is sorted in ascending order.")
                .examples(List.of(
                        Map.of("input", "nums = [-1,0,3,5,9,12], target = 9", "output", "4"),
                        Map.of("input", "nums = [-1,0,3,5,9,12], target = 2", "output", "-1")
                ))
                .hints(Arrays.asList("Track left and right boundaries.", "When nums[mid] == target, return mid."))
                .solutionTemplate(Map.of(
                        "java", "class Solution {\n    public int search(int[] nums, int target) {\n        \n    }\n}",
                        "python", "class Solution:\n    def search(self, nums: List[int], target: int) -> int:\n        pass"
                ))
                .timeComplexity("O(log n)")
                .spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("56.2"))
                .build());

        // Problem 5 - Longest Common Subsequence (MEDIUM)
        problems.add(Problem.builder()
                .title("Longest Common Subsequence")
                .slug("longest-common-subsequence")
                .description("""
                    Given two strings `text1` and `text2`, return *the length of their longest common subsequence*. If there is no common subsequence, return `0`.
                    
                    A **subsequence** of a string is a new string generated from the original string with some characters (can be none) deleted without changing the relative order of the remaining characters.
                    
                    A **common subsequence** of two strings is a subsequence that is common to both strings.
                    """)
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Dynamic Programming")
                .tags(Arrays.asList("String", "Dynamic Programming"))
                .companies(Arrays.asList("Amazon", "Google", "Microsoft", "Uber", "Dropbox"))
                .constraints("- 1 <= text1.length, text2.length <= 1000\n- text1 and text2 consist of only lowercase English characters.")
                .examples(List.of(
                        Map.of("input", "text1 = \"abcde\", text2 = \"ace\"", "output", "3", "explanation", "The longest common subsequence is \"ace\" and its length is 3."),
                        Map.of("input", "text1 = \"abc\", text2 = \"abc\"", "output", "3"),
                        Map.of("input", "text1 = \"abc\", text2 = \"def\"", "output", "0")
                ))
                .hints(Arrays.asList("Try a 2D DP approach.", "dp[i][j] = LCS of text1[0..i] and text2[0..j]", "If chars match, dp[i][j] = dp[i-1][j-1] + 1, else max(dp[i-1][j], dp[i][j-1])"))
                .solutionTemplate(Map.of(
                        "java", "class Solution {\n    public int longestCommonSubsequence(String text1, String text2) {\n        \n    }\n}",
                        "python", "class Solution:\n    def longestCommonSubsequence(self, text1: str, text2: str) -> int:\n        pass"
                ))
                .timeComplexity("O(m*n)")
                .spaceComplexity("O(m*n)")
                .acceptanceRate(new BigDecimal("57.1"))
                .build());

        // Problem 6 - HARD: Median of Two Sorted Arrays
        problems.add(Problem.builder()
                .title("Median of Two Sorted Arrays")
                .slug("median-of-two-sorted-arrays")
                .description("""
                    Given two sorted arrays `nums1` and `nums2` of size `m` and `n` respectively, return **the median** of the two sorted arrays.
                    
                    The overall run time complexity should be `O(log (m+n))`.
                    """)
                .difficulty(Problem.Difficulty.HARD)
                .category("Binary Search")
                .tags(Arrays.asList("Array", "Binary Search", "Divide and Conquer"))
                .companies(Arrays.asList("Google", "Amazon", "Apple", "Microsoft", "Adobe"))
                .constraints("- nums1.length == m\n- nums2.length == n\n- 0 <= m <= 1000\n- 0 <= n <= 1000\n- 1 <= m + n <= 2000\n- -10^6 <= nums1[i], nums2[i] <= 10^6")
                .examples(List.of(
                        Map.of("input", "nums1 = [1,3], nums2 = [2]", "output", "2.00000", "explanation", "merged array = [1,2,3] and median is 2."),
                        Map.of("input", "nums1 = [1,2], nums2 = [3,4]", "output", "2.50000", "explanation", "merged array = [1,2,3,4] and median is (2 + 3) / 2 = 2.5.")
                ))
                .hints(Arrays.asList("Brute force: merge and find median — O((m+n)log(m+n)).", "Binary search on the smaller array partition.", "Think about what partition means: everything on left <= everything on right."))
                .solutionTemplate(Map.of(
                        "java", "class Solution {\n    public double findMedianSortedArrays(int[] nums1, int[] nums2) {\n        \n    }\n}",
                        "python", "class Solution:\n    def findMedianSortedArrays(self, nums1: List[int], nums2: List[int]) -> float:\n        pass"
                ))
                .timeComplexity("O(log(min(m,n)))")
                .spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("36.8"))
                .build());

        // Problem 7 - MEDIUM: LRU Cache
        problems.add(Problem.builder()
                .title("LRU Cache")
                .slug("lru-cache")
                .description("""
                    Design a data structure that follows the constraints of a **Least Recently Used (LRU) cache**.
                    
                    Implement the `LRUCache` class:
                    - `LRUCache(int capacity)` Initialize the LRU cache with **positive** size `capacity`.
                    - `int get(int key)` Return the value of the `key` if the key exists, otherwise return `-1`.
                    - `void put(int key, int value)` Update the value of the `key` if the `key` exists. Otherwise, add the `key-value` pair to the cache. If the number of keys exceeds the `capacity` from this operation, **evict** the least recently used key.
                    
                    The functions `get` and `put` must each run in `O(1)` average time complexity.
                    """)
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Design")
                .tags(Arrays.asList("Hash Table", "Linked List", "Design", "Doubly-Linked List"))
                .companies(Arrays.asList("Amazon", "Facebook", "Microsoft", "Google", "Uber", "Flipkart"))
                .constraints("- 1 <= capacity <= 3000\n- 0 <= key <= 10^4\n- 0 <= value <= 10^5\n- At most 2 * 10^5 calls will be made to get and put.")
                .examples(List.of(
                        Map.of("input", "LRUCache lRUCache = new LRUCache(2);\nlRUCache.put(1, 1);\nlRUCache.put(2, 2);\nlRUCache.get(1);\nlRUCache.put(3, 3);\nlRUCache.get(2);\nlRUCache.put(4, 4);\nlRUCache.get(1);\nlRUCache.get(3);\nlRUCache.get(4);",
                                "output", "[null, null, null, 1, null, -1, null, -1, 3, 4]")
                ))
                .hints(Arrays.asList("Use a HashMap + Doubly Linked List.", "Head = most recently used, Tail = least recently used.", "On every get/put, move the node to head."))
                .solutionTemplate(Map.of(
                        "java", "class LRUCache {\n    public LRUCache(int capacity) {\n        \n    }\n    \n    public int get(int key) {\n        \n    }\n    \n    public void put(int key, int value) {\n        \n    }\n}",
                        "python", "class LRUCache:\n    def __init__(self, capacity: int):\n        pass\n    \n    def get(self, key: int) -> int:\n        pass\n    \n    def put(self, key: int, value: int) -> None:\n        pass"
                ))
                .timeComplexity("O(1)")
                .spaceComplexity("O(capacity)")
                .acceptanceRate(new BigDecimal("41.5"))
                .build());

        problemRepo.saveAll(problems);
        log.info("📚 Seeded {} problems.", problems.size());
    }

    private void seedBehavioralQuestions() {
        List<BehavioralQuestion> questions = new ArrayList<>();

        questions.add(BehavioralQuestion.builder()
                .question("Tell me about a time you had to deal with a difficult team member. How did you handle it?")
                .category("Teamwork & Conflict")
                .companies(Arrays.asList("Amazon", "Google", "Microsoft"))
                .difficulty("MEDIUM")
                .keywords(Arrays.asList("conflict", "communication", "resolution", "empathy", "collaboration"))
                .sampleAnswer("Use STAR: Situation - a teammate consistently missed deadlines; Task - ensure project delivery; Action - had a 1:1, understood their blocker, redistributed work; Result - project delivered on time and relationship improved.")
                .build());

        questions.add(BehavioralQuestion.builder()
                .question("Describe a situation where you had to make a decision quickly with incomplete information.")
                .category("Decision Making")
                .companies(Arrays.asList("Amazon", "Google", "Goldman Sachs"))
                .difficulty("MEDIUM")
                .keywords(Arrays.asList("decision making", "ambiguity", "judgment", "data-driven", "risk"))
                .build());

        questions.add(BehavioralQuestion.builder()
                .question("Tell me about your most challenging project. What made it challenging and how did you overcome it?")
                .category("Problem Solving")
                .companies(Arrays.asList("Amazon", "Microsoft", "Facebook", "Apple"))
                .difficulty("MEDIUM")
                .keywords(Arrays.asList("challenge", "problem solving", "persistence", "creativity", "outcome"))
                .build());

        questions.add(BehavioralQuestion.builder()
                .question("Give me an example of a time you showed leadership without having a formal leadership title.")
                .category("Leadership")
                .companies(Arrays.asList("Amazon", "Google", "McKinsey"))
                .difficulty("MEDIUM")
                .keywords(Arrays.asList("leadership", "initiative", "influence", "ownership", "impact"))
                .build());

        questions.add(BehavioralQuestion.builder()
                .question("Describe a time when you failed. What did you learn from it?")
                .category("Growth Mindset")
                .companies(Arrays.asList("Amazon", "Google", "Microsoft", "Meta"))
                .difficulty("EASY")
                .keywords(Arrays.asList("failure", "learning", "growth", "accountability", "resilience"))
                .build());

        questions.add(BehavioralQuestion.builder()
                .question("Tell me about a time you had to prioritize multiple competing deadlines.")
                .category("Time Management")
                .companies(Arrays.asList("Amazon", "TCS", "Infosys", "Wipro"))
                .difficulty("EASY")
                .keywords(Arrays.asList("prioritization", "time management", "planning", "organization", "delivery"))
                .build());

        questions.add(BehavioralQuestion.builder()
                .question("How have you handled disagreement with your manager about a technical decision?")
                .category("Communication")
                .companies(Arrays.asList("Google", "Netflix", "Stripe"))
                .difficulty("HARD")
                .keywords(Arrays.asList("disagreement", "diplomacy", "data", "communication", "respect"))
                .build());

        bqRepo.saveAll(questions);
        log.info("💬 Seeded {} behavioral questions.", questions.size());
    }

    private void seedCompanies() {
        List<CompanyPattern> companies = new ArrayList<>();

        companies.add(CompanyPattern.builder()
                .companyName("Amazon")
                .description("Amazon focuses heavily on Leadership Principles and System Design. Known for high volume of DSA questions across all rounds.")
                .interviewRounds(List.of(
                        Map.of("round", 1, "name", "Online Assessment", "description", "2 DSA questions + debugging"),
                        Map.of("round", 2, "name", "Technical Screen", "description", "1 DSA + LP questions"),
                        Map.of("round", 3, "name", "Virtual Onsite", "description", "4 rounds: DSA, System Design, LP deep-dive, Bar Raiser")
                ))
                .preferredLanguages(Arrays.asList("Java", "Python", "C++"))
                .focusAreas(Arrays.asList("Arrays", "Dynamic Programming", "Trees", "System Design", "Leadership Principles"))
                .difficultyDistribution(Map.of("EASY", 20, "MEDIUM", 60, "HARD", 20))
                .tips(Arrays.asList(
                        "Study all 16 Amazon Leadership Principles and have STAR examples for each.",
                        "Practice on LeetCode with Amazon tag — focus on DP and Trees.",
                        "System Design: practice designing DynamoDB, S3, SQS.",
                        "The Bar Raiser can veto — demonstrate high standards in all rounds.",
                        "Clarity matters: explain your thought process out loud."
                ))
                .build());

        companies.add(CompanyPattern.builder()
                .companyName("Google")
                .description("Google emphasizes algorithmic thinking, code quality, and clarity of thought. Hiring Committee review means multiple rounds matter.")
                .interviewRounds(List.of(
                        Map.of("round", 1, "name", "Phone Screen", "description", "1-2 coding problems on Google Docs"),
                        Map.of("round", 2, "name", "Onsite - Coding x3", "description", "Medium-Hard algorithmic problems"),
                        Map.of("round", 3, "name", "Onsite - System Design", "description", "Design a large-scale system"),
                        Map.of("round", 4, "name", "Onsite - Googliness", "description", "Culture fit and leadership")
                ))
                .preferredLanguages(Arrays.asList("Python", "Java", "C++", "Go"))
                .focusAreas(Arrays.asList("Graph Algorithms", "Dynamic Programming", "String Manipulation", "System Design", "Distributed Systems"))
                .difficultyDistribution(Map.of("EASY", 10, "MEDIUM", 50, "HARD", 40))
                .tips(Arrays.asList(
                        "Write very clean, production-ready code — even in interviews.",
                        "Practice BFS/DFS, Dijkstra's, and advanced graph problems.",
                        "System Design: focus on scalability, consistency tradeoffs.",
                        "Explain your reasoning clearly — thought process > getting the answer.",
                        "Strong CS fundamentals: time/space complexity for every solution."
                ))
                .build());

        companies.add(CompanyPattern.builder()
                .companyName("Microsoft")
                .description("Microsoft tests problem-solving, system design, and collaboration skills. Known for being more conversational and mentorship-focused.")
                .interviewRounds(List.of(
                        Map.of("round", 1, "name", "HR Screen", "description", "Behavioral + background"),
                        Map.of("round", 2, "name", "Technical Phone", "description", "1-2 coding problems"),
                        Map.of("round", 3, "name", "Onsite x4", "description", "Coding, System Design, Behavioral, As-Appropriate")
                ))
                .preferredLanguages(Arrays.asList("C#", "Java", "Python", "C++"))
                .focusAreas(Arrays.asList("Trees", "Graphs", "OOP Design", "System Design", "Azure Architecture"))
                .difficultyDistribution(Map.of("EASY", 30, "MEDIUM", 55, "HARD", 15))
                .tips(Arrays.asList(
                        "Show growth mindset — Microsoft values learning agility.",
                        "OOP design patterns are important: Factory, Observer, Strategy.",
                        "Discuss trade-offs actively in system design.",
                        "Behavioral: emphasize collaboration and impact.",
                        "Practice medium difficulty tree and graph problems most."
                ))
                .build());

        companies.add(CompanyPattern.builder()
                .companyName("Meta")
                .description("Meta (Facebook) focuses on product sense, execution speed, and scalability. High emphasis on coding speed and system design at senior levels.")
                .interviewRounds(List.of(
                        Map.of("round", 1, "name", "Recruiter Call", "description", "Background and role alignment"),
                        Map.of("round", 2, "name", "Technical Screen", "description", "2 coding problems in 45 minutes"),
                        Map.of("round", 3, "name", "Onsite - Coding x2", "description", "Medium-Hard problems with optimization"),
                        Map.of("round", 4, "name", "Onsite - System Design", "description", "Large scale system design"),
                        Map.of("round", 5, "name", "Onsite - Behavioral", "description", "Values alignment and past experience")
                ))
                .preferredLanguages(Arrays.asList("Python", "Java", "C++", "Hack"))
                .focusAreas(Arrays.asList("Recursion", "Dynamic Programming", "Graph Theory", "Product Design", "Distributed Systems"))
                .difficultyDistribution(Map.of("EASY", 15, "MEDIUM", 55, "HARD", 30))
                .tips(Arrays.asList(
                        "Speed matters at Meta — practice timed coding sessions.",
                        "Recursion and DP are heavily tested.",
                        "For System Design: Instagram feed, News Feed, Messenger are classic.",
                        "Meta values boldness and impact in behavioral questions.",
                        "Know your Big O instantly — no looking up."
                ))
                .build());

        companies.add(CompanyPattern.builder()
                .companyName("TCS")
                .description("TCS interviews focus on fundamentals, aptitude, and communication. Entry-level hiring with standard patterns.")
                .interviewRounds(List.of(
                        Map.of("round", 1, "name", "TCS NQT / Aptitude", "description", "Quantitative, Verbal, Coding"),
                        Map.of("round", 2, "name", "Technical Interview", "description", "Core CS, Programming, Projects"),
                        Map.of("round", 3, "name", "HR Round", "description", "Salary, relocation, HR questions")
                ))
                .preferredLanguages(Arrays.asList("Java", "C", "Python"))
                .focusAreas(Arrays.asList("Core Java", "DBMS", "OS Concepts", "Data Structures", "OOP"))
                .difficultyDistribution(Map.of("EASY", 60, "MEDIUM", 35, "HARD", 5))
                .tips(Arrays.asList(
                        "Practice TCS NQT pattern — time is critical in aptitude section.",
                        "Be strong in OOPS concepts: inheritance, polymorphism, abstraction.",
                        "Know your final year project inside-out.",
                        "Practice SQL queries and normalization.",
                        "HR round: have clear answers on relocation, salary expectations, career goals."
                ))
                .build());

        companyRepo.saveAll(companies);
        log.info("🏢 Seeded {} company patterns.", companies.size());
    }
}
