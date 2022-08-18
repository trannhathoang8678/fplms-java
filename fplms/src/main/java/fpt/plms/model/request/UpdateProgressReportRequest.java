package fpt.plms.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateProgressReportRequest extends CreateProgressReportRequest{
    private Integer id;
}
