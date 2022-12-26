package com.hexagram2021.infiltrators.client.screen;

import com.hexagram2021.infiltrators.common.crafting.AnalystTableMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

@OnlyIn(Dist.CLIENT)
public class AnalystTableScreen extends AbstractContainerScreen<AnalystTableMenu> {
	private static final ResourceLocation BG_LOCATION = new ResourceLocation(MODID, "textures/gui/container/analyst_table.png");
	
	public AnalystTableScreen(AnalystTableMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
		--this.titleLabelY;
	}
	
	@Override
	public void render(@NotNull PoseStack transform, int x, int y, float partialTicks) {
		super.render(transform, x, y, partialTicks);
		this.renderTooltip(transform, x, y);
	}
	
	@Override
	protected void renderBg(@NotNull PoseStack transform, float partialTicks, int x, int y) {
		this.renderBackground(transform);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BG_LOCATION);
		int i = this.leftPos;
		int j = this.topPos;
		this.blit(transform, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}
}
