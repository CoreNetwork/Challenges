package us.corenetwork.challenges;

//import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;



public enum Setting {
	
	SWITCH_TIME_OFFSET("SwitchTimeOffset", 3600 * 19 + 3600 * 24 * 6),
	PLAYER_CLASSES("PlayerClasses", null),
	PROTECTED_GROUPS("ProtectedGroups", Arrays.asList(new String[] { "Guardian" })),
	ITEMS_PER_PAGE("ItemsPerPage", 10),
	DEBUG_MODE("DebugMode", false),
	
	CURRENT_WEEK("CurrentWeek", 1),
	CURRENT_WEEK_START("CurrentWeekStart", null),
	STOPPED("Stopped", false),
	
	MESSAGE_NO_PERMISSION("Messages.NoPermssion", "You are not allowed to do that!"),
	
	MESSAGE_LIST_WEEKS_HEADER("Messages.ListWeeksHeader", "&6ID &2START DATE &7STATUS [NEWLINE] &7---------------------------------------"),
	MESSAGE_LIST_WEEKS_ENTRY("Messages.ListWeeksEntry", "&6#<ID> &2<Date> &7<Status>"),
	MESSAGE_LIST_LEVELS_HEADER("Messages.ListLevelsHeader", "Challenge #&a<ID>&f, running from &a<Start>&f to &a<End>&f: [NEWLINE] &7---------------------------------------"),
	MESSAGE_LIST_LEVELS_ENTRY("Messages.ListLevelsEntry", "Level &a<Level>&f: &7<Desc> &b[<Points> Points]"),
	MESSAGE_CHALLENGE_IN_PROGRESS("Messages.ChallengeInProgress", "Challenge in Progress"),
	MESSAGE_CHALLENGE_ENDED("Messages.ChallengeEnded", "Challenge ended"),
	MESSAGE_LEVELS_CREATED("Messages.LevelCreated", "<Number> levels created"),
	MESSAGE_FUTURE_ONLY("Messages.FutureOnly", "You can only edit future challenges!"),
	MESSAGE_CREATE_COMMAND_RESPONSE("Messages.CreateCommandResponse", "Entering editing mode for challenge #&a<ID>&f, running from &a<Start>&f to &a<End>&f:"),
	MESSAGE_CREATE_COMMAND_INSTRUCTIONS("Messages.CreateCommandInstructions", "Use &e/cha createlevel&f to add new level or &e/cha editlevel [number]&f to edit existing one."),
	MESSAGE_NOT_IN_EDIT_MODE("Messages.NotInEditMode", "&cYou must be in edit mode for that! Use &e/cha editweek [week diff]"),
	MESSAGE_FINISH_EDITING_FIRST("Messages.FinishEditingFirst", "&cYou must first finish existing edit!"),
	MESSAGE_CREATING_LEVEL("Messages.CreatingLevel", "Creating level <Number>"),
	MESSAGE_EDITING_LEVEL("Messages.EditingLevel", "Editing level <Number>"),
	MESSAGE_ENTER_DESCRIPTION("Messages.EnterDescription", "Enter description for this level into chatbox. If you reach chatbox limit, just hit enter and continue with next line. Enter &a/ok&f to finish entering message."),
	MESSAGE_OLD_DESCRITPION("Messages.OldDescription", "Previous description: &e<Desc>&f (Enter &a-1&f to leave it as is)"),
	MESSAGE_DESCRPITION_PART_ENTERED("Messages.DescriptionPartEntered", "Description received. Keep typing or send /ok to finish"),
	MESSAGE_MUST_ENTER_DESCRIPTION("Messages.MustEnterDescription", "You must enter a description! Try again."),
	MESSAGE_ENTER_POINTS("Messages.EnterPoints", "Enter amount of points players will receive for completing this level into chatbox:"),
	MESSAGE_OLD_POINTS("Messages.OldPoints", "Previous amount of points: &e<Number>&f (Enter &a-1&f to leave it as is)"),
	MESSAGE_MUST_ENTER_NUMBER_POINTS("Messages.MustEnterNumberPoints", "You must enter number of points that players will receive! Try again."),
	MESSAGE_LEVEL_SAVED("Messages.LevelSaved", "Level saved. Use &e/cha createlevel&f to add another level, &e/cha editlevel [number]&f to edit existing one or &e/cha save&f to exit editing mode."),
	MESSAGE_EDITING_MODE_EXIT("Messages.EditingModeExit", "You are now leaving editing mode."),
	MESSAGE_LEVEL_DELETED("Messages.LevelDeleted", "Level deleted."),
	MESSAGE_STOPPED_ADMIN("Messages.StoppedAdmin", "Plugin stopped. Use &a/cha resume (id)&f to resume it."),
	MESSAGE_RESUMED_ADMIN("Messages.ResumedAdmin", "Plugin resumed at level <Level>."),
	
