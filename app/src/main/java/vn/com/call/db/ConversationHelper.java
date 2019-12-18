package vn.com.call.db;

import android.content.Context;

import java.util.List;

import vn.com.call.model.sms.Conversation;

/**
 * Created by ngson on 01/11/2017.
 */

public class ConversationHelper {
    public static void addConversationsHaveScheduleMessage(Context context, List<Conversation> conversations) {
        List<Conversation> conversationsHaveScheduleMessage = ScheduleMessageDb.getConversationsScheduleMessage(context);

        int size = conversationsHaveScheduleMessage.size();

        for (int i = size - 1;i >= 0;i--) {
            Conversation conversationSchedule = conversationsHaveScheduleMessage.get(i);

            if (!containConversationScheduleMessage(conversations, conversationSchedule)) conversations.add(0, conversationSchedule);
        }

    }

    private static boolean containConversationScheduleMessage(List<Conversation> conversations, Conversation conversationSchedule) {
        for (Conversation conversation : conversations) {
            if (conversation.getThreadId() == conversationSchedule.getThreadId()) return true;
        }

        return false;
    }
}
