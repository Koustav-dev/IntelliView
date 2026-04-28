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

        // Problem 8 - Maximum Subarray
        problems.add(Problem.builder()
                .title("Maximum Subarray")
                .slug("maximum-subarray")
                .description("Given an integer array `nums`, find the subarray with the largest sum, and return its sum.")
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Dynamic Programming")
                .tags(Arrays.asList("Array", "Divide and Conquer", "Dynamic Programming"))
                .companies(Arrays.asList("Amazon", "Microsoft", "Google", "Apple"))
                .constraints("- 1 <= nums.length <= 10^5\n- -10^4 <= nums[i] <= 10^4")
                .examples(List.of(Map.of("input", "nums = [-2,1,-3,4,-1,2,1,-5,4]", "output", "6", "explanation", "The subarray [4,-1,2,1] has the largest sum 6.")))
                .hints(Arrays.asList("Think about Kadane's algorithm.", "At each step, decide: start a new subarray or extend the current one."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public int maxSubArray(int[] nums) {\n        \n    }\n}", "python", "class Solution:\n    def maxSubArray(self, nums: List[int]) -> int:\n        pass"))
                .timeComplexity("O(n)").spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("50.3"))
                .build());

        // Problem 9 - Merge Two Sorted Lists
        problems.add(Problem.builder()
                .title("Merge Two Sorted Lists")
                .slug("merge-two-sorted-lists")
                .description("Merge two sorted linked lists and return it as a sorted list. The list should be made by splicing together the nodes of the first two lists.")
                .difficulty(Problem.Difficulty.EASY)
                .category("Linked List")
                .tags(Arrays.asList("Linked List", "Recursion"))
                .companies(Arrays.asList("Amazon", "Microsoft", "Google", "Apple", "TCS"))
                .constraints("- The number of nodes in both lists is in the range [0, 50].\n- -100 <= Node.val <= 100")
                .examples(List.of(Map.of("input", "l1 = [1,2,4], l2 = [1,3,4]", "output", "[1,1,2,3,4,4]")))
                .hints(Arrays.asList("Use a dummy head node.", "Compare current nodes and advance the pointer of the smaller one."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {\n        \n    }\n}", "python", "class Solution:\n    def mergeTwoLists(self, list1, list2):\n        pass"))
                .timeComplexity("O(n+m)").spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("62.8"))
                .build());

        // Problem 10 - Best Time to Buy and Sell Stock
        problems.add(Problem.builder()
                .title("Best Time to Buy and Sell Stock")
                .slug("best-time-to-buy-and-sell-stock")
                .description("You are given an array `prices` where `prices[i]` is the price of a given stock on the i-th day. You want to maximize your profit by choosing a single day to buy and a single day to sell. Return the maximum profit you can achieve.")
                .difficulty(Problem.Difficulty.EASY)
                .category("Sliding Window")
                .tags(Arrays.asList("Array", "Dynamic Programming"))
                .companies(Arrays.asList("Amazon", "Facebook", "Microsoft", "Google", "Goldman Sachs"))
                .constraints("- 1 <= prices.length <= 10^5\n- 0 <= prices[i] <= 10^4")
                .examples(List.of(Map.of("input", "prices = [7,1,5,3,6,4]", "output", "5", "explanation", "Buy on day 2 (price = 1) and sell on day 5 (price = 6), profit = 6-1 = 5.")))
                .hints(Arrays.asList("Track the minimum price seen so far.", "At each step, calculate profit if sold today."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public int maxProfit(int[] prices) {\n        \n    }\n}", "python", "class Solution:\n    def maxProfit(self, prices: List[int]) -> int:\n        pass"))
                .timeComplexity("O(n)").spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("54.2"))
                .build());

        // Problem 11 - Maximum Depth of Binary Tree
        problems.add(Problem.builder()
                .title("Maximum Depth of Binary Tree")
                .slug("maximum-depth-of-binary-tree")
                .description("Given the root of a binary tree, return its maximum depth. A binary tree's maximum depth is the number of nodes along the longest path from the root node down to the farthest leaf node.")
                .difficulty(Problem.Difficulty.EASY)
                .category("Trees")
                .tags(Arrays.asList("Tree", "DFS", "BFS", "Binary Tree"))
                .companies(Arrays.asList("Amazon", "Google", "Microsoft", "TCS"))
                .constraints("- The number of nodes in the tree is in the range [0, 10^4].\n- -100 <= Node.val <= 100")
                .examples(List.of(Map.of("input", "root = [3,9,20,null,null,15,7]", "output", "3")))
                .hints(Arrays.asList("Use recursion: depth = 1 + max(left, right).", "Base case: null node returns 0."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public int maxDepth(TreeNode root) {\n        \n    }\n}", "python", "class Solution:\n    def maxDepth(self, root) -> int:\n        pass"))
                .timeComplexity("O(n)").spaceComplexity("O(h)")
                .acceptanceRate(new BigDecimal("73.9"))
                .build());

        // Problem 12 - Climbing Stairs
        problems.add(Problem.builder()
                .title("Climbing Stairs")
                .slug("climbing-stairs")
                .description("You are climbing a staircase. It takes `n` steps to reach the top. Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?")
                .difficulty(Problem.Difficulty.EASY)
                .category("Dynamic Programming")
                .tags(Arrays.asList("Math", "Dynamic Programming", "Memoization"))
                .companies(Arrays.asList("Amazon", "Google", "Microsoft", "Apple", "TCS"))
                .constraints("- 1 <= n <= 45")
                .examples(List.of(Map.of("input", "n = 3", "output", "3", "explanation", "1+1+1, 1+2, 2+1")))
                .hints(Arrays.asList("This is a Fibonacci-like problem.", "dp[i] = dp[i-1] + dp[i-2]"))
                .solutionTemplate(Map.of("java", "class Solution {\n    public int climbStairs(int n) {\n        \n    }\n}", "python", "class Solution:\n    def climbStairs(self, n: int) -> int:\n        pass"))
                .timeComplexity("O(n)").spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("52.1"))
                .build());

        // Problem 13 - Number of Islands
        problems.add(Problem.builder()
                .title("Number of Islands")
                .slug("number-of-islands")
                .description("Given an `m x n` 2D binary grid which represents a map of '1's (land) and '0's (water), return the number of islands. An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically.")
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Graph")
                .tags(Arrays.asList("Array", "DFS", "BFS", "Union Find", "Matrix"))
                .companies(Arrays.asList("Amazon", "Google", "Facebook", "Microsoft", "Bloomberg"))
                .constraints("- m == grid.length\n- n == grid[i].length\n- 1 <= m, n <= 300\n- grid[i][j] is '0' or '1'")
                .examples(List.of(Map.of("input", "grid = [[\"1\",\"1\",\"0\"],[\"1\",\"1\",\"0\"],[\"0\",\"0\",\"1\"]]", "output", "2")))
                .hints(Arrays.asList("Use DFS/BFS to mark all connected land cells.", "Each new unvisited '1' starts a new island."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public int numIslands(char[][] grid) {\n        \n    }\n}", "python", "class Solution:\n    def numIslands(self, grid: List[List[str]]) -> int:\n        pass"))
                .timeComplexity("O(m*n)").spaceComplexity("O(m*n)")
                .acceptanceRate(new BigDecimal("56.7"))
                .build());

        // Problem 14 - 3Sum
        problems.add(Problem.builder()
                .title("3Sum")
                .slug("3sum")
                .description("Given an integer array `nums`, return all the triplets `[nums[i], nums[j], nums[k]]` such that `i != j, i != k, j != k`, and `nums[i] + nums[j] + nums[k] == 0`. The solution set must not contain duplicate triplets.")
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Two Pointers")
                .tags(Arrays.asList("Array", "Two Pointers", "Sorting"))
                .companies(Arrays.asList("Amazon", "Facebook", "Google", "Microsoft", "Apple"))
                .constraints("- 3 <= nums.length <= 3000\n- -10^5 <= nums[i] <= 10^5")
                .examples(List.of(Map.of("input", "nums = [-1,0,1,2,-1,-4]", "output", "[[-1,-1,2],[-1,0,1]]")))
                .hints(Arrays.asList("Sort the array first.", "Fix one element, then use two pointers for the remaining two.", "Skip duplicates to avoid repeated triplets."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public List<List<Integer>> threeSum(int[] nums) {\n        \n    }\n}", "python", "class Solution:\n    def threeSum(self, nums: List[int]) -> List[List[int]]:\n        pass"))
                .timeComplexity("O(n²)").spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("33.7"))
                .build());

        // Problem 15 - Merge Intervals
        problems.add(Problem.builder()
                .title("Merge Intervals")
                .slug("merge-intervals")
                .description("Given an array of `intervals` where `intervals[i] = [start_i, end_i]`, merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.")
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Intervals")
                .tags(Arrays.asList("Array", "Sorting"))
                .companies(Arrays.asList("Amazon", "Google", "Facebook", "Microsoft", "Bloomberg"))
                .constraints("- 1 <= intervals.length <= 10^4\n- intervals[i].length == 2\n- 0 <= start_i <= end_i <= 10^4")
                .examples(List.of(Map.of("input", "intervals = [[1,3],[2,6],[8,10],[15,18]]", "output", "[[1,6],[8,10],[15,18]]")))
                .hints(Arrays.asList("Sort by start time.", "If current interval overlaps with previous, merge them."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public int[][] merge(int[][] intervals) {\n        \n    }\n}", "python", "class Solution:\n    def merge(self, intervals: List[List[int]]) -> List[List[int]]:\n        pass"))
                .timeComplexity("O(n log n)").spaceComplexity("O(n)")
                .acceptanceRate(new BigDecimal("47.3"))
                .build());

        // Problem 16 - Product of Array Except Self
        problems.add(Problem.builder()
                .title("Product of Array Except Self")
                .slug("product-of-array-except-self")
                .description("Given an integer array `nums`, return an array `answer` such that `answer[i]` is equal to the product of all the elements of `nums` except `nums[i]`. You must write an algorithm that runs in O(n) time and without using the division operation.")
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Arrays & Hashing")
                .tags(Arrays.asList("Array", "Prefix Sum"))
                .companies(Arrays.asList("Amazon", "Facebook", "Google", "Apple"))
                .constraints("- 2 <= nums.length <= 10^5\n- -30 <= nums[i] <= 30")
                .examples(List.of(Map.of("input", "nums = [1,2,3,4]", "output", "[24,12,8,6]")))
                .hints(Arrays.asList("Use prefix products from the left.", "Then multiply by suffix products from the right.", "You can do this in-place in the result array."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public int[] productExceptSelf(int[] nums) {\n        \n    }\n}", "python", "class Solution:\n    def productExceptSelf(self, nums: List[int]) -> List[int]:\n        pass"))
                .timeComplexity("O(n)").spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("65.4"))
                .build());

        // Problem 17 - Min Stack
        problems.add(Problem.builder()
                .title("Min Stack")
                .slug("min-stack")
                .description("Design a stack that supports push, pop, top, and retrieving the minimum element in constant time.")
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Stack")
                .tags(Arrays.asList("Stack", "Design"))
                .companies(Arrays.asList("Amazon", "Google", "Microsoft", "Bloomberg"))
                .constraints("- -2^31 <= val <= 2^31 - 1\n- Methods pop, top and getMin will always be called on non-empty stacks.\n- At most 3 * 10^4 calls will be made to push, pop, top, and getMin.")
                .examples(List.of(Map.of("input", "MinStack minStack = new MinStack();\nminStack.push(-2);\nminStack.push(0);\nminStack.push(-3);\nminStack.getMin(); // return -3\nminStack.pop();\nminStack.top(); // return 0\nminStack.getMin(); // return -2", "output", "[null,null,null,null,-3,null,0,-2]")))
                .hints(Arrays.asList("Keep a parallel stack that tracks the minimum at each level.", "When you push, also push the current min onto the min stack."))
                .solutionTemplate(Map.of("java", "class MinStack {\n    public MinStack() {\n        \n    }\n    public void push(int val) {\n        \n    }\n    public void pop() {\n        \n    }\n    public int top() {\n        \n    }\n    public int getMin() {\n        \n    }\n}", "python", "class MinStack:\n    def __init__(self):\n        pass\n    def push(self, val: int) -> None:\n        pass\n    def pop(self) -> None:\n        pass\n    def top(self) -> int:\n        pass\n    def getMin(self) -> int:\n        pass"))
                .timeComplexity("O(1)").spaceComplexity("O(n)")
                .acceptanceRate(new BigDecimal("52.8"))
                .build());

        // Problem 18 - Coin Change
        problems.add(Problem.builder()
                .title("Coin Change")
                .slug("coin-change")
                .description("You are given an integer array `coins` representing coins of different denominations and an integer `amount` representing a total amount of money. Return the fewest number of coins that you need to make up that amount. If that amount cannot be made up, return -1.")
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Dynamic Programming")
                .tags(Arrays.asList("Array", "Dynamic Programming", "BFS"))
                .companies(Arrays.asList("Amazon", "Google", "Apple", "Goldman Sachs"))
                .constraints("- 1 <= coins.length <= 12\n- 1 <= coins[i] <= 2^31 - 1\n- 0 <= amount <= 10^4")
                .examples(List.of(Map.of("input", "coins = [1,5,11], amount = 11", "output", "1", "explanation", "11 = 11")))
                .hints(Arrays.asList("Use bottom-up DP.", "dp[i] = min coins to make amount i.", "dp[0] = 0, try each coin for each amount."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public int coinChange(int[] coins, int amount) {\n        \n    }\n}", "python", "class Solution:\n    def coinChange(self, coins: List[int], amount: int) -> int:\n        pass"))
                .timeComplexity("O(amount * coins)").spaceComplexity("O(amount)")
                .acceptanceRate(new BigDecimal("42.9"))
                .build());

        // Problem 19 - Trapping Rain Water
        problems.add(Problem.builder()
                .title("Trapping Rain Water")
                .slug("trapping-rain-water")
                .description("Given `n` non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.")
                .difficulty(Problem.Difficulty.HARD)
                .category("Two Pointers")
                .tags(Arrays.asList("Array", "Two Pointers", "Dynamic Programming", "Stack", "Monotonic Stack"))
                .companies(Arrays.asList("Amazon", "Google", "Facebook", "Microsoft", "Goldman Sachs"))
                .constraints("- n == height.length\n- 1 <= n <= 2 * 10^4\n- 0 <= height[i] <= 10^5")
                .examples(List.of(Map.of("input", "height = [0,1,0,2,1,0,1,3,2,1,2,1]", "output", "6")))
                .hints(Arrays.asList("Water at each index = min(maxLeft, maxRight) - height[i].", "Use two pointers from both ends.", "Track maxLeft and maxRight as you go."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public int trap(int[] height) {\n        \n    }\n}", "python", "class Solution:\n    def trap(self, height: List[int]) -> int:\n        pass"))
                .timeComplexity("O(n)").spaceComplexity("O(1)")
                .acceptanceRate(new BigDecimal("60.1"))
                .build());

        // Problem 20 - Word Search
        problems.add(Problem.builder()
                .title("Word Search")
                .slug("word-search")
                .description("Given an `m x n` grid of characters `board` and a string `word`, return `true` if `word` exists in the grid. The word can be constructed from letters of sequentially adjacent cells, where adjacent cells are horizontally or vertically neighboring. The same letter cell may not be used more than once.")
                .difficulty(Problem.Difficulty.MEDIUM)
                .category("Backtracking")
                .tags(Arrays.asList("Array", "Backtracking", "Matrix"))
                .companies(Arrays.asList("Amazon", "Google", "Facebook", "Microsoft"))
                .constraints("- m == board.length\n- n = board[i].length\n- 1 <= m, n <= 6\n- 1 <= word.length <= 15")
                .examples(List.of(Map.of("input", "board = [[\"A\",\"B\",\"C\",\"E\"],[\"S\",\"F\",\"C\",\"S\"],[\"A\",\"D\",\"E\",\"E\"]], word = \"ABCCED\"", "output", "true")))
                .hints(Arrays.asList("Use DFS from each cell as starting point.", "Mark visited cells to avoid reuse.", "Backtrack when the path doesn't match."))
                .solutionTemplate(Map.of("java", "class Solution {\n    public boolean exist(char[][] board, String word) {\n        \n    }\n}", "python", "class Solution:\n    def exist(self, board: List[List[str]], word: str) -> bool:\n        pass"))
                .timeComplexity("O(m*n*4^L)").spaceComplexity("O(L)")
                .acceptanceRate(new BigDecimal("41.2"))
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
