package com.aidy.expense.dto;

import java.util.Objects;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailResponseBody {
  private String tnxSource;
  private String tnxAmount;
  private String tnxId;
  private String tnxDate;
  private String tnxDetails;
  private String tnxCategory;

  @JsonIgnore
  public boolean isNotValidData() {
    boolean invalidSource = ObjectUtils.isEmpty(tnxSource) || !tnxSource.matches(".*[A-Za-z].*");
    return ObjectUtils.isEmpty(tnxAmount) || ObjectUtils.isEmpty(tnxDate)
        || invalidSource;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    EmailResponseBody that = (EmailResponseBody) o;
    return Objects.equals(tnxSource, that.tnxSource) && Objects.equals(tnxAmount, that.tnxAmount)
        && Objects.equals(tnxId, that.tnxId) && Objects.equals(tnxDate, that.tnxDate)
        && Objects.equals(tnxDetails, that.tnxDetails)
        && Objects.equals(tnxCategory, that.tnxCategory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tnxSource, tnxAmount, tnxId, tnxDate, tnxDetails, tnxCategory);
  }
}
