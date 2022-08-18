package fpt.plms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CycleReportDTO {
	private Integer id;
	private String title;
	private String content;
	private Integer cycleNumber;
	private String feedback;
	private String resourceLink;
	private Float mark;
	private Integer groupId;
	
	@Override
	public String toString() {
		return "CycleReportDTO [id=" + id + ", groupId=" + groupId + "]";
	}
	
}
