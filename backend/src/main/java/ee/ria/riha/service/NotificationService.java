package ee.ria.riha.service;

import ee.ria.riha.conf.ApplicationProperties;
import ee.ria.riha.domain.model.InfoSystem;
import ee.ria.riha.service.notifications.EmailNotificationSenderService;
import ee.ria.riha.service.notifications.model.NewInfoSystemsNotification;
import ee.ria.riha.service.notifications.model.NewIssueCommentNotification;
import ee.ria.riha.service.notifications.model.NewIssueNotification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Getter
@Slf4j
public class NotificationService {

    private final boolean newIssueNotificationEnabled;
    private final boolean newIssueCommentNotificationEnabled;

    private EmailNotificationSenderService emailNotificationSenderService;
    private IssueCommentService issueCommentService;
    private UserService userService;
    private InfoSystemService infoSystemService;

    @Autowired
    public NotificationService(ApplicationProperties applicationProperties) {
        newIssueNotificationEnabled = applicationProperties.getNotification().getNewIssue().isEnabled();
        newIssueCommentNotificationEnabled = applicationProperties.getNotification().getNewIssueComment().isEnabled();
    }

    public void sendNewInfoSystemsNotification(NewInfoSystemsNotification notification) {
        emailNotificationSenderService.sendNotification(notification);
    }

    public void sendNewIssueCommentNotification(Long issueId) {
        if (!isNewIssueCommentNotificationEnabled()) {
            log.warn("New issue comment notifications sending is disabled.");
            return;
        }

        Set<String> authorsPersonalCodes = issueCommentService.getAllAuthorsPersonalCodes(issueId);
        Set<String> emails = userService.getUsersEmailsByUsersIds(authorsPersonalCodes);

        if (emails.isEmpty()) {
            log.info("New issue comment has been recently added for issue with id {}, but none of issue participants have emails.", issueId);
            return;
        }

        InfoSystem infoSystem = infoSystemService.get(issueId);
        NewIssueCommentNotification model = NewIssueCommentNotification.builder()
                .infoSystemFullName(infoSystem.getFullName())
                .infoSystemShortName(infoSystem.getShortName())
                .to(emails.toArray(new String[emails.size()]))
                .build();

        emailNotificationSenderService.sendNotification(model);
    }

    public void sendNewIssueNotification(InfoSystem infoSystem) {
        if (!isNewIssueNotificationEnabled()) {
            log.warn("New issue notifications sending is disabled.");
            return;
        }

        String[] to = infoSystemService.getSystemContacts(infoSystem);
        if (to.length == 0) {
            log.info("New issue has been recently added, but info system '{}' has no contacts.", infoSystem.getFullName());
            return;
        }

        NewIssueNotification model = NewIssueNotification.builder()
                .infoSystemFullName(infoSystem.getFullName())
                .infoSystemShortName(infoSystem.getShortName())
                .to(to)
                .build();

        emailNotificationSenderService.sendNotification(model);
    }

    @Autowired
    public void setEmailNotificationSenderService(EmailNotificationSenderService emailNotificationSenderService) {
        this.emailNotificationSenderService = emailNotificationSenderService;
    }

    @Autowired
    public void setIssueCommentService(IssueCommentService issueCommentService) {
        this.issueCommentService = issueCommentService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setInfoSystemService(InfoSystemService infoSystemService) {
        this.infoSystemService = infoSystemService;
    }
}
