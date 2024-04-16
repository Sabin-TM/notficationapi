package gov.legmt.notifications.service;

import gov.legmt.notifications.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ObjectToMapConverter objectToMapConverter;

    @Value("${spring.mail.senderEmail}")
    private String senderEmail;
    public static final String SCHEDULED_EMAIL_TEMPLATE = "lpp-scheduled-email-template";
    public static final String CANCEL_EMAIL_TEMPLATE = "lpp-cancel-email-template";
    public static final String RESCHEDULED_EMAIL_TEMPLATE = "lpp-rescheduled-email-template";

    public void sendScheduledEmail(EmailDto emailDto)  {

        try {
            Map<String, Object> map = objectToMapConverter.convert(emailDto);
            this.sendScheduledEmailTemplateImpl(map);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public void sendRescheduledEmail(EmailDto emailDto)  {
        try {
            Map<String, Object> map = objectToMapConverter.convert(emailDto);
            this.sendRescheduledEmailImpl(map);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public void sendCancelEmail(EmailDto emailDto)  {
        try {
            Map<String, Object> map = objectToMapConverter.convert(emailDto);
            this.sendCancelTemplateEmailImpl(map);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    private void sendScheduledEmailTemplateImpl(Map<String, Object> variables) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(getScheduledSubject(variables));
        List<String> recipientEmails = (List<String>) variables.get("recipients");
        helper.setTo(recipientEmails.get(0));
        helper.setFrom(senderEmail);
        Context context = getContext(variables);
        String htmlContent = templateEngine.process(SCHEDULED_EMAIL_TEMPLATE, context);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    private void sendCancelTemplateEmailImpl(Map<String, Object> variables) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(getSubjectForCancel(variables));
        List<String> recipientEmails = (List<String>) variables.get("recipients");
        helper.setTo(recipientEmails.get(0));
        helper.setFrom(senderEmail);
        Context context = getContext(variables);
        String htmlContent = templateEngine.process(CANCEL_EMAIL_TEMPLATE, context);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }
    private void sendRescheduledEmailImpl(Map<String, Object> variables) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(getRescheduledSubject(variables));
        List<String> recipientEmails = (List<String>) variables.get("recipients");
        helper.setTo(recipientEmails.get(0));
        helper.setFrom(senderEmail);
        Context context = getContext(variables);
        String htmlContent = templateEngine.process(RESCHEDULED_EMAIL_TEMPLATE, context);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    private String getScheduledSubject(Map<String, Object> variables){
        String space = " ";
        String testimonyLink = "Testimony Link ";
        String house = "(H) ";
        String subject = null;
        if (variables.get("committeeName") != null) {
            subject = variables.get("committeeName") +space+testimonyLink+variables.get("testimonyLink");
        } else if (variables.get("billNumber") != null) {
            subject = house+variables.get("billNumber") +space+testimonyLink+ variables.get("testimonyLink");
        }
        return subject;
    }

    private String getSubjectForCancel(Map<String, Object> variables){
        String house = "(HB) ";
        String testimonyLink = "Testimony Link ";
        String space = " ";
        String subjectStr = " ";
        if (variables.get("committeeName") != null) {
            subjectStr = variables.get("committeeName") +space+testimonyLink;
        } else if (variables.get("billNumber") != null) {
            subjectStr =  house+variables.get("billNumber") +space+testimonyLink;
        }
        if(variables.get("testimonyLink") != null){
            subjectStr = subjectStr + variables.get("testimonyLink");
        }
        return subjectStr;
    }

    private String getRescheduledSubject(Map<String, Object> variables){
        // Subject: [Bill # OR Committee Name if no Bill] [Hearing OR Meeting] Rescheduled
        String space = " ";
        String rescheduled = "Rescheduled ";
        String house = "(H) ";
        String subject = null;
        if (variables.get("committeeName") != null) {
            subject = variables.get("committeeName") +space+rescheduled;
        } else if (variables.get("billNumber") != null) {
            subject = house+variables.get("billNumber") +space+rescheduled;
        }
        return subject;
    }

    private Context getContext(Map<String, Object> variables){
        Context context = new Context();
        String house = "(HB)";
        context.setVariable("house",house);
        if(variables.get("committeeName") != null){
            context.setVariable("committeeName",(String)variables.get("committeeName"));
        } else if(variables.get("billNumber") != null){
            context.setVariable("billNumber",(String)variables.get("billNumber"));
        }
        if(variables.get("meetingDate") != null){
            context.setVariable("dateTime",(LocalDateTime)variables.get("meetingDate"));
        }
        if(variables.get("newMeetingDate") != null){
            context.setVariable("newDateTime",(LocalDateTime)variables.get("newMeetingDate"));
        }
        context.setVariables(variables);
        return context;
    }



}


