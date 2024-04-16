package gov.legmt.notifications.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailDto {

    private String committeeName;
    private String billNumber;
    private LocalDateTime meetingDate;
    private LocalDateTime  newMeetingDate;
    private String zoomLink;
    private List<String> recipients;
    private String testimonyLink;
}

