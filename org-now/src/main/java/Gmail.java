import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.List;

import com.google.api.services.gmail.Gmail.Users;
import com.google.api.services.gmail.model.Label;

/**
 * Handles function calls relating to the Gmail API.
 *
 * @author Pavi Lee
 * @version September 5, 2020
 */
public class Gmail {
    private static final String APPLICATION_NAME = "Gmail API";
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens-gmail-api";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Arrays.asList(GmailScopes.MAIL_GOOGLE_COM);
    private static final String CREDENTIALS_FILE_PATH = "/credentials-gmail" +
            "-api.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials_trash.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Gmail.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY,
                        new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline")
                        .build();
        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(
                "user");
    }

    public static List<String> getLabels() throws GeneralSecurityException,
            IOException {
        final NetHttpTransport HTTP_TRANSPORT =
                GoogleNetHttpTransport.newTrustedTransport();
        com.google.api.services.gmail.Gmail service =
                new com.google.api.services.gmail.Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        ListLabelsResponse listResponse = service.users().labels().list
                ("me").execute();
        List<Label> labels = listResponse.getLabels();

        if (labels.isEmpty()) {
            return null;
        } else {
            List<String> labelNameList = new LinkedList<String>();
            for (Label label : labels) {
                labelNameList.add(label.getName());
            }
            return labelNameList;
        }
    }

    public static List<EventInfo> getEventList(String label) throws IOException,
            GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT =
                GoogleNetHttpTransport.newTrustedTransport();
        com.google.api.services.gmail.Gmail service =
                new com.google.api.services.gmail.Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        String user = "me";
        Users users = service.users();
        List<Message> messages = service.users().messages().list(user).setQ(
                "is:" + label).setMaxResults((long) 100).execute().getMessages();

        if (messages == null) {
            return null;
        }

        Iterator<Message> iter = messages.iterator();
        List<EventInfo> eventInfoList = new LinkedList<EventInfo>();

        while (iter.hasNext()) {
            Message msg =
                    users.messages().get(user, iter.next().getId()).setFormat("FULL").execute();
            String snippet = msg.getSnippet();
            String eventName =
                    msg.getPayload().getHeaders().get(3).getValue();

            List<EventInfo> eventInfos =
                    EventInfo.createEventInfos(eventName,
                            snippet);
            if (eventInfos != null) {
                for (EventInfo eventInfo : eventInfos) {
                    if (eventInfo != null && !eventInfoList.contains(eventInfo)) {
                        eventInfoList.add(eventInfo);
                    }
                }
            }
        }

        return eventInfoList;
    }

}