	MESSAGE_CH_HEADER("Messages.ChHeader", "Challenge #&a<ID>&f &7(<From> to <To> - <Left> left) [NEWLINE] ---------------------------------------"),
	MESSAGE_CH_ENTRY("Messages.ChEntry", "&eLevel <Level>: &6<Desc> &b[<Status>] [NEWLINE] &7---------------------------------------"),
	MESSAGE_CH_ALL_COMPELETED("Messages.ChAllCompleted", "&aYou have completed every challenge! Wait until next week for new ones. [NEWLINE] &7---------------------------------------"),
	MESSAGE_CH_FOOTER("Messages.ChFooter", "&fWhen completed, use &b/ch done&f to submit your entry and get new challenge."),
	MESSAGE_CH_NUM_FOOTER("Messages.ChNumFooter", "&fWhen completed, use &b/ch done <Number>&f to submit your entry."),
	MESSAGE_CH_ALL_FOOTER("Messages.ChAllFooter", "&fWhen completed, use &b/ch done level&f to submit your entry, where level is number of level you completed."),
	MESSAGE_POINTS("Messages.Points", "+<Points> points"),
	MESSAGE_POINT("Messages.Point", "+<Points> point"),
	MESSAGE_COMPLETED("Messages.Completed", "Completed"),
	MESSAGE_WAITING_INSPECTION("Messages.WaitingInspection", "Waiting inspection"),
	MESSAGE_REJECTED("Messages.Rejected", "Rejected."),
	MESSAGE_NOT_SUBMITTED("Messages.NotSubmitted", "Not Submitted"),
	MESSAGE_ALREADY_COMPLETED("Messages.AlreadyCompleted", "&cYou have already completed that level!"),
	MESSAGE_DONE_USAGE("Messages.DoneUsage","Usage: &b/ch done level&f where \"level\" is number of level you completed."),
	MESSAGE_INVALID_LEVEL("Messages.InvalidLevel", "&cThere is no level <Level>."),
	MESSAGE_CHALLENGE_SENT("Messages.ChallengeSent", "&aChallenge submission has been sent. Give us some time to review it. Meanwhile, use &f/ch &ato do the next level."),
	MESSAGE_CHALLENGE_SENT_MAX("Messages.ChallengeSentMax", "&aChallenge submission has been sent. Give us some time to review it."),
	MESSAGE_MOD_SUBMISSION_NOTICE("Messages.ModSubmissionNotice", "&9Player <Player> has just completed level <Level>! Use &e/chm tp <ID>"),
	MESSAGE_MOD_LOGIN_NOTICE("Messages.ModLoginNotice", "&9There are <Amount> completed challenges waiting for inspection! Use &e/chm list"),
	MESSAGE_COMPLETED_HEADER("Messages.CompletedHeader", "Challenges waiting for inspection: [NEWLINE] &7Page <Current> of <Max> [NEWLINE] &7---------------------------------------"),
	MESSAGE_COMPLETED_ALL_DONE("Messages.CompletedAllDone", "&bAll entries have been inspected, good job."),
	MESSAGE_COMPLETED_ENTRY("Messages.CompletedEntry", "#&a<ID> &f - &f<Player> &f(Level &a<Level>&f)<HandledBy>"),
	MESSAGE_HANDLED("Messages.HandledBy", " &cHandled by <Mod>"),
	MESSAGE_COMPLETED_FOOTER("Messages.CompletedFooter", "&7--------------------------------------- [NEWLINE] Use command &e/chm tp [ID]"),
	MESSAGE_COMPLETED_FOOTER_PAGES("Messages.CompletedFooterPages", "&7--------------------------------------- [NEWLINE] Use command &e/chm tp [ID] &7Page <Page>/<Pages>"),
	MESSAGE_TELEPORTED("Messages.Teleported", "&aTeleported"),
	MESSAGE_INVALID_ID("Messages.InvalidID", "&cInvalid ID! Try again."),
	MESSAGE_ALREADY_HANDLED("Messages.AlreadyHandled", "&cThis submission is already handled by <Mod>!"),
	MESSAGE_COMPLETION_DONE("Messages.CompletionDone", "Level marked as completed. Player &a<Player>&f will receive &a<Points>&f points."),
	MESSAGE_COMPLETION_DONE_NO_POINTS("Messages.CompletionDoneNoPoints", "Level marked as completed, but player will not receive any points. Are you sure you did not complete twice?"),
	MESSAGE_DELETE_ONLY_OPEN_CHALLENGES("Messages.DeleteOnlyOpenChallenges", "You can't delete already closed challenge!"),
	MESSAGE_SUBMISSION_REJECTED("Messages.SubmissionRejected", "&9Your challenge submission for level &e<Level>&9 was rejected. Make sure you fully fulfilled objectives and try again."),
	MESSAGE_SUBMISSION_REJECTED_MESSAGE("Messages.SubmissionRejectedMesage", "&9Your challenge submission for level &e<Level>&9 was rejected: &a<Message>"),
	MESSAGE_SUBMISSION_UNDONE("Messages.SubmissionUndone.NoReason", "&9Your challenge submission for level &e<Level>&9 was undone. Make sure you fully fulfilled objectives and try again."),
	MESSAGE_SUBMISSION_UNDONE_MESSAGE("Messages.SubmissionUndone.Reason", "&9Your challenge submission for level &e<Level>&9 was undone: &a<Message>"),
	MESSAGE_DELETED("Messages.Deleted", "Submission was deleted and rejected."),
	MESSAGE_LOCK_ONLY_APPROVED("Messages.LockOnlyApproved", "&cCannot lock! Use &7/chm complete <ID> &cfirst."),
	MESSAGE_LOCK_FUTURE_ONLY("Messages.LockFutureOnly", "You cannot lock past week's submissions!"),
	MESSAGE_LOCKED("Messages.locked", "Submission locked"),
	MESSAGE_NEW_CHALLENGE_ANNOUNCEMENT("Messages.NewChallengeAnnouncement", "&aAttention flatcorians! There is a new week challenge. Check out &e/ch&a and start gaining some points!"),
	MESSAGE_GLOBAL_PUNISHED("Messages.GlobalPunished", "&6Player &a<Player>&6 was just punished for &a<Points>&6 points due to &a<Reason>&6!"),
	MESSAGE_GLOBAL_DEMOTED("Messages.GlobalDemoted", "&cPlayer &7<Player> &chas just lost &7<Points> &cpoints, demoted to &7<Class> &cdue to:&7<Reason>!"),
	MESSAGE_GLOBAL_PROMOTED("Messages.GlobalPromoted", "&a<Jubilation> Player &6<Player>&a has just been promoted to &6<Class>&a!"),
	MESSAGE_PUNISHED("Messages.MessagePunished", "&cYou have been punished! Your flat point count has been deducted by &6<Amount>&c points."),
	MESSAGE_PUNISHED_REASON("Messages.MessagePunishedReason", "&cYou have been punished! Your flat point count has been deducted by &6<Amount>&c points due to &6<Reason>&c."),
	MESSAGE_EARNED_POINTS("Messages.MessageEarnedPoints", "&aYou have just earned &c<Amount>&a Points!"),
	MESSAGE_EARNED_POINTS_REASON("Messages.MessageEarnedPointsReason", "&aYou have just earned &c<Amount>&a Points due to &c<Reason>&a!"),
	MESSAGE_PROMOTED("Messages.MessagePromoted", "&a<Jubilation> You have been promoted to &6<Class>&a!"),
	MESSAGE_PROMOTED_REASON("Messages.MessagePromotedReason", "&a<Jubilation> You have been promoted to &6<Class>&a due to &6<Reason>&a!"),
	MESSAGE_DEMOTED("Messages.MessageDemoted", "&cYou have been demoted to &6<Class>&c!"),
	MESSAGE_DEMOTED_REASON("Messages.MessageDemotedReason", "&cYou have been demoted to &6<Class>&c due to &6<Reason>&c!"),
	MESSAGE_PLAYER_POINTS("Messages.PlayerPoints", "&6Player &a<Player>&6 has &a<Points>&6 points total and &a<PendingPoints>&6 points waiting for his logon."),
	
