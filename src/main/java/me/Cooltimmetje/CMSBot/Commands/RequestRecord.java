package me.Cooltimmetje.CMSBot.Commands;

import me.Cooltimmetje.CMSBot.Enums.EmojiEnum;
import me.Cooltimmetje.CMSBot.Main;
import me.Cooltimmetje.CMSBot.Profiles.CMSViewer;
import me.Cooltimmetje.CMSBot.Profiles.ProfileManager;
import me.Cooltimmetje.CMSBot.Utilities.Constants;
import me.Cooltimmetje.CMSBot.Utilities.MessagesUtils;
import me.Cooltimmetje.CMSBot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

/**
 * For recalling viewing records.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.1-ALPHA-DEV
 * @since v0.1-ALPHA-DEV
 */
public class RequestRecord {

    public static void run(String command, IMessage message){
        String[] args = command.split(" ");
        if(args.length > 1){
            if(Constants.botAccounts.contains(args[1].toLowerCase())){
                MessagesUtils.addReaction(message, "This account is a bot: " + args[1], EmojiEnum.X);
                return;
            }
            CMSViewer viewer = ProfileManager.getViewer(args[1].toLowerCase(), false);
            if(viewer == null){
                MessagesUtils.addReaction(message, "No viewing record found: " + args[1], EmojiEnum.X);
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.withAuthorIcon(Main.getInstance().getCmsBot().getApplicationIconURL()).withAuthorName("Viewing record for: " + args[1]);

                embed.withColor(MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255));

                embed.withTitle("Time shown in hours: active / inactive / total");
                embed.withDesc("Breakdown per streamer:");

                embed.withTimestamp(System.currentTimeMillis());
                embed.withFooterText("Generated at");

                double activeTotal = 0;
                double inactiveTotal = 0;
                double totalOverall = 0;
                for(String s : viewer.getActiveHours().keySet()){
                    double active,inactive,total;
                    active = viewer.getActiveHours().get(s);
                    inactive = viewer.getInactiveHours().get(s);
                    total = active + inactive;

                    activeTotal += active;
                    inactiveTotal += inactive;
                    totalOverall += total;

                    embed.appendField((viewer.getPresentIn().contains(s) ? ">" : "") + (viewer.getActiveIn().contains(s) ? ">" : "") + " __" + s + "__", MiscUtils.round(active, 1) + " / " + MiscUtils.round(inactive, 1) + " / " + MiscUtils.round(total, 1), true);
                }

                embed.appendField("__Total:__", MiscUtils.round(activeTotal, 1) + " / " + MiscUtils.round(inactiveTotal, 1) + " / " + MiscUtils.round(totalOverall, 1), false);

                embed.appendField("__Is entered:__", viewer.isEntered() ? EmojiEnum.WHITE_CHECK_MARK.getEmoji() : EmojiEnum.X.getEmoji(), true);
                embed.appendField("__Is opted-out:__", viewer.isOptedOut() ? EmojiEnum.WHITE_CHECK_MARK.getEmoji() : EmojiEnum.X.getEmoji(), true);

                message.getChannel().sendMessage("", embed.build(), false);
            }
        } else {
            MessagesUtils.addReaction(message, "Required arguments: username", EmojiEnum.X);
        }
    }

}
