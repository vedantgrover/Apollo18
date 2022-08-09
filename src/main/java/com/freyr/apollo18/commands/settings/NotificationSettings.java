package com.freyr.apollo18.commands.settings;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class NotificationSettings extends Command {

    public NotificationSettings(Apollo18 bot) {
        super(bot);
        this.name = "notification-settings";
        this.description = "Edit notification settings here";
        this.category = Category.SETTINGS;

        this.subCommands.add(new SubcommandData("toggle", "Toggle your notifications on or off"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();

        switch (event.getSubcommandName()) {
            case "toggle" -> {
                db.toggleNotifications(event.getUser().getId());
                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Notifications have been __" + ((db.getNotificationToggle(event.getUser().getId())) ? "enabled":"disabled") + "__")).queue();
            }
        }
    }
}
