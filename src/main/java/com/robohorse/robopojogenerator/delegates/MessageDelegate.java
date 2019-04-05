package com.robohorse.robopojogenerator.delegates;

import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.robohorse.robopojogenerator.errors.RoboPluginException;
import groovy.lang.Singleton;

import javax.inject.Inject;

/**
 * Created by vadim on 24.09.16.
 */
@Singleton
public class MessageDelegate {
    private static final String GROUP_DISPLAY = "RoboPOJOGenerator";
    private static final String GROUP_DISPLAY_LOG = "RoboPOJOGenerator LOG";
    private static final NotificationGroup GROUP_DISPLAY_ID_INFO =
            new NotificationGroup(GROUP_DISPLAY, NotificationDisplayType.BALLOON, true);
    private static final NotificationGroup GROUP_DISPLAY_ID_LOG =
            new NotificationGroup(GROUP_DISPLAY_LOG, NotificationDisplayType.NONE, true);

    @Inject
    public MessageDelegate() {
    }

    public void onPluginExceptionHandled(RoboPluginException exception) {
        showMessage(exception.getMessage(), exception.getHeader());
    }

    public void logEventMessage(String message) {
        final Notification notification = GROUP_DISPLAY_ID_LOG
                .createNotification(message, NotificationType.INFORMATION);
        sendNotification(notification);
    }

    public void showSuccessMessage() {
        final Notification notification = GROUP_DISPLAY_ID_INFO
                .createNotification("POJO generation: Success", NotificationType.INFORMATION);
        sendNotification(notification);
    }

    private void sendNotification(final Notification notification) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Project[] projects = ProjectManager.getInstance().getOpenProjects();
                Notifications.Bus.notify(notification, projects[0]);
            }
        });
    }

    public void showMessage(String message, String header) {
        Messages.showDialog(message, header, new String[]{"OK"}, -1, null);
    }
}
