package redrosr.jcaddons.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    // Default prefix for the client
    private static final String CLIENT_PREFIX = Formatting.DARK_GRAY + "[" + Formatting.GRAY + "C" + Formatting.GREEN + "A" + Formatting.DARK_GRAY + "] " + Formatting.RESET;

    /**
     * Sends a formatted text message with the client prefix.
     * Supports multiple text components, which will be combined with spaces.
     *
     * @param messages The text components to send.
     */
    public static void sendMessage(Text... messages) {
        if (mc.player != null) {
            Text combinedMessage = Text.empty().append(CLIENT_PREFIX).append(joinTexts(messages));
            mc.player.sendMessage(combinedMessage, false);
        }
    }

    /**
     * Sends a raw message without a prefix.
     * Supports multiple text components, which will be combined with spaces.
     *
     * @param messages The text components to send.
     */
    public static void sendRawMessage(Text... messages) {
        if (mc.player != null) {
            mc.player.sendMessage(joinTexts(messages), false);
        }
    }

    /**
     * Joins multiple Text components with spaces.
     *
     * @param messages The text components to join.
     * @return The combined Text component.
     */
    public static Text joinTexts(Text... messages) {
        MutableText combined = Text.empty();
        for (int i = 0; i < messages.length; i++) {
            combined.append(messages[i]);
            if (i < messages.length - 1) {
                combined.append(Text.literal(" ")); // Add space between texts
            }
        }
        return combined;
    }

    /**
     * Creates a hoverable text component.
     *
     * @param text      The main text.
     * @param hoverText The text to show on hover.
     * @return The formatted Text component.
     */
    public static Text hoverableText(String text, String hoverText) {
        return Text.literal(translateColorCodes(text))
                .setStyle(Style.EMPTY.withHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(translateColorCodes(hoverText))))
                );
    }

    /**
     * Creates a clickable text component.
     *
     * @param text    The main text.
     * @param command The command to run when clicked.
     * @return The formatted Text component.
     */
    public static Text clickableText(String text, String command) {
        return Text.literal(translateColorCodes(text))
                .setStyle(Style.EMPTY.withClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                );
    }

    /**
     * Creates a text component that suggests text when clicked.
     *
     * @param text       The main text.
     * @param suggestion The text to suggest when clicked.
     * @return The formatted Text component.
     */
    public static Text suggestableText(String text, String suggestion) {
        return Text.literal(translateColorCodes(text))
                .setStyle(Style.EMPTY.withClickEvent(
                        new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestion))
                );
    }

    /**
     * Creates a text component that opens a URL when clicked.
     *
     * @param text The main text.
     * @param url  The URL to open.
     * @return The formatted Text component.
     */
    public static Text urlText(String text, String url) {
        return Text.literal(translateColorCodes(text))
                .setStyle(Style.EMPTY.withClickEvent(
                        new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                );
    }

    /**
     * Creates an item hoverable text component.
     *
     * @param text The main text.
     * @param item The item stack to show.
     * @return The formatted Text component.
     */
    public static Text itemHoverText(String text, ItemStack item) {
        return Text.literal(translateColorCodes(text))
                .setStyle(Style.EMPTY.withHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(item)))
                );
    }

    /**
     * Creates a formatted text component.
     *
     * @param text The main text.
     * @return The formatted Text component.
     */
    public static Text formatText(String text) {
        return Text.literal(translateColorCodes(text));
    }

    /**
     * Formats a message with placeholders and color codes.
     *
     * @param message The message to format.
     * @return The fully formatted message.
     */
    private static String format(String message) {
        return translateColorCodes(message);
    }

    /**
     * Converts '&' color codes to Minecraft's formatting codes.
     *
     * @param message The message to process.
     * @return The color-formatted message.
     */
    private static String translateColorCodes(String message) {
        return message.replace("&0", Formatting.BLACK.toString())
                .replace("&1", Formatting.DARK_BLUE.toString())
                .replace("&2", Formatting.DARK_GREEN.toString())
                .replace("&3", Formatting.DARK_AQUA.toString())
                .replace("&4", Formatting.DARK_RED.toString())
                .replace("&5", Formatting.DARK_PURPLE.toString())
                .replace("&6", Formatting.GOLD.toString())
                .replace("&7", Formatting.GRAY.toString())
                .replace("&8", Formatting.DARK_GRAY.toString())
                .replace("&9", Formatting.BLUE.toString())
                .replace("&a", Formatting.GREEN.toString())
                .replace("&b", Formatting.AQUA.toString())
                .replace("&c", Formatting.RED.toString())
                .replace("&d", Formatting.LIGHT_PURPLE.toString())
                .replace("&e", Formatting.YELLOW.toString())
                .replace("&f", Formatting.WHITE.toString())
                .replace("&k", Formatting.OBFUSCATED.toString())
                .replace("&l", Formatting.BOLD.toString())
                .replace("&m", Formatting.STRIKETHROUGH.toString())
                .replace("&n", Formatting.UNDERLINE.toString())
                .replace("&o", Formatting.ITALIC.toString())
                .replace("&r", Formatting.RESET.toString());
    }

    /**
     * Converts '&' color codes into Minecraft `Text` components.
     *
     * @param message The message string with color codes.
     * @return A properly formatted Text component.
     */
    public static Text parseFormattedText(String message) {
        Text textComponent = Text.empty();
        Pattern pattern = Pattern.compile("&([0-9a-fklmnor])");
        Matcher matcher = pattern.matcher(message);

        int lastIndex = 0;
        Style currentStyle = Style.EMPTY;

        while (matcher.find()) {
            // Append previous text segment
            if (matcher.start() > lastIndex) {
                textComponent = textComponent.copy().append(Text.literal(message.substring(lastIndex, matcher.start())).setStyle(currentStyle));
            }

            // Get the formatting code
            Formatting format = getFormatting(matcher.group(1).charAt(0));

            if (format != null) {
                // Apply new style
                currentStyle = currentStyle.withFormatting(format);
            }

            lastIndex = matcher.end();
        }

        // Append the remaining text
        if (lastIndex < message.length()) {
            textComponent = textComponent.copy().append(Text.literal(message.substring(lastIndex)).setStyle(currentStyle));
        }

        return textComponent;
    }

    /**
     * Gets the corresponding `Formatting` for a given '&' code.
     *
     * @param code The color code character.
     * @return The corresponding `Formatting` enum.
     */
    private static Formatting getFormatting(char code) {
        return switch (code) {
            case '0' -> Formatting.BLACK;
            case '1' -> Formatting.DARK_BLUE;
            case '2' -> Formatting.DARK_GREEN;
            case '3' -> Formatting.DARK_AQUA;
            case '4' -> Formatting.DARK_RED;
            case '5' -> Formatting.DARK_PURPLE;
            case '6' -> Formatting.GOLD;
            case '7' -> Formatting.GRAY;
            case '8' -> Formatting.DARK_GRAY;
            case '9' -> Formatting.BLUE;
            case 'a' -> Formatting.GREEN;
            case 'b' -> Formatting.AQUA;
            case 'c' -> Formatting.RED;
            case 'd' -> Formatting.LIGHT_PURPLE;
            case 'e' -> Formatting.YELLOW;
            case 'f' -> Formatting.WHITE;
            case 'k' -> Formatting.OBFUSCATED;
            case 'l' -> Formatting.BOLD;
            case 'm' -> Formatting.STRIKETHROUGH;
            case 'n' -> Formatting.UNDERLINE;
            case 'o' -> Formatting.ITALIC;
            case 'r' -> Formatting.RESET;
            default -> null;
        };
    }
}