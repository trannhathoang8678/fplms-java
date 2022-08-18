package fpt.plms.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationDTO {
    private String title;
    private String url;
    private String userEmail;
}
