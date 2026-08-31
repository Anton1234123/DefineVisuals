package moscow.definevisuals.utility.inventory.group.impl;

import java.util.List;
import moscow.definevisuals.utility.inventory.group.SlotGroup;
import moscow.definevisuals.utility.inventory.slots.OffhandSlot;

public class OffhandSlotGroup extends SlotGroup<OffhandSlot> {
   public OffhandSlotGroup() {
      super(List.of(new OffhandSlot()));
   }
}
