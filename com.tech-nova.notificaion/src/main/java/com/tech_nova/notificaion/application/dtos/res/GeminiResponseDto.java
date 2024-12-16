package com.tech_nova.notificaion.application.dtos.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeminiResponseDto {

  private List<Candidate> candidates;
  private PromptFeedback promptFeedback;

  @Getter
  @Setter
  public static class Candidate {

    private Content content;
    private String finishReason;
    private int index;
    private List<SafetyRating> safetyRatings;

  }

  @Getter
  @Setter
  @ToString
  public static class Content {

    private List<Parts> parts;
    private String role;

  }

  @Getter
  @Setter
  @ToString
  public static class Parts {

    private String text;

  }

  @Getter
  @Setter
  public static class SafetyRating {

    private String category;
    private String probability;
  }

  @Getter
  @Setter
  public static class PromptFeedback {

    private List<SafetyRating> safetyRatings;
  }
}