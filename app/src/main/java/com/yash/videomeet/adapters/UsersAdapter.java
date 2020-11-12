package com.yash.videomeet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.yash.videomeet.R;
import com.yash.videomeet.listeners.UsersListener;
import com.yash.videomeet.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> users;
    private UsersListener usersListener;
    private List<User> selectedUsers;

    public UsersAdapter(List<User> users, UsersListener usersListener) {
        this.users = users;
        this.usersListener = usersListener;
        selectedUsers = new ArrayList<>();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user, parent, false);
        return new UserViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.setUserData(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView imageAudioMeeting, imageVideoMeeting;
        TextView textFirstChar, textUsername, textEmail;
        ConstraintLayout userContainer;
        ImageView imageSelected;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imageAudioMeeting = itemView.findViewById(R.id.imageAudioMeeting);
            imageVideoMeeting = itemView.findViewById(R.id.imageVideoMeeting);
            textFirstChar = itemView.findViewById(R.id.textFirstChar);
            textUsername = itemView.findViewById(R.id.textUsername);
            textEmail = itemView.findViewById(R.id.textEmail);
            userContainer = itemView.findViewById(R.id.userContainer);
            imageSelected = itemView.findViewById(R.id.imageSelected);

        }

        void setUserData(User user) {
            textFirstChar.setText(user.firstName.substring(0, 1));
            textUsername.setText(String.format("%s %s", user.firstName, user.lastName));
            textEmail.setText(user.email);

            imageVideoMeeting.setOnClickListener(view ->
                    usersListener.initiateVideoMeeting(user));

            imageAudioMeeting.setOnClickListener(view ->
                    usersListener.initiateAudioMeeting(user));

            userContainer.setOnLongClickListener(view -> {

                if (imageSelected.getVisibility() != View.VISIBLE) {
                    selectedUsers.add(user);
                    imageSelected.setVisibility(View.VISIBLE);
                    imageVideoMeeting.setVisibility(View.GONE);
                    imageAudioMeeting.setVisibility(View.GONE);
                    usersListener.onMultipleUserAction(true);
                }

                return true;
            });

            userContainer.setOnClickListener(view -> {

                if (imageSelected.getVisibility() == View.VISIBLE) {
                    selectedUsers.remove(user);
                    imageSelected.setVisibility(View.GONE);
                    imageAudioMeeting.setVisibility(View.VISIBLE);
                    imageVideoMeeting.setVisibility(View.VISIBLE);
                    if (selectedUsers.size() == 0) {
                        usersListener.onMultipleUserAction(false);
                    }
                } else {
                    if (selectedUsers.size() > 0) {
                        selectedUsers.add(user);
                        imageSelected.setVisibility(View.VISIBLE);
                        imageVideoMeeting.setVisibility(View.GONE);
                        imageAudioMeeting.setVisibility(View.GONE);
                    }
                }
            });

        }
    }
}
