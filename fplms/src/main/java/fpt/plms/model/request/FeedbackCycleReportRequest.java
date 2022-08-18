package fpt.plms.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FeedbackCycleReportRequest {
    String feedback;
    Integer groupId;
    Integer reportId;
    Float mark;
}
