package com.freyr.apollo18.commands.fun;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class BinaryCommand extends Command {

    public BinaryCommand(Apollo18 bot) {
        super(bot);
        this.name = "binary";
        this.description = "Encode any string into a binary equivalent!";
        this.category = Category.FUN;

        this.subCommands.add(new SubcommandData("encode", "Encode your string!").addOption(OptionType.STRING, "string", "Type in your string!", true));
        this.subCommands.add(new SubcommandData("decode", "Decode a binary string!").addOption(OptionType.STRING, "string", "Type in your binary string!", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String subCommand = event.getSubcommandName();

        switch (subCommand) {
            case "encode" -> {
                String data = getApiData("https://some-random-api.ml/binary?text=" + event.getOption("string").getAsString()).getString("binary");
                event.getHook().sendMessage("```" + data + "```").queue();
            }

            case "decode" -> {
                String data = getApiData("https://some-random-api.ml/binary?decode=" + event.getOption("string").getAsString()).getString("text");
                event.getHook().sendMessage("```" + data + "```").queue();
            }
        }
    }
}