	MESSAGE_PLAYER_POINTS_ALTERED("Messages.PlayerPointsAltered", "&6Amount of points for player &a<Player>&6 was changed by &a<Change>&6"),
	MESSAGE_REASON_COMPLETE("Messages.ReasonComplete", "completing challenge #<Week> level <Level>!"),
	MESSAGE_FLATPOINTS("Messages.FlatPoints", "&aYou currently have &6<Points>&a points and your rank is &6<Rank>&a!"),
	MESSAGE_FLATPOINTS_NEXT_RANK("Messages.FlatPointsNextRank", "&aYou currently have &6<Points>&a points and your rank is &6<Rank>&a! To achieve new rank &6<NewRank>&a, you need to earn another &6<PointsLeft>&a points!"),
	MESSAGE_USING_PREVIOUS_ID("Messages.UsingPreviousId", "&6No ID Specified. Using ID from last teleport - &a<ID>&6."),
	MESSAGE_EXPLODE_ENTRY("Messages.ExplodeEntry", "Level <Level>: /chm tp <ID>"),
	MESSAGE_SUBMISSION_UNCLAIMED("Messages.SubmissionUnclaimed", "Submission was successfully released of all claims!"),
	
	MESSAGE_STATUS_HEADER("Messages.StatusHeader", "Challenge #&a<ID>&f &7(<From> to <To> - <Left> left) [NEWLINE] ---------------------------------------"),
	MESSAGE_STATUS_ENTRY("Messages.StatusEntry", "Level <Level>: <Status>"),

