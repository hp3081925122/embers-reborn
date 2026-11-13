"""Turn the 1.21.1 datagen asset output into static 26.1 assets.

26.1 replaced BlockStateProvider/ItemModelProvider with ModelProvider. The blockstates and models of this mod are
almost all custom (pipes, machines, multiblocks), so instead of re-describing 900 lines of model datagen in the new
API we ship the already-generated JSON as static resources and generate the new client item definitions
(assets/<ns>/items/*.json) here. Run once; re-run is idempotent and never overwrites hand-made files in src/main.
"""
import json, pathlib, shutil, sys

root = pathlib.Path(__file__).resolve().parent.parent
src = pathlib.Path(sys.argv[1]) if len(sys.argv) > 1 else root.parent / "Embers Reborn-1211-neoforge" / "src/generated/resources/assets/embers_reborn"
dst = root / "src/main/resources/assets/embers_reborn"
report = {"copied": 0, "kept_existing": 0, "items": 0, "fluid_containers": 0, "manual": []}


def copy_tree(sub, skip=lambda p: False):
    for f in (src / sub).rglob("*.json"):
        if skip(f):
            continue
        out = dst / sub / f.relative_to(src / sub)
        if out.exists():
            report["kept_existing"] += 1
            continue
        out.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(f, out)
        report["copied"] += 1


def is_fluid_container(f):
    try:
        return json.loads(f.read_text(encoding="utf-8")).get("loader") == "neoforge:fluid_container"
    except Exception:
        return False


copy_tree("blockstates")
copy_tree("models/block")
copy_tree("models/item", skip=is_fluid_container)

bucket_parent = json.loads((dst / "models/item/bucket_fluid.json").read_text(encoding="utf-8"))
items_dir = dst / "items"
items_dir.mkdir(exist_ok=True)


def write_item(name, model):
    out = items_dir / f"{name}.json"
    if out.exists():
        return
    out.write_text(json.dumps({"model": model}, indent=2) + "\n", encoding="utf-8")
    report["items"] += 1


names = {f.stem for f in (src / "models/item").glob("*.json")} | {f.stem for f in (dst / "models/item").glob("*.json")}
for name in sorted(names):
    gen = src / "models/item" / f"{name}.json"
    data = json.loads(gen.read_text(encoding="utf-8")) if gen.exists() else {}
    if data.get("loader") == "neoforge:fluid_container":
        model = {"type": "neoforge:fluid_container", "textures": bucket_parent["textures"], "fluid": data["fluid"]}
        for key in ("flip_gas", "cover_is_mask", "apply_fluid_luminosity"):
            if key in data:
                model[key] = data[key]
        write_item(name, model)
        report["fluid_containers"] += 1
        continue
    if name == "bucket_fluid":
        continue  # parent template only
    if data.get("loader") == "neoforge:separate_transforms":
        report["manual"].append(f"{name}: separate_transforms loader is gone in 26.1; uses the plain model for now")
    if data.get("parent") == "minecraft:item/template_spawn_egg":
        report["manual"].append(f"{name}: 26.1 spawn eggs need their own texture (template_spawn_egg and egg tints were removed)")
    write_item(name, {"type": "minecraft:model", "model": f"embers_reborn:item/{name}"})

print(json.dumps(report, indent=2, ensure_ascii=False))
