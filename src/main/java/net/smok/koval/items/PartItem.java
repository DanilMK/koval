package net.smok.koval.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.smok.koval.KovalRegistry;
import net.smok.Values;
import net.smok.koval.Part;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PartItem extends Item {

    private final Identifier partId;

    public PartItem(Settings settings, Identifier partId) {
        super(settings.group(Values.PARTS_CREATIVE_TAB));
        this.partId = partId;
    }

    public Part getPart() {
        return KovalRegistry.PARTS.get(partId);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Part part = getPart();
        if (part != null) part.appendTooltip(tooltip);
    }
}
