"""LivingTickEvent was removed in 1.21; the AI replaced it with the abstract LivingEvent,
which NeoForge refuses to register. Use EntityTickEvent.Pre filtered to LivingEntity."""
import io
import re
from pathlib import Path

BASE = Path("src/main/java/com/rekindled/embers/augment")
PAT = re.compile(
    r"(public void \w+\()net\.neoforged\.neoforge\.event\.entity\.living\.LivingEvent event\) \{(\r?\n)(\t+)LivingEntity entity = event\.getEntity\(\);"
)
for name in ["CinderJetAugment.java", "ShiftingScalesAugment.java", "WindingGearsAugment.java"]:
    p = BASE / name
    t = io.open(p, encoding="utf-8", newline="").read()
    t, n = PAT.subn(
        r"\1net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event) {\2\3if (!(event.getEntity() instanceof LivingEntity entity)) return;",
        t,
    )
    io.open(p, "w", encoding="utf-8", newline="").write(t)
    print(name, n)
