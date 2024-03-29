/*
 * Copyright (C) 2019-2023 C4
 *
 * Caelus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Caelus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Caelus.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.caelus;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import top.theillusivec4.caelus.api.CaelusApi;
import top.theillusivec4.caelus.common.CaelusApiImpl;
import top.theillusivec4.caelus.common.CaelusEvents;
import top.theillusivec4.caelus.common.network.CPacketFlightPayload;
import top.theillusivec4.caelus.common.network.CaelusServerPayloadHandler;

@Mod(CaelusConstants.MOD_ID)
public class CaelusNeoForgeMod {

  public CaelusNeoForgeMod(IEventBus eventBus) {
    CaelusApiImpl.setup();
    eventBus.addListener(this::registerPayloadHandler);
    eventBus.addListener(this::attributeSetup);
    NeoForge.EVENT_BUS.addListener(this::livingTick);
  }

  private void attributeSetup(final EntityAttributeModificationEvent evt) {

    for (EntityType<? extends LivingEntity> type : evt.getTypes()) {
      evt.add(type, CaelusApi.getInstance().getFlightAttribute());
    }
  }

  private void registerPayloadHandler(final RegisterPayloadHandlerEvent evt) {
    evt.registrar(CaelusConstants.MOD_ID).play(CPacketFlightPayload.ID, CPacketFlightPayload::new,
        handler -> handler.server(CaelusServerPayloadHandler.getInstance()::handleFlight));
  }

  private void livingTick(final LivingEvent.LivingTickEvent evt) {
    CaelusEvents.livingTick(evt.getEntity());
  }
}
