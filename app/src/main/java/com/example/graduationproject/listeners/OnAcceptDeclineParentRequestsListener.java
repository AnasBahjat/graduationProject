package com.example.graduationproject.listeners;

import com.example.graduationproject.models.ParentReceivedRequest;

public interface OnAcceptDeclineParentRequestsListener {
    void onParentAcceptDeclineClicked(int flag , ParentReceivedRequest parentReceivedRequest);
}
