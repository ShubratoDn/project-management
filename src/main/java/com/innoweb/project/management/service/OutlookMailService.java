package com.innoweb.project.management.service;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutlookMailService {

    private final GraphServiceClient<Request> graphClient;
    private final String targetUserIdOrEmail;

    public OutlookMailService(GraphServiceClient<?> client, @Value("${graph.user-id-or-email}") String targetUserIdOrEmail) {
        // The SDK uses raw type for builder, cast safely to Request typed client
        @SuppressWarnings("unchecked") GraphServiceClient<Request> typed = (GraphServiceClient<Request>) client;
        this.graphClient = typed;
        this.targetUserIdOrEmail = targetUserIdOrEmail;
    }

    public List<Message> listInboxTop(int top) {
        MessageCollectionPage page = graphClient.users(targetUserIdOrEmail).mailFolders("inbox").messages()
                .buildRequest()
                .top(top)
                .select("id,subject,from,receivedDateTime,hasAttachments,isRead")
                .orderBy("receivedDateTime DESC")
                .get();
        return page.getCurrentPage();
    }

    public Message getMessage(String messageId) {
        return graphClient.users(targetUserIdOrEmail).messages(messageId)
                .buildRequest()
                .select("id,subject,from,receivedDateTime,body,hasAttachments,isRead")
                .get();
    }
}


