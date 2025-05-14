package com.farcai.daemon.service;

import com.microsoft.graph.requests.*;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public DriveCollectionPage getDriveItems(){
        return graphServiceClient.drives().buildRequest().top(2).get();
    }

    public DriveItemCollectionPage getDriveRoot(){
     return graphServiceClient.users(defaultEmail)
             .drive()
             .root()
             .children()
             .buildRequest()
             //.select("id,name,createdBy,webUrl")
             .top(10)
             .get();
    }
}
