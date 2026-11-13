"""Turn blockstate variants that point at a pipe loader model into inline embers_reborn:pipe custom models,
then delete the old loader model JSONs (26.1 has no geometry loader for them)."""
import json, pathlib

assets = pathlib.Path(__file__).resolve().parent.parent / "src/main/resources/assets/embers_reborn"
models = assets / "models/block"

pipes = {}
for name in ["item_pipe", "fluid_pipe", "item_extractor", "fluid_extractor", "mini_boiler_pipes"]:
    data = json.loads((models / (name + ".json")).read_text(encoding="utf-8"))
    assert data.get("loader") == "embers_reborn:pipe", name
    pipes["embers_reborn:block/" + name] = {k: data[k]["parent"] for k in ["center", "connection", "connection2", "end", "end2"]}

changed = 0
for bs in (assets / "blockstates").glob("*.json"):
    data = json.loads(bs.read_text(encoding="utf-8"))
    hit = False
    for key, variant in data.get("variants", {}).items():
        if isinstance(variant, dict) and variant.get("model") in pipes:
            p = pipes[variant["model"]]
            center = {"model": p["center"]}
            for rot in ("x", "y", "uvlock"):
                if rot in variant:
                    center[rot] = variant[rot]
            data["variants"][key] = {"type": "embers_reborn:pipe", "center": center,
                                     "connection": p["connection"], "connection2": p["connection2"],
                                     "end": p["end"], "end2": p["end2"]}
            hit = True
    if hit:
        bs.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
        changed += 1
        print("blockstate", bs.name)

for name in pipes:
    (models / (name.split("/")[-1] + ".json")).unlink()
print("changed", changed, "deleted", len(pipes))
