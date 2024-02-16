package io.github.winnpixie.wpsmp.playerdata;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.List;

public class PlayerData {
    private ArmorStand seat;

    public ArmorStand getSeat() {
        return seat;
    }

    public void setSeat(ArmorStand seat) {
        if (this.seat != null) {
            clearPassengers(this.seat);

            this.seat.remove();
        }

        this.seat = seat;
    }

    private void clearPassengers(Entity entity) {
        List<Entity> passengers = entity.getPassengers();
        for (int i = passengers.size(); i > 0; i--) {
            Entity passenger = passengers.get(i - 1);
            clearPassengers(passenger);
            entity.removePassenger(entity);
        }
    }
}
