"""Forge Phase.START handlers -> NeoForge *.Pre events (abstract base classes cannot be listened to)."""
import io
from pathlib import Path

J = Path("src/main/java/com/rekindled/embers")
EDITS = {
    "augment/CasterOrbAugment.java": [("(ServerTickEvent event)", "(ServerTickEvent.Pre event)"), ("(ClientTickEvent.Post event)", "(ClientTickEvent.Pre event)")],
    "augment/ResonatingBellAugment.java": [("(ServerTickEvent event)", "(ServerTickEvent.Pre event)")],
    "augment/ShiftingScalesAugment.java": [("(ServerTickEvent event)", "(ServerTickEvent.Pre event)"), ("(ClientTickEvent.Post event)", "(ClientTickEvent.Pre event)")],
    "EmbersClientEvents.java": [("onClientTick(ClientTickEvent.Post event)", "onClientTick(ClientTickEvent.Pre event)")],
    "EmbersEvents.java": [("onServerTick(LevelTickEvent event)", "onServerTick(LevelTickEvent.Pre event)")],
}
for rel, reps in EDITS.items():
    p = J / rel
    t = io.open(p, encoding="utf-8", newline="").read()
    for a, b in reps:
        assert t.count(a) == 1, (rel, a, t.count(a))
        t = t.replace(a, b)
    io.open(p, "w", encoding="utf-8", newline="").write(t)
    print("ok", rel)
