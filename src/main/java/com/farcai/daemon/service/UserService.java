package com.farcai.daemon.service;

import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private GraphServiceClient<Request> graphServiceClient;

    @Value("${admin-email}")
    private String defaultEmail;

    public UserCollectionPage getUsers() {
        return graphServiceClient.users().buildRequest()
                .select("id")
                .count(true)
                .top(1)
                .get();
    }

    public DriveCollectionPage getDriveItems() {
        return graphServiceClient.drives().buildRequest().top(2).get();
    }

    public DriveItemCollectionPage getDriveRoot() {
        return graphServiceClient.users(defaultEmail)
                .drive()
                .root()
                .children()
                .buildRequest()
                //.select("id,name,createdBy,webUrl")
                .top(10)
                .get();
    }

    public String shareWithSpecificUsersOnly(String fileId, String rol, List<String> userEmails) {
        String fileUrl = getFileUrl(fileId);

        for (String email : userEmails) {
            try {
                // 1. Obtener todos los permisos del archivo
                PermissionCollectionPage existingPermissions = graphServiceClient.users(defaultEmail)
                        .drive()
                        .items(fileId)
                        .permissions()
                        .buildRequest()
                        .get();

                // 2. Buscar y eliminar permisos existentes para este usuario
                existingPermissions.getCurrentPage().forEach(permission -> {
                    if (permission.grantedTo != null &&
                            permission.grantedTo.user != null &&
                            email.equalsIgnoreCase(getEmailFromGrantedTo(permission.grantedTo.user.id))) {

                        // Eliminar permiso existente
                        graphServiceClient.users(defaultEmail)
                                .drive()
                                .items(fileId)
                                .permissions(permission.id)
                                .buildRequest()
                                .delete();
                    }
                });

                // 3. Asignar nuevo permiso
                LinkedList<DriveRecipient> recipients = new LinkedList<>();
                DriveRecipient recipient = new DriveRecipient();
                recipient.email = email;
                recipients.add(recipient);

                graphServiceClient.users(defaultEmail)
                        .drive()
                        .items(fileId)
                        .invite(DriveItemInviteParameterSet
                                .newBuilder()
                                .withRecipients(recipients)
                                .withRoles(Arrays.asList(rol))
                                .withRequireSignIn(true)
                                .withSendInvitation(false)
                                .build())
                        .buildRequest()
                        .post();

            } catch (Exception e) {
                log.error("Error al actualizar permisos para " + email, e);
            }
        }

        return fileUrl;
    }

    // Helper para extraer el email (ya que grantedTo.user no lo incluye directamente)
    private String getEmailFromGrantedTo(String userId) {
        // Si no tienes el email, necesitarás una consulta adicional
        User fullUser = graphServiceClient.users(userId)
                .buildRequest()
                .select("mail,userPrincipalName")
                .get();
        return fullUser.mail != null ? fullUser.mail : fullUser.userPrincipalName;
    }

    private String getFileUrl(String externalId) {
        DriveItem document = graphServiceClient
                .users(defaultEmail)
                .drive()
                .items(externalId)
                .buildRequest()
                .get();
        return document.webUrl;
    }

    public PermissionCollectionPage getPermissions(String externalId) {
        return graphServiceClient.users(defaultEmail).drive()
                .items(externalId)
                .permissions()
                .buildRequest()
                .get();
    }

    public Object setInvitation(String email, String name) {
        Invitation invitation = new Invitation();
        invitation.invitedUserEmailAddress = email;
        invitation.invitedUserDisplayName=name;
        invitation.sendInvitationMessage = true;
        invitation.inviteRedirectUrl= "https://m365.cloud.microsoft/";
        Invitation result = graphServiceClient.invitations().buildRequest().post(invitation);
        return graphServiceClient.users(result.invitedUser.id).buildRequest().get();
    }
}
