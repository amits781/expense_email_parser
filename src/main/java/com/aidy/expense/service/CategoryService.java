// package com.aidy.expense.service;
//
// import java.util.Map;
// import java.util.stream.Collectors;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;
// import com.aidy.expense.repository.CategoryRuleRepository;
// import jakarta.annotation.PostConstruct;
//
// @Service
// public class CategoryService {
//
// private final CategoryRuleRepository repository;
// private Map<String, String> rulesCache;
//
// public CategoryService(CategoryRuleRepository repository) {
// this.repository = repository;
// }
//
// @PostConstruct
// @Scheduled(fixedRate = 1800000)
// public void loadRules() {
// this.rulesCache = repository.findAll().stream()
// .collect(Collectors.toMap(
// rule -> rule.getKeyword().toUpperCase(),
// rule -> rule.getCategory()
// ));
// }
//
// public Map<String, String> getRulesCache() {
// return rulesCache;
// }
//
// public String predictCategory(String details) {
// if (details == null) return "Misc";
//
// String upperDetails = details.toUpperCase();
//
// return rulesCache.entrySet().stream()
// .filter(entry -> upperDetails.contains(entry.getKey()))
// .map(Map.Entry::getValue)
// .findFirst()
// .orElse("Misc");
// }
// }
