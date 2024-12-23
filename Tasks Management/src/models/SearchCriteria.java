package models;

import java.util.Date;

public class SearchCriteria {
    private String keyword;
    private Date startDate;
    private Date endDate;
    private Task.ImportanceLevel importance;
    private Category category;
    private Boolean isCompleted;

    private SearchCriteria() {} // Private constructor for Builder pattern

    public String getKeyword() { return keyword; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public Task.ImportanceLevel getImportance() { return importance; }
    public Category getCategory() { return category; }
    public Boolean getIsCompleted() { return isCompleted; }

    public static class Builder {
        private final SearchCriteria criteria;

        public Builder() {
            criteria = new SearchCriteria();
        }

        public Builder withKeyword(String keyword) {
            criteria.keyword = keyword;
            return this;
        }

        public Builder withDateRange(Date start, Date end) {
            criteria.startDate = start;
            criteria.endDate = end;
            return this;
        }

        public Builder withImportance(Task.ImportanceLevel importance) {
            criteria.importance = importance;
            return this;
        }

        public Builder withCategory(Category category) {
            criteria.category = category;
            return this;
        }

        public Builder withCompletionStatus(Boolean isCompleted) {
            criteria.isCompleted = isCompleted;
            return this;
        }

        public SearchCriteria build() {
            return criteria;
        }
    }
}