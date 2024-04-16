package gov.legmt.notifications.controller;

import gov.legmt.notifications.dto.EmailDto;
import gov.legmt.notifications.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail")
    public void sendScheduledEmail(@RequestBody EmailDto emailDTO) {
            emailService.sendScheduledEmail(emailDTO);
    }

    @PostMapping("/sendCancelEmail")
    public void sendCancelEmail(@RequestBody EmailDto emailDTO) {
        emailService.sendCancelEmail(emailDTO);
    }


    @PostMapping("/sendRescheduledEmail")
    public void sendRescheduledEmail(@RequestBody EmailDto emailDTO) {
        emailService.sendRescheduledEmail(emailDTO);
    }


}

