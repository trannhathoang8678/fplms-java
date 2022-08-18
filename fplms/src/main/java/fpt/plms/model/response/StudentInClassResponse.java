package fpt.plms.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fpt.plms.model.dto.StudentDTO;

@Getter
@Setter
@NoArgsConstructor
public class StudentInClassResponse extends StudentDTO {
    Integer groupId;
    Integer groupNumber;
    Boolean isLeader;
}
