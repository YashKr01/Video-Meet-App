package com.yash.videomeet.listeners;

import com.yash.videomeet.models.User;

public interface UsersListener {

    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);

    void onMultipleUserAction(Boolean isMultipleUsersSelected);

}
