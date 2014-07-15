package us.corenetwork.challenges;

import org.bukkit.Sound;
import us.corenetwork.combine.notification.Template;

public class NotificationTemplates {
    public static Template CHALLENGE_ACCEPTED, CHALLENGE_REJECTED, CHALLENGE_UNDONE;

    public static void init() {
        CHALLENGE_ACCEPTED = Challenges.notifications.registerTemplate(Challenges.instance, "challengeaccept", "Challenge <Level> was accepted. +<Points> points.", Sound.VILLAGER_YES);
        CHALLENGE_REJECTED = Challenges.notifications.registerTemplate(Challenges.instance, "challengereject", "Challenge <Level> was rejected, due to <Reason>.",  Sound.VILLAGER_NO);
        CHALLENGE_UNDONE =   Challenges.notifications.registerTemplate(Challenges.instance, "challengeundo",   "Challenge <Level> was undon, due to <Reason>.",     null);
        //TODO use settings values
    }
}
