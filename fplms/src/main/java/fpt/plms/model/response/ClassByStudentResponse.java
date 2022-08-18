package fpt.plms.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fpt.plms.model.dto.LecturerDTO;

@Getter
@Setter
@NoArgsConstructor
public class ClassByStudentResponse {
    private Integer id;
    private String name;
    private String semesterCode;
    private Boolean hasEnrollKey;
    private Integer subjectId;
    private LecturerDTO lecturerDto;
    private boolean isJoin;
}
