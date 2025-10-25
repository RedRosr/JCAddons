package redrosr.jcaddons.util;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


public class GuiUtils {
    public static boolean isInventory(Text title, String translatableKey) {
        List<Text> flatText = flattenText(title);

        for (Text t : flatText) {
            if (t.getContent() instanceof TranslatableTextContent translatable) {
                String key = translatable.getKey();
                if (key.startsWith(translatableKey)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Recursively flattens a Text object and all of its siblings into a list.
     */
    public static List<Text> flattenText(Text root) {
        List<Text> result = new ArrayList<>();
        Deque<Text> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Text current = queue.poll();
            result.add(current);
            queue.addAll(current.getSiblings());
        }

        return result;
    }
}