"""Build the CurseForge icon: original Embers logo with "REBORN" in pixel letters underneath.

Works on the native 99x99 logo grid, then upscales with nearest neighbour so pixels stay crisp.
"""
from collections import Counter
from pathlib import Path

from PIL import Image

HERE = Path(__file__).resolve().parent
SRC = HERE.parent.parent / "_source-1201" / "images" / "logo.png"
OUT = HERE / "cf-project-icon.png"

GRID = 128          # working canvas, in logo pixels
SCALE = 4           # 128 * 4 = 512 px output

# 5x7 glyphs, '#' = filled
FONT = {
    "R": ["####.", "#...#", "#...#", "####.", "#.#..", "#..#.", "#...#"],
    "E": ["#####", "#....", "#....", "####.", "#....", "#....", "#####"],
    "B": ["####.", "#...#", "#...#", "####.", "#...#", "#...#", "####."],
    "O": [".###.", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."],
    "N": ["#...#", "##..#", "#.#.#", "#..##", "#...#", "#...#", "#...#"],
}


def main():
    logo = Image.open(SRC).convert("RGBA")
    bg = Counter(logo.getdata()).most_common(1)[0][0]

    canvas = Image.new("RGBA", (GRID, GRID), bg)
    lx = (GRID - logo.width) // 2
    canvas.paste(logo, (lx, 0))

    # gold ramp sampled to match the EMBERS lettering: light top, darker bottom, brown outline
    ramp = [(255, 226, 150, 255), (246, 196, 104, 255), (226, 160, 72, 255), (196, 124, 48, 255)]
    outline = (74, 44, 20, 255)

    text = "REBORN"
    cell = 2                      # each font dot = 2x2 logo pixels
    gw, gh, gap = 5 * cell, 7 * cell, 2 * cell
    width = len(text) * gw + (len(text) - 1) * gap
    ox = (GRID - width) // 2
    oy = logo.height + (GRID - logo.height - gh) // 2

    mask = set()
    for i, ch in enumerate(text):
        for r, row in enumerate(FONT[ch]):
            for c, v in enumerate(row):
                if v != "#":
                    continue
                for dy in range(cell):
                    for dx in range(cell):
                        mask.add((ox + i * (gw + gap) + c * cell + dx, oy + r * cell + dy))

    px = canvas.load()
    for x, y in mask:
        for ddx in (-1, 0, 1):
            for ddy in (-1, 0, 1, 2):     # extra row below = drop shadow
                p = (x + ddx, y + ddy)
                if p not in mask and 0 <= p[0] < GRID and 0 <= p[1] < GRID:
                    px[p] = outline
    for x, y in mask:
        band = min(len(ramp) - 1, (y - oy) * len(ramp) // gh)
        px[x, y] = ramp[band]

    canvas.resize((GRID * SCALE, GRID * SCALE), Image.NEAREST).save(OUT)
    print(OUT, canvas.size, "->", GRID * SCALE)


if __name__ == "__main__":
    main()
