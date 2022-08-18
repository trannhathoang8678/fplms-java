package fpt.plms.model.request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateCycleReportRequest {
	private String title;
	private String content;
	private String resourceLink;
	private Integer groupId;

}
