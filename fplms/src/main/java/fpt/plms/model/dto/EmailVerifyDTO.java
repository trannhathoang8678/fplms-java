package fpt.plms.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailVerifyDTO {
    String email;
    String role;
}
