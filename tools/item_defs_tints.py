import json, pathlib

items = pathlib.Path(__file__).resolve().parent.parent / "src/main/resources/assets/embers_reborn/items"


def model(name):
    return {"type": "minecraft:model", "model": f"embers_reborn:item/{name}"}


def write(name, m):
    (items / f"{name}.json").write_text(json.dumps({"model": m}, indent=2) + "\n", encoding="utf-8")


for name in ("ember_jar", "ember_cartridge", "ember_bulb"):
    m = model(name)
    m["tints"] = [{"type": "embers_reborn:ember_charge"}]
    write(name, m)

m = model("tyrfing")
m["tints"] = [{"type": "minecraft:constant", "value": -1}, {"type": "embers_reborn:tyrfing_glow"}]
write("tyrfing", m)

write("inflictor_gem", {"type": "minecraft:condition", "property": "embers_reborn:charged",
                        "on_true": model("inflictor_gem_charged"), "on_false": model("inflictor_gem")})
# the 1.21.1 model kept its overrides block; 26.1 ignores it, drop it so the file is clean
p = items.parent / "models/item/inflictor_gem.json"
d = json.loads(p.read_text(encoding="utf-8"))
d.pop("overrides", None)
p.write_text(json.dumps(d, indent=2) + "\n", encoding="utf-8")
print("ok")
