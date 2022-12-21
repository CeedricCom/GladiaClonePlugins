package me.deltaorion.townymissionsv2.configuration;

public enum Message {

    TEST("test"),
    FAKE("do.not.use.test"),
    NO_PERMISSION("no-permission"),
    COMMAND_NOT_INTEGER("command.notInteger"),
    COMMAND_NOT_NUMBER("command.notNumber"),
    COMMAND_NOT_BOOLEAN("command.notBoolean"),
    COMMAND_NOT_PLAYER("command.notPlayer"),
    COMMAND_NOT_GOVERNEMENT("command.notGovernment"),
    COMMAND_NOT_TOWN("command.notTown"),
    COMMAND_NOT_NATION("command.notNation"),
    COMMAND_NOT_VALID("command.invalidArgumentOther"),
    COMMAND_INVALID_DURATION("command.invalidDuration"),
    COMMAND_NOT_ENUM("command.notEnum"),
    COMMAND_INVALID_ARGUMENT("command.invalidArgument"),
    COMMAND_BAD_USAGE("command.invalidUsage"),
    GOV_MISSION_TITLE("mission.mission-title"),
    GOV_MISSION_HELP("mission.help-text"),
    GOV_DURATION("mission.duration-text"),
    GOV_CHAT_GOAL_TEXT("mission.chat.goal-text"),
    GOV_GATHER_TEXT("mission.gather.goal-text"),
    MISSION_GOAL_COMPLETION("mission.goal.completion"),
    MISSION_GOAL_GIVE("mission.goal.give"),
    MISSION_REWARD_RECEIVE("mission.reward.completion"),
    GOVERNMENT_COMPLETION("mission.government.completion"),
    MISSION_GOAL_COMPLETE_TITLE("mission.goal.complete-title"),
    MISSION_GOAL_COMPLETE_SUBTITLE("mission.goal.complete-subtitle"),
    COMMAND_NO_MISSION("command.contribute.no-mission"),
    COMMAND_NOT_PRIMARY("command.contribute.not-primary"),
    ERROR_ACTIVE_NO_GOAL("error.active-no-goal"),
    COMMAND_AUTOCONTRIBUTE_TOGGLE_ALL("command.autocontribute.toggle-all"),
    COMMAND_AUTOCONTRIBUTE_TOGGLE_SPECIFIC("command.autocontribute.toggle-specific"),
    COMMAND_NOT_IN_GOVERNMENT("command.misc.not-in-government"),
    GOVERNMENT_COOLDOWN_SCREEN("mission.cooldown-text"),
    REWARD_MONEY_NAME("reward.money-name"),
    REWARD_EXP_NAME("reward.exp-name"),
    REWARD_GOV_BANK_NAME("reward.government-bank-reward-name");
    ;

    //Mission: Status
    //   > help text
    //   > reward
    //   > goal
    //   > duration (if applicable)

    private final String location;

    Message(String location) {
        this.location = location;
    }

    public String getMessage(Object... args) {
        return MessageManager.getMessage(location,args);
    }
}