	MESSAGE_STATUS_WAITING_REVIEW("Messages.StatusWaitingReview", "Submitted, awaiting review"),
	MESSAGE_STATUS_APPROVED("Messages.StatusApproved", "Approved <Time> ago! +<Points>!"),
	MESSAGE_STATUS_REJECTED("Messages.StatusRejected", "Rejected <Time> ago."),
	MESSAGE_STATUS_REJECTED_COMMENT("Messages.StatusRejectedComment", "Rejected <Time> ago. Comment: <Comment>"),
	MESSAGE_STATUS_NOT_SUBMITTED("Messages.StatusNotSubmitted", "Not submitted yet."),
	MESSAGE_STATUS_TP_LEVEL("Messages.StatusTpLevel", "Level <Numbers>: <State>"),
	MESSAGE_STATUS_TP_LEVELS("Messages.StatusTpLevels", "Levels <Numbers>: <State>"),
	
	MESSAGE_STOPPED("Messages.Stopped", "&cThere is no challenge at this time"),
	MESSAGE_RESUMED_ANNOUNCEMENT("Messages.ResumedAnnouncement", MESSAGE_NEW_CHALLENGE_ANNOUNCEMENT.getDefault()),

	MESSAGE_MOD_LIST_ENTRIES("Messages.ModListEntries.Title", "Challenge entries for <Player>:"),
	MESSAGE_MOD_LIST_ENTRIES_WEEK("Messages.ModListEntries.Week", "#<Week>: <Entries>"),
	MESSAGE_MOD_LIST_ENTRIES_ENTRY("Messages.ModListEntries.Entry", "<Color><ID>/<Level>"),
	MESSAGE_NO_CHALLENGES_THAT_WEEK("Messages.NoChallengesThatWeek", "There are no challenges submitted for that week."),

	MESSAGE_MOD_UNDO_SUMMARY("Messages.ModUndo.Summary", "Undone <Levels> levels from <Player>, total of <Points> points"),
	MESSAGE_MOD_UNDO_NOT_FOUND("Messages.ModUndo.NotFound", "No challenges found to undo"),

	MESSAGE_TOP_HEADER("Messages.TopHeader", "Player              Points  Pending"),
	
	COLOR_NOT_SUBMITTED("Colors.ChallengeState.NotSubmitted", "&8"),
	COLOR_SUBMITTED("Colors.ChallengeState.Submitted", "&6"),
	COLOR_ACCEPTED("Colors.ChallengeState.Accepted", "&e"),
	COLOR_REJECTED("Colors.ChallengeState.Rejected", "&c"),
	COLOR_UNDONE("Colors.ChallengeState.Rejected", "&c"),

	@SuppressWarnings("serial")
	JUBILATIONS("Jubilations", new ArrayList<String>(){{
		add("Woo!");
		add("Nice work!");
		add("Great job!");
		add("Not bad.");
	}}),

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
	TOP_NAME_COLUMN_COLOR("Top.Name.Color", "&r");
	
	private String name;
	private Object def;
	
	private Setting(String Name, Object Def)
	{
		name = Name;
		def = Def;
	}
	
	public String getString()
	{
		return name;
	}
	
	public Object getDefault()
	{
		return def;
	}
}
