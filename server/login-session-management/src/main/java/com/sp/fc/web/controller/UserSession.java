package com.sp.fc.web.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
<<<<<<< HEAD
@AllArgsConstructor
@NoArgsConstructor
=======
@NoArgsConstructor
@AllArgsConstructor
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
@Builder
public class UserSession {

    private String username;
    private List<SessionInfo> sessions;

    public int getCount(){
        return sessions.size();
    }
<<<<<<< HEAD

=======
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
}
