package us.corenetwork.challenges;

//import org.bukkit.ChatColor; 

import java.util.ArrayList;
import java.util.Arrays;

;

public enum Setting {

    SWITCH_TIME_OFFSET("SwitchTimeOffset", 3600 * 19 + 3600 * 24 * 6),
    PLAYER_CLASSES("PlayerClasses", null),
    PROTECTED_GROUPS("ProtectedGroups", Arrays.asList(new String[]{"Guardian"})),
    ITEMS_PER_PAGE("ItemsPerPage", 10),
    DEBUG_MODE("DebugMode", false),
    CURRENT_WEEK("CurrentWeek", 1, SettingType.STORAGE),
    CURRENT_WEEK_START("CurrentWeekStart", null, SettingType.STORAGE),
    STOPPED("Stopped", false, SettingType.STORAGE),
    FIRST_WEEK_START("FirstWeekStart", "Sun, 18:00"),
    MESSAGE_NO_PERMISSION("Messages.NoPermssion", "&cYou are not allowed to do that."),
    MESSAGE_LIST_WEEKS_HEADER("Messages.ListWeeksHeader", "&6ID &2START DATE &7STATUS [NEWLINE] &8---------------------------------"),
    MESSAGE_LIST_WEEKS_ENTRY("Messages.ListWeeksEntry", "&6#<ID> &2<Date> &7<Status>"),
    MESSAGE_LIST_LEVELS_HEADER("Messages.ListLevelsHeader", " [NEWLINE] &bChallenge #<ID> &7(<From> - <To>): [NEWLINE] &8---------------------------------"),
    MESSAGE_LIST_LEVELS_ENTRY("Messages.ListLevelsEntry", "&e[<Level>] &7<Desc> &b<Points> points total"),
    MESSAGE_CHALLENGE_IN_PROGRESS("Messages.ChallengeInProgress", "Challenge in Progress"),
    MESSAGE_CHALLENGE_ENDED("Messages.ChallengeEnded", "Challenge ended"),
    MESSAGE_LEVELS_CREATED("Messages.LevelCreated", "<Number> levels created"),
    MESSAGE_FUTURE_ONLY("Messages.FutureOnly", "&cYou can only edit future challenges!"),
    MESSAGE_CREATE_COMMAND_RESPONSE("Messages.CreateCommandResponse", "&6Editing challenge &e#<ID>&6, running from &e<Start>&6 to &e<End>&6:"),
    MESSAGE_CREATE_COMMAND_INSTRUCTIONS("Messages.CreateCommandInstructions", "&6Use &e/cha createlevel &6to add new level. [NEWLINE] &6Use &e/cha editlevel &6[&enumber&6] to edit existing one."),
    MESSAGE_NOT_IN_EDIT_MODE("Messages.NotInEditMode", "&cYou must be in edit mode first! Use &7/cha editweek [week difference]"),
    MESSAGE_FINISH_EDITING_FIRST("Messages.FinishEditingFirst", "&cYou must first finish existing edit!"),
    MESSAGE_CREATING_LEVEL("Messages.CreatingLevel", "&aCreating level <Number>"),
    MESSAGE_EDITING_LEVEL("Messages.EditingLevel", "&aEditing level <Number>"),
    MESSAGE_ENTER_DESCRIPTION("Messages.EnterDescription", "&6Enter description for this level. &8If you reach chatbox limit, just hit Enter and continue with next line. [NEWLINE] &6Enter &e/ok &6to finish input."),
    MESSAGE_OLD_DESCRITPION("Messages.OldDescription", "&7Previous description: &6<Desc>&f &7(Enter &f-1&7 to leave it as is)"),
    MESSAGE_DESCRPITION_PART_ENTERED("Messages.DescriptionPartEntered", "&aDescription received. Keep typing or send &f/ok &ato finish."),
    MESSAGE_MUST_ENTER_DESCRIPTION("Messages.MustEnterDescription", "&cYou must enter a description! Try again."),
    MESSAGE_ENTER_POINTS("Messages.EnterPoints", "&6Enter amount of points players will get for this level:"),
    MESSAGE_OLD_POINTS("Messages.OldPoints", "&7Previous points: &6<Number> &7(Enter &f-1 &7to leave it as is)"),
    MESSAGE_MUST_ENTER_NUMBER_POINTS("Messages.MustEnterNumberPoints", "&cYou must enter number of points for the level. Try again."),
    MESSAGE_LEVEL_SAVED("Messages.LevelSaved", "&aLevel saved. [NEWLINE] &6Use &e/cha createlevel &6to add another level. [NEWLINE] &6Use &e/cha editlevel &6[&enumber&6] to edit existing. [NEWLINE] &6Use &e/cha save &6to exit editing mode."),
    MESSAGE_EDITING_MODE_EXIT("Messages.EditingModeExit", "&7You are now leaving editing mode."),
    MESSAGE_LEVEL_DELETED("Messages.LevelDeleted", "Level deleted."),
    MESSAGE_STOPPED_ADMIN("Messages.StoppedAdmin", "Plugin stopped. Use &a/cha resume (id)&f to resume it."),
    MESSAGE_RESUMED_ADMIN("Messages.ResumedAdmin", "Plugin resumed at level <Level>."),
    MESSAGE_CH_HEADER("Messages.ChHeader", " [NEWLINE] &bChallenge #<ID> &7- <Left> left - (<From> to <To>) [NEWLINE] &8--------------------------------------------"),
    MESSAGE_CH_HEADER_PAST("Messages.ChHeader", " [NEWLINE] &bChallenge #<ID> &7- <Left> left - (<From> to <To>) [NEWLINE] &8--------------------------------------------"),
    MESSAGE_CH_ENTRY("Messages.ChEntry", "&e[Level <Level>] &6<Desc> &b<Status> [NEWLINE] &8--------------------------------------------"),
    MESSAGE_CH_ALL_COMPELETED("Messages.ChAllCompleted", "&bYou have completed all levels of this challenge! [NEWLINE] &7Wait until next week for another."),
    MESSAGE_CH_FOOTER("Messages.ChFooter", "&bWhen completed, use &f/ch done &bto submit your entry. [NEWLINE] &7Use &f/ch all &7to display all levels."),
    MESSAGE_CH_NUM_FOOTER("Messages.ChNumFooter", "&bWhen completed, use &f/ch done &bto submit your entry."),
    MESSAGE_CH_ALL_FOOTER("Messages.ChAllFooter", "&bWhen completed, use &f/ch done &bto submit your entry. &8(Example: /ch done 3)"),
    MESSAGE_POINTS("Messages.Points", "+<Points> points"),
    MESSAGE_POINT("Messages.Point", "+<Points> point"),
    MESSAGE_COMPLETED("Messages.Completed", "Completed!"),
    MESSAGE_WAITING_INSPECTION("Messages.WaitingInspection", "Awaiting inspection"),
    MESSAGE_REJECTED("Messages.Rejected", "&cRejected."),
    MESSAGE_NOT_SUBMITTED("Messages.NotSubmitted", "&7Not submitted yet"),
    MESSAGE_ALREADY_COMPLETED("Messages.AlreadyCompleted", "&cYou have already completed that level!"),
    MESSAGE_DONE_USAGE("Messages.DoneUsage", "Usage: &b/ch done"),
    MESSAGE_INVALID_LEVEL("Messages.InvalidLevel", "&cThere is no level <Level>."),
    MESSAGE_CHALLENGE_SENT("Messages.ChallengeSent", "&aChallenge entry has been sent: &6/status. [NEWLINE] &aGive us time to review it. Use &6/ch &afor another."),
    MESSAGE_CHALLENGE_SENT_MAX("Messages.ChallengeSentMax", "&aChallenge entry has been sent: &6/status. [NEWLINE] &aGive us some time to review it."),
    MESSAGE_MOD_SUBMISSION_NOTICE("Messages.ModSubmissionNotice", "&b<Player> has sent level <Level>&b! Review: &f/chm tp <ID>"),
    MESSAGE_MOD_LOGIN_NOTICE("Messages.ModLoginNotice", "&bThere are challenges to review (&e<Amount>&b). &f/chm list"),
    MESSAGE_COMPLETED_HEADER("Messages.CompletedHeader", " [NEWLINE] &bUse &f/chm tp id &bto claim a submission and teleport to it: [NEWLINE] &8---------------------------------"),
    MESSAGE_COMPLETED_ALL_DONE("Messages.CompletedAllDone", "&bAll entries have been inspected. Good job!"),
    MESSAGE_COMPLETED_ENTRY("Messages.CompletedEntry", "&bID: <ID> &7- &f<Player> &e[Level <Level>] <HandledBy>"),
    MESSAGE_HANDLED("Messages.HandledBy", " &cHandled by <Mod>"),
    MESSAGE_COMPLETED_FOOTER("Messages.CompletedFooter", "&8--------------------------------- [NEWLINE]"),
    MESSAGE_COMPLETED_FOOTER_PAGES("Messages.CompletedFooterPages", "&8--------------------------------- [NEWLINE] &8Page <Current>/<Max>"),
    MESSAGE_TELEPORTED("Messages.Teleported", "&6Teleported to &7<ID>&6. Use &7/chm complete &6to approve it. [NEWLINE] &6Use &7/chm deny reason &6to deny it."),
    MESSAGE_INVALID_ID("Messages.InvalidID", "&cInvalid ID! Try again."),
    MESSAGE_ALREADY_HANDLED("Messages.AlreadyHandled", "&cThis challenge entry is already handled by <Mod>."),
    MESSAGE_COMPLETION_DONE("Messages.CompletionDone", "&6Marked &7<ID> &6as completed. &7<Player> &6gets &7+<Points>"),
    MESSAGE_COMPLETION_DONE_NO_POINTS("Messages.CompletionDoneNoPoints", "&6Entry completed, but player won't receive any points. Did you complete it twice?"),
    MESSAGE_DELETE_ONLY_OPEN_CHALLENGES("Messages.DeleteOnlyOpenChallenges", "&cYou can't delete an already closed challenge!"),
    MESSAGE_SUBMISSION_REJECTED("Messages.SubmissionRejected", "&cYour challenge entry was rejected. Make sure to complete all objectives and try again."),
    MESSAGE_SUBMISSION_REJECTED_MESSAGE("Messages.SubmissionRejectedMesage", "&cYour challenge entry was rejected with a comment: &7<Message>"),
    MESSAGE_SUBMISSION_UNDONE("Messages.SubmissionUndone.NoReason", "&9Your challenge submission for level &e<Level>&9 was undone. Make sure you fully fulfilled objectives and try again."),
    MESSAGE_SUBMISSION_UNDONE_MESSAGE("Messages.SubmissionUndone.Reason", "&9Your challenge submission for level &e<Level>&9 was undone: &a<Message>"),
    MESSAGE_DELETED("Messages.Deleted", "&6Submission was deleted and rejected."),
    MESSAGE_LOCK_ONLY_APPROVED("Messages.LockOnlyApproved", "&cCannot lock! Use &7/chm complete &cfirst."),
    MESSAGE_LOCK_FUTURE_ONLY("Messages.LockFutureOnly", "&cYou cannot lock past week's submissions!"),
    MESSAGE_LOCKED("Messages.locked", "&6Area locked. Use &7/chm lock&6 to lock more areas."),
    MESSAGE_NEW_CHALLENGE_ANNOUNCEMENT("Messages.NewChallengeAnnouncement", "&b-------- [NEWLINE] &bAttention Flatcore! There is a new weekly challenge. [NEWLINE] &bCheck out &f/ch &band get some points! [NEWLINE] &b--------"),
    MESSAGE_GLOBAL_PUNISHED("Messages.GlobalPunished", "&cPlayer &7<Player> &chas just lost &7<Points> &cpoints due to: &7<Reason>&c."),
    MESSAGE_GLOBAL_DEMOTED("Messages.GlobalDemoted", "&cPlayer &7<Player> &chas just been demoted to &7<Class> &cdue to: &7<Reason>&c."),
    MESSAGE_GLOBAL_PROMOTED("Messages.GlobalPromoted", "&b<Jubilation>! &f<Player> &bis now a &f<Class>&b!"),
    MESSAGE_PUNISHED("Messages.MessagePunished", "&cYour points were deducted by &7<Amount>."),
    MESSAGE_PUNISHED_REASON("Messages.MessagePunishedReason", "&cYour points were deducted by &7<Amount>, due to <Reason>."),
    MESSAGE_EARNED_POINTS("Messages.MessageEarnedPoints", "&bYou got extra points: +&e<Amount>."),
    MESSAGE_EARNED_POINTS_REASON("Messages.MessageEarnedPointsReason", "<Reason> Points: &e+ <Amount>."),
    MESSAGE_PROMOTED("Messages.MessagePromoted", "&aYou have been promoted to &6<Class>&a!"),
    MESSAGE_PROMOTED_REASON("Messages.MessagePromotedReason", "&aYou have been promoted to &6<Class>&a! (<Reason>)"),
    MESSAGE_DEMOTED("Messages.MessageDemoted", "&cYou have been demoted to &7<Class>&c."),
    MESSAGE_DEMOTED_REASON("Messages.MessageDemotedReason", "&cYou have been demoted to &7<Class>&c. (<Reason>)"),
    MESSAGE_PLAYER_POINTS("Messages.PlayerPoints", "&6Player &7<Player>&6 has &7<Points>&6 points and <PendingPoints> pending."),
    MESSAGE_PLAYER_POINTS_ALTERED("Messages.PlayerPointsAltered", "&6Changed points for &7<Player>&6 by &7<Change>&6."),
    MESSAGE_REASON_COMPLETE("Messages.ReasonComplete", "&bCompleted &e[Level <Level>] &bof Challenge #<Week>."),
    MESSAGE_FLATPOINTS("Messages.FlatPoints", "&aYou have &6<Points>&a points. [NEWLINE]Your rank is &7<Rank>&6."),
    MESSAGE_FLATPOINTS_NEXT_RANK("Messages.FlatPointsNextRank", "&aYou have &6<Points>&a points. Your rank: &6<Rank>&a. [NEWLINE] You need &6<PointsLeft>&a more points to get &7<NewRank>&6."),
	MESSAGE_FLATPOINTS_PLAYER("Messages.FlatPointsPlayer", "&6Player &7<Player>&6 has &7<Points>&6 points and &7<PointsPending>&6 pending. [NEWLINE]&6Their rank is &7<Rank>&6."),
	MESSAGE_FLATPOINTS_NEXT_RANK_PLAYER("Messages.FlatPointsNextRankPlayer", "&6Player &7<Player>&6 has &7<Points>&6 points and &7<PointsPending>&6 pending. [NEWLINE] &6Their rank is &7<Rank> &6and they need &7<PointsLeft> &6to rank up to &6<NewRank>&6."),
    MESSAGE_USING_PREVIOUS_ID("Messages.UsingPreviousId", "&6Using ID from last teleport: &7<ID>&6."),
    MESSAGE_EXPLODE_ENTRY("Messages.ExplodeEntry", "&6Level <Level>: &7/chm tp <ID>"),
    MESSAGE_SUBMISSION_UNCLAIMED("Messages.SubmissionUnclaimed", "&6Submission is not handled by anyone anymore."),
    MESSAGE_STATUS_HEADER("Messages.StatusHeader", "[NEWLINE] &bChallenge #<ID> &7(<From> - <To>, <Left> left) [NEWLINE] &8--------------------------------------------"),
    MESSAGE_STATUS_HEADER_PAST("Messages.StatusHeaderPast", "[NEWLINE] &bChallenge #<ID> &7(<From> - <To>) [NEWLINE] &8--------------------------------------------"),
    MESSAGE_STATUS_ENTRY("Messages.StatusEntry", "&e[Level <Level>] <Status>"),
    MESSAGE_STATUS_WAITING_REVIEW("Messages.StatusWaitingReview", "&eSubmitted, awaiting review"),
    MESSAGE_STATUS_APPROVED("Messages.StatusApproved", "&6Approved <Time> ago! &b+<Points>!"),
    MESSAGE_STATUS_REJECTED("Messages.StatusRejected", "&cRejected <Time> ago without comment."),
    MESSAGE_STATUS_REJECTED_COMMENT("Messages.StatusRejectedComment", "&cRejected <Time> ago. Reason: <Comment>"),
    MESSAGE_STATUS_NOT_SUBMITTED("Messages.StatusNotSubmitted", "&7Not submitted yet."),
    MESSAGE_STATUS_TP_LEVEL("Messages.StatusTpLevel", "&6[Level <Numbers>] &7<State>"),
    MESSAGE_STATUS_TP_LEVELS("Messages.StatusTpLevels", "&6[Levels <Numbers>] &7<State>"),
    MESSAGE_STOPPED("Messages.Stopped", "&cChallenges start soon, relax your anus muscles."),
    MESSAGE_RESUMED_ANNOUNCEMENT("Messages.ResumedAnnouncement", "&b-------- [NEWLINE] &bAttention Flatcore! There is a new weekly challenge. [NEWLINE] &bCheck out &f/ch &band get some points! [NEWLINE] &b--------"),
    MESSAGE_MOD_LIST_HEADER("Messages.ModList.Header", "Challenge entries from week #<Week>:[NEWLINE]&8---------------------------------"),
    MESSAGE_MOD_LIST_SEPERATOR("Messages.ModList.Seperator", "&8---------------------------------"),
    MESSAGE_MOD_LIST_ENTRIES("Messages.ModListEntries.Title", "Challenge entries for <Player>:"),
    MESSAGE_MOD_LIST_ENTRIES_WEEK("Messages.ModListEntries.Week", "#<Week>: <Entries>"),
    MESSAGE_MOD_LIST_ENTRIES_ENTRY("Messages.ModListEntries.Entry", "<Color><ID>/<Level>"),
    MESSAGE_NO_CHALLENGES_THAT_WEEK("Messages.NoChallengesThatWeek", "There are no challenges submitted for that week."),
    MESSAGE_MOD_UNDO_SUMMARY("Messages.ModUndo.Summary", "Undone <Levels> levels from <Player>, total of <Points> points"),
    MESSAGE_MOD_UNDO_NOT_FOUND("Messages.ModUndo.NotFound", "No challenges found to undo"),
    MESSAGE_TOP_HEADER("Messages.TopHeader", "Player              Points  Pending"),
	MESSAGE_MOD_HISTORY_HEADER("Messages.ModHistory.Header", "Challenge edit history[NEWLINE]&8---------------------------------"),
	MESSAGE_MOD_HISTORY_HEADER_PLAYER("Messages.ModHistory.HeaderPlayer", "Challenge edit history for <Player>[NEWLINE]&8---------------------------------"),
	MESSAGE_MOD_HISTORY_ENTRY("Messages.ModHistory.Entry", "&b#<ID> Level <Level> <State>&7, <time> ago"),
	MESSAGE_MOD_HISTORY_ENTRY_PLAYER("Messages.ModHistory.EntryPlayer", "&b#<ID> Level <Level> <State> &7by <moderator>, <time> ago"),
	MESSAGE_MOD_TOP_HEADER("Messages.ModTop.Header", "Challenge edits rank list[NEWLINE]&8---------------------------------"),
	MESSAGE_MOD_TOP_ENTRY("Messages.ModTop.Entry", "#<Rank> <Player> <Edits>"),
	MESSAGE_MOD_UNCLAIM("Messages.ModUnclaim.Unclaim", "&6Your claim for a submission has been released."),
	MESSAGE_MOD_UNCLAIM_ID("Messages.ModUnclaim.UnclaimId", "&6A claim for submission &7<ID>&6 has been released."),
	MESSAGE_MOD_ALL_SUBMISSIONS("Messages.ModSubmissions.List", "&6All accepted submissions for level &7<Level>&6 in week &7<Week>&6:[NEWLINE]<List>"),
	MESSAGE_MOD_ALL_SUBMISSIONS_NONE("Message.ModSubmissions.NotFound", "&6No accepted submissions for level &7<Level>&6 in week &7<Week>&6 found."),
    MESSAGE_DEBUG_MODE("MessageDebug", false),
    COLOR_NOT_SUBMITTED("Colors.ChallengeState.NotSubmitted", "&8"),
    COLOR_SUBMITTED("Colors.ChallengeState.Submitted", "&6"),
    COLOR_ACCEPTED("Colors.ChallengeState.Accepted", "&e"),
    COLOR_REJECTED("Colors.ChallengeState.Rejected", "&c"),
    COLOR_UNDONE("Colors.ChallengeState.Rejected", "&c"),
    @SuppressWarnings("serial")
    JUBILATIONS("Jubilations", new ArrayList<String>() {

        {
            add("Woo!");
            add("Nice work!");
            add("Great job!");
            add("Not bad.");
        }
    }),
    TOP_PER_PAGE("Top.MaxPerPage", 10),
    TOP_FOOTNOTE_COLOR("Top.FootnoteColor", "&7"),
    TOP_PLACE_COLUMN_HEADER("Top.Place.Header", "&6Place"),
    TOP_PLACE_COLUMN_DISPLAY("Top.Place.Display", "<Place>"),
    TOP_PLACE_COLUMN_COLOR("Top.Place.Color", "&6"),
    TOP_PLACE_COLUMN_LEADING_ZERO_COLOR("Top.Place.LeadingZeroColor", "&8"),
    TOP_POINTS_COLUMN_HEADER("Top.Points.Header", "&ePoints"),
    TOP_POINTS_COLUMN_DISPLAY("Top.Points.Display", "<Points>"),
    TOP_POINTS_COLUMN_COLOR("Top.Points.Color", "&e"),
    TOP_POINTS_COLUMN_LEADING_ZERO_COLOR("Top.Points.LeadingZeroColor", "&8"),
    TOP_NAME_COLUMN_HEADER("Top.Name.Header", "&rName"),
    TOP_NAME_COLUMN_DISPLAY("Top.Name.Display", "<Player>"),
    TOP_NAME_COLUMN_COLOR("Top.Name.Color", "&r"),
    RUN_COMMAND_ON_RANK_UP("RunCommandOnRankUp", false),
    COMMAND_ON_RANK_UP("CommandOnRankUp", "crankreload"),
	GROUPMANAGER_USERS_FILE("GroupManager.UsersYml", "plugins/GroupManager/worlds/world/users.yml"),

    STORAGE_VERSION("StorageVersion", 0, SettingType.STORAGE);

	private SettingType type;

	private String name;

    private Object def;

    private Setting(String Name, Object Def) {
        name = Name;
        def = Def;
	    this.type = SettingType.CONFIG;
    }

	private Setting(String Name, Object Def, SettingType type) {
		name = Name;
		def = Def;
		this.type = type;
	}

	public String getString() {
        return name;
    }

    public Object getDefault() {
        return def;
    }

	public SettingType getSettingType()
	{
		return type;
	}
}