package plms.ManagementService.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private Integer id;
    private Integer number;
    private Integer memberQuantity;
    private Timestamp enrollTime;
    private ProjectDTO projectDTO;
}
