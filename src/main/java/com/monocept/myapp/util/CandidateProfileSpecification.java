package com.monocept.myapp.util;

import com.monocept.myapp.entity.CandidateProfile;
import org.springframework.data.jpa.domain.Specification;

public class CandidateProfileSpecification {

    public static Specification<CandidateProfile> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<CandidateProfile> hasSkill(String skill) {
        return (root, query, criteriaBuilder) ->
                skill == null ? null : criteriaBuilder.like(root.get("skill"), "%" + skill + "%");
    }

    public static Specification<CandidateProfile> hasSubSkill(String subSkill) {
        return (root, query, criteriaBuilder) ->
                subSkill == null ? null : criteriaBuilder.like(root.get("subSkill"), "%" + subSkill + "%");
    }

    public static Specification<CandidateProfile> hasLocation(String location) {
        return (root, query, criteriaBuilder) ->
                location == null ? null : criteriaBuilder.like(root.get("location"), "%" + location + "%");
    }

    public static Specification<CandidateProfile> hasRelevantExperienceBetween(Integer minRelExp, Integer maxRelExp) {
        return (root, query, criteriaBuilder) -> {
            if (minRelExp == null && maxRelExp == null) return null;
            if (minRelExp == null) return criteriaBuilder.lessThanOrEqualTo(root.get("relevantExperience"), maxRelExp);
            if (maxRelExp == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("relevantExperience"), minRelExp);
            return criteriaBuilder.between(root.get("relevantExperience"), minRelExp, maxRelExp);
        };
    }

    public static Specification<CandidateProfile> hasTotalExperienceBetween(Integer minTotalExp, Integer maxTotalExp) {
        return (root, query, criteriaBuilder) -> {
            if (minTotalExp == null && maxTotalExp == null) return null;
            if (minTotalExp == null) return criteriaBuilder.lessThanOrEqualTo(root.get("totalExperience"), maxTotalExp);
            if (maxTotalExp == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("totalExperience"), minTotalExp);
            return criteriaBuilder.between(root.get("totalExperience"), minTotalExp, maxTotalExp);
        };
    }
}
