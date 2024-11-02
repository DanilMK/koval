package net.smok;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.koval.assembler.AssemblerScreenHandler;

public class AssemblerTableScreen extends HandledScreen<AssemblerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(Values.MOD_ID, "textures/gui/assembler_table.png");
    private final int rows;

    private static final int RESULT_CAN_TAKE = 176;
    private static final int RESULT_CANNOT_TAKE = 202;
    private static final int PART_EMPTY = 0;
    private static final int PART_SUITABLE = 18;
    private static final int PART_UNSUITABLE = 36;


    public AssemblerTableScreen(AssemblerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight++;
        this.rows = handler.getRows();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
        this.drawTexture(matrices, i, j + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);

        handler.forResultSlot(slot -> drawResultSlot(matrices, slot.x, slot.y, slot.canTake()));
        handler.foreachPartSlot(slot -> drawPartSlot(matrices, slot.x, slot.y, slot.getStack().isEmpty() ? PART_EMPTY : slot.isSuitable() ? PART_SUITABLE : PART_UNSUITABLE));

    }

    private void drawPartSlot(MatrixStack matrices, int x, int y, int type) {
        this.drawTexture(matrices, this.x + x - 1, this.y + y - 1, 176 + type, 0, 18, 18);
    }

    private void drawResultSlot(MatrixStack matrices, int x, int y, boolean isEnable) {
        if (isEnable) drawTexture(matrices, this.x + x - 5, this.y + y - 5, RESULT_CAN_TAKE, 18, 26, 26);
        else drawTexture(matrices, this.x + x - 5, this.y + y - 5, RESULT_CANNOT_TAKE, 18, 26, 26);

    }



    // todo render slot interact
}
